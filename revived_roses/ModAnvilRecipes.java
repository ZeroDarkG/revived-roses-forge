package com.dalterdile.revived_roses;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AnvilUpdateEvent;

public final class ModAnvilRecipes {

    private ModAnvilRecipes() {}

    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (left.isEmpty() || right.isEmpty()) return;

        if (left.is(Items.IRON_INGOT) && right.is(Items.REDSTONE)) {
            int crafts = Math.min(left.getCount(), right.getCount());
            if (crafts <= 0) return;
            ItemStack result = new ItemStack(ModBlocks.GEAR.get().asItem(), 4 * crafts);
            event.setOutput(result);
            event.setMaterialCost(crafts);
            event.setCost(1);
        }

        if (!right.is(ModItems.RUBY.get())) return;

        if (left.is(Items.DIAMOND_HORSE_ARMOR)) {
            event.setOutput(new ItemStack(ModItems.RUBY_HORSE_ARMOR.get()));
            event.setMaterialCost(1);
            event.setCost(10); // también sin XP
            return;
        }

        if (left.is(Items.DIAMOND_NAUTILUS_ARMOR)) {
            event.setOutput(new ItemStack(ModItems.RUBY_NAUTILUS_ARMOR.get()));
            event.setMaterialCost(1);
            event.setCost(10);
        }
    }
}