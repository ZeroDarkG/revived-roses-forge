package com.dalterdile.revived_roses;

import com.dalterdile.revived_roses.client.renderer.ScarecrowModel;
import com.dalterdile.revived_roses.client.renderer.ScarecrowRenderer;
import com.dalterdile.revived_roses.empty_flowers.FlowerShearHandler;
import com.dalterdile.revived_roses.registry.ModEntities;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(RevivedRoses.MODID)
public class RevivedRoses {
    public static final String MODID = "revived_roses";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final TagKey<Block> FLOWERS_BLOCK =
            TagKey.create(Registries.BLOCK, Identifier.parse("minecraft:flowers"));
    private static final TagKey<Item> FLOWERS_ITEM =
            TagKey.create(Registries.ITEM, Identifier.parse("minecraft:flowers"));

    public RevivedRoses(FMLJavaModLoadingContext context) {
        var modBus = context.getModBusGroup();

        // Registries
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModEntities.ENTITY_TYPES.register(modBus);
        ModSounds.SOUND_EVENTS.register(modBus);

        PlayerInteractEvent.RightClickBlock.BUS.addListener(FlowerShearHandler::onRightClickBlock);

        // MOD bus listeners (Usando la forma que ya tienes para los otros)
        AddPackFindersEvent.getBus(modBus).addListener(ModResourcePacks::addPackFinders);
        BuildCreativeModeTabContentsEvent.getBus(modBus).addListener(RevivedRoses::addToCreativeTab);
        FMLCommonSetupEvent.getBus(modBus).addListener(RevivedRoses::commonSetup);

        // CORRECCIÓN AQUÍ: Para los atributos usamos el bus del evento específico
        net.minecraftforge.event.entity.EntityAttributeCreationEvent.getBus(modBus).addListener(RevivedRoses::registerAttributes);

        // Forge bus listeners (globales)
        AnvilUpdateEvent.BUS.addListener(ModAnvilRecipes::onAnvilUpdate);
        ServerStartingEvent.BUS.addListener(RevivedRoses::onServerStarting);

        // AÑADE ESTO: Registro del Renderer (Solo para el cliente)
        net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers.getBus(modBus)
                .addListener(RevivedRoses::registerRenderers);

        // AÑADE ESTO: Registro de las capas del modelo (Layers)
        net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions.getBus(modBus)
                .addListener(RevivedRoses::registerLayerDefinitions);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModBlocks.registerFlowerPots();

            ComposterBlock.COMPOSTABLES.put(ModItems.ROSE.get(), 0.65f);
            ComposterBlock.COMPOSTABLES.put(ModItems.BLUE_ROSE.get(), 0.65f);
            ComposterBlock.COMPOSTABLES.put(ModItems.BLUE_ROSE_BUSH.get(), 0.85f);
            ComposterBlock.COMPOSTABLES.put(ModItems.SMALL_PEONY.get(), 0.65f);
        });
    }

    private static void onServerStarting(ServerStartingEvent event) {
        logBlockTag("ROSE_BLOCK", ModBlocks.ROSE.get());
        logBlockTag("BLUE_ROSE_BLOCK", ModBlocks.BLUE_ROSE.get());
        logBlockTag("BLUE_ROSE_BUSH_BLOCK", ModBlocks.BLUE_ROSE_BUSH.get());
        logBlockTag("SMALL_PEONY_BLOCK", ModBlocks.SMALL_PEONY.get());

        logItemTag("ROSE_ITEM", ModItems.ROSE.get());
        logItemTag("BLUE_ROSE_ITEM", ModItems.BLUE_ROSE.get());
        logItemTag("BLUE_ROSE_BUSH_ITEM", ModItems.BLUE_ROSE_BUSH.get());
        logItemTag("SMALL_PEONY_ITEM", ModItems.SMALL_PEONY.get());
    }

    private static void logBlockTag(String name, Block block) {
        Holder<Block> holder = BuiltInRegistries.BLOCK.wrapAsHolder(block);
        LOGGER.info("[BeeTagDebug] {} in #minecraft:flowers = {}", name, holder.is(FLOWERS_BLOCK));
    }

    private static void logItemTag(String name, Item item) {
        Holder<Item> holder = BuiltInRegistries.ITEM.wrapAsHolder(item);
        LOGGER.info("[BeeTagDebug] {} in #minecraft:flowers = {}", name, holder.is(FLOWERS_ITEM));
    }

    private static void registerAttributes(net.minecraftforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(ModEntities.SCARECROW.get(), net.minecraft.world.entity.Mob.createMobAttributes()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 20.0D)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.25D)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 4.0D) // <-- FALTA ESTO
                .add(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE, 40.0D) // Importante para la IA
                .build());
    }

    private static void registerRenderers(net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        // Vincula la entidad con su Renderer
        event.registerEntityRenderer(ModEntities.SCARECROW.get(), ScarecrowRenderer::new);
    }

    private static void registerLayerDefinitions(net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions event) {
        // Registra la "forma" del modelo para que el juego sepa cómo construirlo
        event.registerLayerDefinition(ScarecrowModel.LAYER_LOCATION, ScarecrowModel::createBodyLayer);
    }

    private static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {

        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            var entries = event.getEntries();

            entries.putAfter(new ItemStack(Items.POPPY),
                    new ItemStack(ModItems.ROSE.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putAfter(new ItemStack(Items.BLUE_ORCHID),
                    new ItemStack(ModItems.BLUE_ROSE.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putBefore(new ItemStack(Items.PEONY),
                    new ItemStack(ModItems.BLUE_ROSE_BUSH.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putAfter(new ItemStack(Items.ALLIUM),
                    new ItemStack(ModItems.SMALL_PEONY.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putBefore(new ItemStack(Items.EMERALD_ORE),
                    new ItemStack(ModItems.RUBY_ORE.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putAfter(new ItemStack(ModItems.RUBY_ORE.get()),
                    new ItemStack(ModItems.DEEPSLATE_RUBY_ORE.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            return;
        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.getEntries().putBefore(new ItemStack(Items.EMERALD),
                    new ItemStack(ModItems.RUBY.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.getEntries().putAfter(new ItemStack(Items.EMERALD_BLOCK),
                    new ItemStack(ModItems.RUBY_BLOCK.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.getEntries().putAfter(new ItemStack(Items.REDSTONE),
                    new ItemStack(ModItems.GEAR.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            var entries = event.getEntries();

            entries.putAfter(new ItemStack(Items.DIAMOND_SPEAR),
                    new ItemStack(ModItems.RUBY_SPEAR.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putBefore(new ItemStack(Items.NETHERITE_HELMET),
                    new ItemStack(ModItems.RUBY_HELMET.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putAfter(new ItemStack(ModItems.RUBY_HELMET.get()),
                    new ItemStack(ModItems.RUBY_CHESTPLATE.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putAfter(new ItemStack(ModItems.RUBY_CHESTPLATE.get()),
                    new ItemStack(ModItems.RUBY_LEGGINGS.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putAfter(new ItemStack(ModItems.RUBY_LEGGINGS.get()),
                    new ItemStack(ModItems.RUBY_BOOTS.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putBefore(new ItemStack(Items.NETHERITE_SWORD),
                    new ItemStack(ModItems.RUBY_SWORD.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putBefore(new ItemStack(Items.NETHERITE_AXE),
                    new ItemStack(ModItems.RUBY_AXE.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putAfter(new ItemStack(Items.DIAMOND_HORSE_ARMOR),
                    new ItemStack(ModItems.RUBY_HORSE_ARMOR.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putAfter(new ItemStack(Items.DIAMOND_NAUTILUS_ARMOR),
                    new ItemStack(ModItems.RUBY_NAUTILUS_ARMOR.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            var entries = event.getEntries();

            entries.putBefore(new ItemStack(Items.NETHERITE_SHOVEL),
                    new ItemStack(ModItems.RUBY_SHOVEL.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putAfter(new ItemStack(ModItems.RUBY_SHOVEL.get()),
                    new ItemStack(ModItems.RUBY_PICKAXE.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putAfter(new ItemStack(ModItems.RUBY_PICKAXE.get()),
                    new ItemStack(ModItems.RUBY_AXE.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putAfter(new ItemStack(ModItems.RUBY_AXE.get()),
                    new ItemStack(ModItems.RUBY_HOE.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

        }

        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            var entries = event.getEntries();

            entries.putAfter(new ItemStack(Items.SHULKER_SPAWN_EGG),
                    new ItemStack(ModItems.SCARECROW.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

    }
}