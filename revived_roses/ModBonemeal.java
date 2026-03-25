package com.dalterdile.revived_roses;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "revived_roses", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModBonemeal {

    @SubscribeEvent
    public static boolean onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = event.getItemStack();

        if (!stack.is(Items.BONE_MEAL) || event.getHand() != InteractionHand.MAIN_HAND) return false;

        boolean changed = false;

        // Rosa -> rosal vanilla
        if (state.is(ModBlocks.ROSE.get())) {
            changed = tryConvertToTall(level, pos, Blocks.ROSE_BUSH);

            // Rosa azul -> rosal azul
        } else if (state.is(ModBlocks.BLUE_ROSE.get())) {
            changed = tryConvertToTall(level, pos, ModBlocks.BLUE_ROSE_BUSH.get());

            // Peonía pequeña -> Peonía vanilla (alta)
        } else if (state.is(ModBlocks.SMALL_PEONY.get())) {
            changed = tryConvertToTall(level, pos, Blocks.PEONY);
        }

        if (!changed) return false;

        Player player = event.getEntity();

        // Animación mano
        player.swing(event.getHand(), true);

        // Partículas verdes vanilla
        BoneMealItem.addGrowthParticles(level, pos, 15);

        // Sonido/efecto bonemeal (server)
        if (!level.isClientSide()) {
            level.levelEvent(1505, pos, 0);
        }

        // Consumir bone meal
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        event.setCancellationResult(level.isClientSide() ? InteractionResult.CONSUME : InteractionResult.SUCCESS);
        return true;
    }

    @SubscribeEvent
    public static void onBonemealOnGrass(BonemealEvent event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();

        if (level.isClientSide()) return;
        if (!level.getBlockState(pos).is(Blocks.GRASS_BLOCK)) return;

        BlockPos abovePos = pos.above();
        if (!level.isEmptyBlock(abovePos)) return;

        double chance = level.getRandom().nextDouble();

        // Reparto sugerido (puedes ajustarlo):
        // 20% Rose, 20% Blue Rose, 10% Small Peony
        if (chance < 0.20) {
            level.setBlock(abovePos, ModBlocks.ROSE.get().defaultBlockState(), 3);
            level.levelEvent(1505, abovePos, 0);
            event.setResult(Result.ALLOW);

        } else if (chance < 0.40) {
            level.setBlock(abovePos, ModBlocks.BLUE_ROSE.get().defaultBlockState(), 3);
            level.levelEvent(1505, abovePos, 0);
            event.setResult(Result.ALLOW);

        } else if (chance < 0.50) {
            level.setBlock(abovePos, ModBlocks.SMALL_PEONY.get().defaultBlockState(), 3);
            level.levelEvent(1505, abovePos, 0);
            event.setResult(Result.ALLOW);

        } else {
            event.setResult(Result.DEFAULT);
        }
    }

    private static boolean tryConvertToTall(Level level, BlockPos pos, Block tallBlock) {
        BlockPos above = pos.above();
        if (!level.isEmptyBlock(above)) return false;

        level.setBlock(pos, tallBlock.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(above, tallBlock.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER), 3);
        return true;
    }
}