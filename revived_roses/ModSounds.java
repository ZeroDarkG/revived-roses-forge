package com.dalterdile.revived_roses;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    // Definimos el registro para sonidos
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RevivedRoses.MODID);

    // Registro del sonido de daño (Hurt)
    public static final RegistryObject<SoundEvent> SCARECROW_HURT = registerSoundEvent("scarecrow_hurt");

    // Registro del sonido de muerte (Dead)
    public static final RegistryObject<SoundEvent> SCARECROW_DEAD = registerSoundEvent("scarecrow_dead");

    public static final RegistryObject<SoundEvent> SCARECROW_LAUGH = registerSoundEvent("scarecrow_laugh");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () ->
                SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(RevivedRoses.MODID, name)));
    }

    private ModSounds() {} // Constructor privado para evitar instanciación
}