package com.dalterdile.revived_roses.empty_flowers;

import com.dalterdile.revived_roses.ModItems;
import com.dalterdile.revived_roses.RevivedRoses;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RevivedRoses.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FlowerShearHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {

        Level level = event.getLevel();
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        ItemStack stack = event.getItemStack();

        if (level.isClientSide()) return;
        if (!(stack.getItem() instanceof ShearsItem)) return;

        BlockState state = level.getBlockState(pos);

        boolean isPeony = state.getBlock() == Blocks.PEONY;
        boolean isRoseBush = state.getBlock() == Blocks.ROSE_BUSH;
        boolean isBlueRoseBush = state.getBlock() == com.dalterdile.revived_roses.ModBlocks.BLUE_ROSE_BUSH.get();

        if (!isPeony && !isRoseBush && !isBlueRoseBush) return;

        // Asegurar parte inferior
        if (state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.below();
        }

        // Romper planta completa
        level.destroyBlock(pos, false);
        level.destroyBlock(pos.above(), false);

        // Drops
        if (isPeony) {
            Block.popResource(level, pos, new ItemStack(ModItems.SMALL_PEONY.get(), 4));
        }

        if (isRoseBush) {
            Block.popResource(level, pos, new ItemStack(ModItems.ROSE.get(), 4));
        }

        if (isBlueRoseBush) {
            Block.popResource(level, pos, new ItemStack(ModItems.BLUE_ROSE.get(), 4));
        }

        // Daño tijeras
        stack.hurtAndBreak(1, player, event.getHand());
        player.swing(event.getHand(), true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}