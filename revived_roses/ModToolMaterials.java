package com.dalterdile.revived_roses;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;

public final class ModToolMaterials {
    public static final ToolMaterial RUBY = new ToolMaterial(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            1796,
            8.5f,
            3.5f,
            12,
            ModTags.Items.RUBY_REPAIR
    );

    private ModToolMaterials() {}
}