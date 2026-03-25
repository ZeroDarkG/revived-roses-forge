package com.dalterdile.revived_roses;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class RubySpearItem extends Item { // <- temporal hasta que me pases la clase real del spear

    public static final double RUBY_ATTACK_DAMAGE = 9.0D;  // 4.5 corazones
    public static final double RUBY_ATTACK_SPEED  = -2.9D;

    public RubySpearItem(Properties props) {
        super(props);
    }

    public static ItemAttributeModifiers createRubyAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                Item.BASE_ATTACK_DAMAGE_ID,
                                RUBY_ATTACK_DAMAGE - 1.0D,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                Item.BASE_ATTACK_SPEED_ID,
                                RUBY_ATTACK_SPEED,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }
}