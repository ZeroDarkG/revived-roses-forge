package com.dalterdile.revived_roses.registry;

import com.dalterdile.revived_roses.RevivedRoses;
import com.dalterdile.revived_roses.scarecrow.ScarecrowEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RevivedRoses.MODID);

    public static final RegistryObject<EntityType<ScarecrowEntity>> SCARECROW =
            ENTITY_TYPES.register("scarecrow",
                    () -> EntityType.Builder.of(ScarecrowEntity::new, MobCategory.MISC)
                            .sized(0.6F, 1.9F)
                            .clientTrackingRange(10)
                            // Usamos Identifier.fromNamespaceAndPath que es lo estándar ahora
                            .build(ResourceKey.create(Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(RevivedRoses.MODID, "scarecrow"))));
}