package com.dalterdile.revived_roses;

import com.dalterdile.revived_roses.scarecrow.ScarecrowItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RevivedRoses.MODID);

    private static Identifier id(String path) {
        return Identifier.parse(RevivedRoses.MODID + ":" + path);
    }

    private static Item.Properties props(String name) {
        return new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id(name)));
    }

    public static final RegistryObject<Item> ROSE =
            ITEMS.register("rose", () ->
                    new BlockItem(ModBlocks.ROSE.get(), props("rose")));

    public static final RegistryObject<Item> BLUE_ROSE =
            ITEMS.register("blue_rose", () ->
                    new BlockItem(ModBlocks.BLUE_ROSE.get(), props("blue_rose")));

    public static final RegistryObject<Item> BLUE_ROSE_BUSH =
            ITEMS.register("blue_rose_bush", () ->
                    new BlockItem(ModBlocks.BLUE_ROSE_BUSH.get(), props("blue_rose_bush")));

    public static final RegistryObject<Item> SMALL_PEONY =
            ITEMS.register("small_peony", () ->
                    new BlockItem(ModBlocks.SMALL_PEONY.get(), props("small_peony")));

    public static final RegistryObject<Item> RUBY =
            ITEMS.register("ruby", () ->
                    new Item(props("ruby")));

    public static final RegistryObject<Item> RUBY_ORE =
            ITEMS.register("ruby_ore", () ->
                    new BlockItem(ModBlocks.RUBY_ORE.get(), props("ruby_ore")));

    public static final RegistryObject<Item> DEEPSLATE_RUBY_ORE =
            ITEMS.register("deepslate_ruby_ore", () ->
                    new BlockItem(ModBlocks.DEEPSLATE_RUBY_ORE.get(), props("deepslate_ruby_ore")));

    public static final RegistryObject<Item> RUBY_BLOCK =
            ITEMS.register("ruby_block", () ->
                    new BlockItem(ModBlocks.RUBY_BLOCK.get(), props("ruby_block")));

    public static final RegistryObject<Item> RUBY_HELMET =
            ITEMS.register("ruby_helmet", () ->
                    new Item(props("ruby_helmet")
                            .component(DataComponents.ENCHANTABLE, new Enchantable(12))
                            .humanoidArmor(ModArmorMaterials.RUBY, ArmorType.HELMET))
            );

    public static final RegistryObject<Item> RUBY_CHESTPLATE =
            ITEMS.register("ruby_chestplate", () ->
                    new Item(props("ruby_chestplate")
                            .component(DataComponents.ENCHANTABLE, new Enchantable(12))
                            .humanoidArmor(ModArmorMaterials.RUBY, ArmorType.CHESTPLATE))
            );

    public static final RegistryObject<Item> RUBY_LEGGINGS =
            ITEMS.register("ruby_leggings", () ->
                    new Item(props("ruby_leggings")
                            .component(DataComponents.ENCHANTABLE, new Enchantable(12))
                            .humanoidArmor(ModArmorMaterials.RUBY, ArmorType.LEGGINGS))
            );

    public static final RegistryObject<Item> RUBY_BOOTS =
            ITEMS.register("ruby_boots", () ->
                    new Item(props("ruby_boots")
                            .component(DataComponents.ENCHANTABLE, new Enchantable(12))
                            .humanoidArmor(ModArmorMaterials.RUBY, ArmorType.BOOTS))
            );

    public static final RegistryObject<Item> RUBY_SWORD =
            ITEMS.register("ruby_sword", () ->
                    new Item(props("ruby_sword")
                            .component(DataComponents.ENCHANTABLE, new Enchantable(12))
                            .sword(ModToolMaterials.RUBY, 3.0f, -2.4f))
            );

    public static final RegistryObject<Item> RUBY_PICKAXE =
            ITEMS.register("ruby_pickaxe", () ->
                    new Item(props("ruby_pickaxe")
                            .component(DataComponents.ENCHANTABLE, new Enchantable(12))
                            .pickaxe(ModToolMaterials.RUBY, 1.0f, -2.8f))
            );

    public static final RegistryObject<Item> RUBY_AXE =
            ITEMS.register("ruby_axe", () ->
                    new Item(props("ruby_axe")
                            .component(DataComponents.ENCHANTABLE, new Enchantable(12))
                            .axe(ModToolMaterials.RUBY, 5.0f, -3.0f))
            );

    public static final RegistryObject<Item> RUBY_SHOVEL =
            ITEMS.register("ruby_shovel", () ->
                    new Item(props("ruby_shovel")
                            .component(DataComponents.ENCHANTABLE, new Enchantable(12))
                            .shovel(ModToolMaterials.RUBY, 1.5f, -3.0f))
            );

    public static final RegistryObject<Item> RUBY_HOE =
            ITEMS.register("ruby_hoe", () ->
                    new HoeItem(
                            ModToolMaterials.RUBY,
                            -3.5f,
                            0.0f,
                            props("ruby_hoe")
                                    .component(DataComponents.ENCHANTABLE, new Enchantable(12))
                    )
            );

    public static final RegistryObject<Item> RUBY_SPEAR =
            ITEMS.register("ruby_spear", () ->
                    new Item(props("ruby_spear")
                            .component(DataComponents.ENCHANTABLE, new Enchantable(12))
                            .stacksTo(1)
                            .durability(1796)
                            .spear(
                                    ModToolMaterials.RUBY,
                                    1.05F, 1.075F, 0.5F, 3.0F,
                                    8.25F,
                                    6.5F, 5.1F, 10.0F, 4.6F
                            ))
            );

    public static final RegistryObject<Item> RUBY_HORSE_ARMOR =
            ITEMS.register("ruby_horse_armor", () ->
                    new Item(props("ruby_horse_armor").horseArmor(ModArmorMaterials.RUBY))
            );

    public static final RegistryObject<Item> RUBY_NAUTILUS_ARMOR =
            ITEMS.register("ruby_nautilus_armor", () ->
                    new Item(props("ruby_nautilus_armor").nautilusArmor(ModArmorMaterials.RUBY))
            );

    public static final RegistryObject<Item> GEAR =
            ITEMS.register("gear", () ->
                    new BlockItem(ModBlocks.GEAR.get(), props("gear")));

    public static final RegistryObject<Item> SCARECROW = ITEMS.register("scarecrow",
            () -> new ScarecrowItem(props("scarecrow")));

    private ModItems() {}
}