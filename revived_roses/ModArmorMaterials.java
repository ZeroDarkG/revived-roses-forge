package com.dalterdile.revived_roses;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.sounds.SoundEvents;

import java.util.EnumMap;
import java.util.Map;

public final class ModArmorMaterials {

    private static final TagKey<Item> REPAIR = ModTags.Items.RUBY_REPAIR;

    private static final Map<ArmorType, Integer> DEFENSE = new EnumMap<>(ArmorType.class);
    static {
        DEFENSE.put(ArmorType.HELMET, 3);
        DEFENSE.put(ArmorType.CHESTPLATE, 8);
        DEFENSE.put(ArmorType.LEGGINGS, 6);
        DEFENSE.put(ArmorType.BOOTS, 3);

        // IMPORTANTE: caballo / wolf / etc
        DEFENSE.put(ArmorType.BODY, 15);
    }

    // AssetId con namespace del mod (evita minecraft: y evita el crash del ':')
    private static final ResourceKey<EquipmentAsset> RUBY_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(RevivedRoses.MODID, "ruby"));

    public static final ArmorMaterial RUBY = new ArmorMaterial(
            35,
            DEFENSE,
            12,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            2.5F,
            0.05F,
            REPAIR,
            RUBY_ASSET
    );

    private ModArmorMaterials() {}
}