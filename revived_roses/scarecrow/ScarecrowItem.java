package com.dalterdile.revived_roses.scarecrow;

import com.dalterdile.revived_roses.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack; // Import necesario para isFoil
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ScarecrowItem extends Item {

    public ScarecrowItem(Properties props) {
        super(props);
    }

    // Este método hace que el ítem brille como si estuviera encantado
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();

        // Comprobación de seguridad para el lado del servidor
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        ServerLevel server = (ServerLevel) level;
        BlockPos spawnPos = ctx.getClickedPos().relative(ctx.getClickedFace());

        // Invocación de la entidad Scarecrow
        var entity = ModEntities.SCARECROW.get().spawn(
                server,
                ctx.getItemInHand(),
                ctx.getPlayer(),
                spawnPos,
                EntitySpawnReason.SPAWN_ITEM_USE,
                true,
                false
        );

        // Si falló el spawn, no consumimos el ítem
        if (entity == null) return InteractionResult.FAIL;

        // Consumir el ítem si no está en modo creativo
        if (ctx.getPlayer() == null || !ctx.getPlayer().getAbilities().instabuild) {
            ctx.getItemInHand().shrink(1);
        }

        return InteractionResult.CONSUME;
    }
}