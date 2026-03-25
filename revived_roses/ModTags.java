package com.dalterdile.revived_roses;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier; // <- la tuya
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModTags {
    public static final class Items {
        public static final TagKey<Item> RUBY_REPAIR = TagKey.create(
                Registries.ITEM,
                Identifier.parse(RevivedRoses.MODID + ":ruby_repair")
        );
    }

    private ModTags() {}
}