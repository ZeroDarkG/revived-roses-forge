package com.dalterdile.revived_roses;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RevivedRoses.MODID);

    private static Identifier id(String path) {
        return Identifier.parse(RevivedRoses.MODID + ":" + path);
    }

    private static BlockBehaviour.Properties props(String name, Block copyFrom) {
        return BlockBehaviour.Properties.ofFullCopy(copyFrom)
                .setId(ResourceKey.create(Registries.BLOCK, id(name)));
    }

    public static final RegistryObject<Block> ROSE =
            BLOCKS.register("rose", () -> new FlowerBlock(
                    ((FlowerBlock) Blocks.POPPY).getSuspiciousEffects(),
                    props("rose", Blocks.POPPY)
            ));

    public static final RegistryObject<Block> BLUE_ROSE =
            BLOCKS.register("blue_rose", () -> new FlowerBlock(
                    ((FlowerBlock) Blocks.POPPY).getSuspiciousEffects(),
                    props("blue_rose", Blocks.POPPY)
            ));

    public static final RegistryObject<Block> BLUE_ROSE_BUSH =
            BLOCKS.register("blue_rose_bush", () -> new TallFlowerBlock(
                    props("blue_rose_bush", Blocks.ROSE_BUSH)
            ));

    public static final RegistryObject<Block> SMALL_PEONY =
            BLOCKS.register("small_peony", () -> new FlowerBlock(
                    ((FlowerBlock) Blocks.POPPY).getSuspiciousEffects(),
                    props("small_peony", Blocks.POPPY)
            ));

    public static final RegistryObject<Block> POTTED_ROSE =
            BLOCKS.register("potted_rose", () -> new FlowerPotBlock(
                    () -> (FlowerPotBlock) Blocks.FLOWER_POT,
                    ROSE::get,
                    props("potted_rose", Blocks.POTTED_POPPY)
            ));

    public static final RegistryObject<Block> POTTED_BLUE_ROSE =
            BLOCKS.register("potted_blue_rose", () -> new FlowerPotBlock(
                    () -> (FlowerPotBlock) Blocks.FLOWER_POT,
                    BLUE_ROSE::get,
                    props("potted_blue_rose", Blocks.POTTED_POPPY)
            ));

    public static final RegistryObject<Block> POTTED_SMALL_PEONY =
            BLOCKS.register("potted_small_peony", () -> new FlowerPotBlock(
                    () -> (FlowerPotBlock) Blocks.FLOWER_POT,
                    SMALL_PEONY::get,
                    props("potted_small_peony", Blocks.POTTED_POPPY)
            ));

    public static final RegistryObject<Block> RUBY_ORE =
            BLOCKS.register("ruby_ore", () -> new DropExperienceBlock(
                    UniformInt.of(3, 7),
                    props("ruby_ore", Blocks.EMERALD_ORE)
            ));

    public static final RegistryObject<Block> DEEPSLATE_RUBY_ORE =
            BLOCKS.register("deepslate_ruby_ore", () -> new DropExperienceBlock(
                    UniformInt.of(3, 7),
                    props("deepslate_ruby_ore", Blocks.DEEPSLATE_EMERALD_ORE)
            ));

    public static final RegistryObject<Block> RUBY_BLOCK =
            BLOCKS.register("ruby_block", () -> new Block(
                    props("ruby_block", Blocks.EMERALD_BLOCK)
            ));

    public static final RegistryObject<Block> GEAR =
            BLOCKS.register("gear", () -> new GearBlock(
                    props("gear", Blocks.IRON_BLOCK)
                            .strength(0F)
                            .noOcclusion()
            ));

    public static void registerFlowerPots() {
        FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;

        pot.addPlant(id("rose"), POTTED_ROSE);
        pot.addPlant(id("blue_rose"), POTTED_BLUE_ROSE);
        pot.addPlant(id("small_peony"), POTTED_SMALL_PEONY);
    }

    private ModBlocks() {}
}