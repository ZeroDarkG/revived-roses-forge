package com.dalterdile.revived_roses.scarecrow;

import com.dalterdile.revived_roses.ModSounds;
import com.dalterdile.revived_roses.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class ScarecrowEntity extends PathfinderMob {
    private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(ScarecrowEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDimensions STATUE_DIMENSIONS = EntityDimensions.scalable(0.45f, 1.55f);
    private static final EntityDimensions ANGRY_DIMENSIONS = EntityDimensions.scalable(0.45f, 1.55f);

    private boolean justSpawned = true;
    private BlockPos spawnPos;
    private float spawnYRot;
    private UUID ownerUUID;
    private BlockPos lastLightPos;
    private boolean rotationInitialized = false;

    public ScarecrowEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
    }

    private boolean isFriend(Entity entity) {
        // 1. Evitar que se peguen entre ellos (No atacar a otros espantapájaros)
        if (entity instanceof ScarecrowEntity) return true;

        // 2. Evitar atacar a CUALQUIER jugador (Como pediste, no solo al dueño)
        if (entity instanceof Player) return true;

        // 3. Golems amigos
        if (entity instanceof net.minecraft.world.entity.animal.golem.SnowGolem) return true;
        if (entity instanceof net.minecraft.world.entity.animal.golem.IronGolem) return true;

        // 4. Mascotas del dueño
        if (entity instanceof OwnableEntity ownable) {
            Entity owner = ownable.getOwner();
            if (owner != null && owner.getUUID().equals(this.ownerUUID)) {
                return true;
            }
        }

        // 5. El dueño directamente (aunque ya está cubierto por Player, se deja por seguridad)
        return entity.getUUID().equals(this.ownerUUID);
    }

    private boolean isTrespassing(LivingEntity entity) {
        if (isFriend(entity)) return false;
        BlockPos entityPos = entity.blockPosition();
        // Escaneo de área bajo la entidad
        for (BlockPos checkPos : BlockPos.betweenClosed(entityPos.offset(-1, -1, -1), entityPos.offset(1, 0, 1))) {
            if (this.level().getBlockState(checkPos).is(Blocks.FARMLAND)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.is(Items.WHEAT) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide()) {
                this.heal(5.0F);
                if (!player.getAbilities().instabuild) itemstack.shrink(1);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GRASS_BREAK, this.getSoundSource(), 3.0F, 1.0F);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WOOL_BREAK, this.getSoundSource(), 2.0F, 0.8F);
                ((ServerLevel)this.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.WHEAT)), this.getX(), this.getY(0.5D), this.getZ(), 15, 0.2D, 0.2D, 0.2D, 0.05D);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public ItemStack getPickResult() {
        // Devuelve el ítem que creamos para spawnearlo
        return new ItemStack(ModItems.SCARECROW.get());
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        boolean wasHurt = super.hurtServer(level, source, amount);

        // Si fue herido y hay un atacante físico
        if (wasHurt && source.getEntity() instanceof LivingEntity attacker) {

            // Efecto visual de paja
            level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.WHEAT)),
                    this.getX(), this.getY(0.5D), this.getZ(), 8, 0.1D, 0.3D, 0.1D, 0.05D);

            // --- LÓGICA DE ALARMA INTELIGENTE ---
            // Solo avisamos a los demás si el atacante NO es un amigo (esto excluye a jugadores y otros espantapájaros)
            if (!this.isFriend(attacker)) {
                level.getEntitiesOfClass(ScarecrowEntity.class, this.getBoundingBox().inflate(20.0D))
                        .forEach(nearbyScarecrow -> {
                            nearbyScarecrow.setTarget(attacker);
                            nearbyScarecrow.setAngry(true);
                        });
            }

            // Si el espantapájaros estaba quieto (modo estatua), lo preparamos para reaccionar
            if (!this.isAngry()) {
                this.setDeltaMovement(0, 0, 0);
                this.hurtMarked = true;
            }
        }
        return wasHurt;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        // Solo ignorar daño de caída si está en modo estatua
        if (!this.isAngry()) {
            // Actualizamos posición pero no aplicamos daño
            super.setOnGround(onGroundIn);
            return;
        }
        // Si está enojado, usar comportamiento normal
        super.checkFallDamage(y, onGroundIn, state, pos);
    }

    @Override
    public void tick() {

        if (this.justSpawned && !this.level().isClientSide()) {

            // SOLO inicializar si nunca tuvo spawn guardado
            if (this.spawnPos == null) {

                this.spawnPos = this.blockPosition();

                Player nearest = this.level().getNearestPlayer(this, 10.0D);

                if (nearest != null) {

                    this.ownerUUID = nearest.getUUID();

                    double dx = nearest.getX() - this.getX();
                    double dz = nearest.getZ() - this.getZ();

                    float yaw = (float)(Math.atan2(dz, dx) * (180F / Math.PI)) - 90F;

                    this.spawnYRot = yaw;

                    this.setYRot(yaw);
                    this.setYHeadRot(yaw);
                    this.setYBodyRot(yaw);
                    this.yRotO = yaw;

                } else {
                    this.spawnYRot = this.getYRot();
                }
            }

            this.justSpawned = false;
        }

        super.tick();

        if (!(this.level() instanceof ServerLevel serverLevel) || this.level().isClientSide())
            return;

        if (!this.isAngry() && this.tickCount % 40 == 0 && this.getHealth() < this.getMaxHealth())
            this.heal(0.5F);

        LivingEntity potentialTarget = null;

        if (this.getTarget() != null && this.getTarget().isAlive() && !isFriend(this.getTarget())) {
            if (this.distanceToSqr(this.getTarget()) <= 1600.0D)
                potentialTarget = this.getTarget();
            else
                this.setTarget(null);
        }

        if (potentialTarget == null && this.ownerUUID != null) {
            Player owner = this.level().getPlayerByUUID(this.ownerUUID);
            if (owner != null && owner.isAlive()) {
                LivingEntity attacker = owner.getLastHurtByMob();
                if (attacker != null && attacker.isAlive() && !isFriend(attacker) && this.distanceTo(attacker) <= 40.0D)
                    potentialTarget = attacker;
            }
        }

        if (potentialTarget == null) {
            for (LivingEntity nearby : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(15.0D))) {
                if (nearby != this && nearby.isAlive() && isTrespassing(nearby)) {
                    potentialTarget = nearby;
                    break;
                }
            }
        }

        if (potentialTarget != null) {

            this.setTarget(potentialTarget);
            this.setAngry(true);
            this.getNavigation().moveTo(potentialTarget, 1.2D);

            if (this.distanceToSqr(potentialTarget) < 2.5D && this.tickCount % 20 == 0) {
                this.doHurtTarget(serverLevel, potentialTarget);
                this.swing(InteractionHand.MAIN_HAND);
            }

        } else {

            if (this.getTarget() != null) this.setTarget(null);

            if (this.spawnPos != null) {

                double tx = this.spawnPos.getX() + 0.5D;
                double tz = this.spawnPos.getZ() + 0.5D;
                double dx = tx - this.getX();
                double dz = tz - this.getZ();
                double dist = dx * dx + dz * dz;

                if (dist > 0.04D) {

                    this.setAngry(true);
                    this.getNavigation().moveTo(tx, this.spawnPos.getY(), tz, 1.2D);

                    if (this.horizontalCollision || this.getNavigation().isDone() || this.tickCount % 10 == 0) {
                        Vec3 vec = new Vec3(dx, 0, dz).normalize().scale(0.2D);
                        this.setDeltaMovement(vec.x, this.getDeltaMovement().y, vec.z);
                    }

                } else {

                    this.setAngry(false);
                    this.getNavigation().stop();
                    this.setPos(tx, this.getY(), tz);
                    this.setYRot(this.spawnYRot);
                    this.setYHeadRot(this.spawnYRot);
                    this.yBodyRot = this.spawnYRot;
                    this.yRotO = this.spawnYRot;
                }
            }
        }

        if (!this.isAngry()) {
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            this.setXRot(0);
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
            this.yRotO = this.getYRot();
            this.xo = this.getX();
            this.yo = this.getY();
            this.zo = this.getZ();
        }

        // 🔥 LUZ DINÁMICA SIN RASTRO
        BlockPos currentPos = this.blockPosition();

        if (this.isAngry()) {

            if (lastLightPos != null && !lastLightPos.equals(currentPos)) {
                if (this.level().getBlockState(lastLightPos).is(net.minecraft.world.level.block.Blocks.LIGHT)) {
                    this.level().removeBlock(lastLightPos, false);
                }
            }

            if (this.level().getBlockState(currentPos).isAir()) {
                this.level().setBlockAndUpdate(
                        currentPos,
                        net.minecraft.world.level.block.Blocks.LIGHT
                                .defaultBlockState()
                                .setValue(net.minecraft.world.level.block.LightBlock.LEVEL, 14)
                );
            }

            lastLightPos = currentPos;

        } else {

            if (lastLightPos != null) {
                if (this.level().getBlockState(lastLightPos).is(net.minecraft.world.level.block.Blocks.LIGHT)) {
                    this.level().removeBlock(lastLightPos, false);
                }
                lastLightPos = null;
            }
        }
    }

    @Override
    public void remove(RemovalReason reason) {

        if (lastLightPos != null) {
            if (this.level().getBlockState(lastLightPos).is(net.minecraft.world.level.block.Blocks.LIGHT)) {
                this.level().removeBlock(lastLightPos, false);
            }
        }

        super.remove(reason);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (this.spawnPos != null) {
            output.putInt("spawn_x", this.spawnPos.getX());
            output.putInt("spawn_y", this.spawnPos.getY());
            output.putInt("spawn_z", this.spawnPos.getZ());
            output.putFloat("spawn_rot", this.spawnYRot);
        }
        if (this.ownerUUID != null) {
            output.store("OwnerUUID", net.minecraft.core.UUIDUtil.CODEC, this.ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        input.getInt("spawn_x").ifPresent(x -> {
            int y = input.getInt("spawn_y").orElse(0);
            int z = input.getInt("spawn_z").orElse(0);
            this.spawnPos = new BlockPos(x, y, z);
        });
        input.read("spawn_rot", com.mojang.serialization.Codec.FLOAT).ifPresent(f -> this.spawnYRot = f);
        input.read("OwnerUUID", net.minecraft.core.UUIDUtil.CODEC).ifPresent(uuid -> this.ownerUUID = uuid);
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        return this.isAngry() ? ANGRY_DIMENSIONS : STATUE_DIMENSIONS;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (ANGRY.equals(key)) this.refreshDimensions();
    }

    public void setAngry(boolean angry) {
        if (this.entityData.get(ANGRY) == angry) return;
        this.entityData.set(ANGRY, angry);
        if (angry && !this.level().isClientSide()) {
            this.playSound(ModSounds.SCARECROW_LAUGH.get(), 1.0F, 1.0F);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {

        Entity killer = source.getEntity();

        // Si lo mató un jugador → usamos el drop normal
        if (killer instanceof Player) {
            super.dropCustomDeathLoot(level, source, recentlyHit);
            return;
        }

        // Si NO lo mató jugador → soltamos el espantapájaros manualmente
        ItemStack stack = new ItemStack(ModItems.SCARECROW.get());
        ItemEntity item = new ItemEntity(level, this.getX(), this.getY(), this.getZ(), stack);
        item.setDefaultPickUpDelay();
        level.addFreshEntity(item);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANGRY, false);
    }

    public boolean isAngry() { return this.entityData.get(ANGRY); }
    @Override public boolean removeWhenFarAway(double dist) { return false; }
    @Override protected SoundEvent getHurtSound(DamageSource ds) { return ModSounds.SCARECROW_HURT.get(); }
    @Override protected SoundEvent getDeathSound() { return ModSounds.SCARECROW_DEAD.get(); }
}