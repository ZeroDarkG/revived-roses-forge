package com.dalterdile.revived_roses;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.ArrayDeque;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GearBlock extends Block {

    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty EAST = BooleanProperty.create("east");

    public static final BooleanProperty CLOCKWISE = BooleanProperty.create("clockwise");
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    protected static final VoxelShape SHAPE_DOWN = Block.box(0, 0, 0, 16, 1, 16);
    protected static final VoxelShape SHAPE_UP = Block.box(0, 15, 0, 16, 16, 16);
    protected static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 0, 16, 16, 1);
    protected static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 15, 16, 16, 16);
    protected static final VoxelShape SHAPE_WEST = Block.box(0, 0, 0, 1, 16, 16);
    protected static final VoxelShape SHAPE_EAST = Block.box(15, 0, 0, 16, 16, 16);

    private static final Map<Direction, BooleanProperty> FACE_PROP = new EnumMap<>(Direction.class);
    private static final Map<Direction, VoxelShape> FACE_SHAPE = new EnumMap<>(Direction.class);

    static {
        FACE_PROP.put(Direction.DOWN, DOWN);
        FACE_PROP.put(Direction.UP, UP);
        FACE_PROP.put(Direction.NORTH, NORTH);
        FACE_PROP.put(Direction.SOUTH, SOUTH);
        FACE_PROP.put(Direction.WEST, WEST);
        FACE_PROP.put(Direction.EAST, EAST);

        FACE_SHAPE.put(Direction.DOWN, SHAPE_UP);
        FACE_SHAPE.put(Direction.UP, SHAPE_DOWN);
        FACE_SHAPE.put(Direction.NORTH, SHAPE_SOUTH);
        FACE_SHAPE.put(Direction.SOUTH, SHAPE_NORTH);
        FACE_SHAPE.put(Direction.WEST, SHAPE_EAST);
        FACE_SHAPE.put(Direction.EAST, SHAPE_WEST);
    }

    public GearBlock(BlockBehaviour.Properties props) {
        super(props.noCollision().noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(DOWN, false).setValue(UP, false)
                .setValue(NORTH, false).setValue(SOUTH, false)
                .setValue(WEST, false).setValue(EAST, false)
                .setValue(CLOCKWISE, true)
                .setValue(POWER, 0)
        );
    }

    private static BooleanProperty propFor(Direction d) {
        return FACE_PROP.get(d);
    }

    private static boolean hasAnyFace(BlockState state) {
        for (Direction d : Direction.values()) {
            BooleanProperty p = propFor(d);
            if (p != null && state.getValue(p)) return true;
        }
        return false;
    }

    private static boolean hasFace(BlockState state, Direction face) {
        BooleanProperty p = propFor(face);
        return p != null && state.getValue(p);
    }

    private static boolean perpendicular(Direction a, Direction b) {
        return a.getAxis() != b.getAxis();
    }

    private static Direction physicalFace(Direction propFace) {
        return propFace.getOpposite();
    }

    private boolean hasPhysicalFaceAt(ServerLevel level, BlockPos pos, Direction phys) {
        BlockState s = level.getBlockState(pos);
        if (!s.is(this)) return false;
        BooleanProperty prop = propFor(phys.getOpposite());
        return prop != null && s.getValue(prop);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        VoxelShape shape = Shapes.empty();
        for (Direction d : Direction.values()) {
            BooleanProperty p = propFor(d);
            if (p != null && state.getValue(p)) {
                shape = Shapes.or(shape, FACE_SHAPE.get(d));
            }
        }
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext ctx) {
        if (ctx.getItemInHand().is(this.asItem())) {
            Direction placeFace = ctx.getClickedFace();
            return !state.getValue(propFor(placeFace));
        }
        return super.canBeReplaced(state, ctx);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction face = ctx.getClickedFace();
        BlockPos pos = ctx.getClickedPos();

        boolean cw = (Math.abs(pos.getX() + pos.getY() + pos.getZ()) % 2 == 0);

        BlockState existing = ctx.getLevel().getBlockState(pos);
        BlockState out;

        if (existing.is(this)) {
            out = existing.setValue(propFor(face), true).setValue(CLOCKWISE, cw);
        } else {
            out = this.defaultBlockState()
                    .setValue(propFor(face), true)
                    .setValue(CLOCKWISE, cw)
                    .setValue(POWER, 0);
        }

        if (!ctx.getLevel().isClientSide()) {
            notifyWrapAround(ctx.getLevel(), pos, out);
        }
        return out;
    }


    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        boolean hasAnySupport = false;
        boolean hasAnyConnection = false;

        for (Direction face : Direction.values()) {
            BooleanProperty p = propFor(face);
            if (p == null || !state.getValue(p)) continue;
            hasAnyConnection = true;
            BlockPos supportPos = pos.relative(face.getOpposite());
            BlockState supportState = world.getBlockState(supportPos);
            if (!supportState.isAir() && supportState.isFaceSturdy(world, supportPos, face)) {
                hasAnySupport = true;
                break;
            }
        }
        return !hasAnyConnection || hasAnySupport;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
        return state.getValue(POWER);
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction dir) {
        return state.getValue(POWER);
    }

    private void notifyWrapAround(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;
        Set<BlockPos> toTick = new HashSet<>();
        toTick.add(pos);

        for (Direction propFace : Direction.values()) {
            BooleanProperty fp = propFor(propFace);
            if (fp == null || !state.getValue(fp)) continue;
            Direction physFace = physicalFace(propFace);
            toTick.add(pos.relative(physFace));
            for (Direction dir : Direction.values()) {
                if (dir.getAxis() == physFace.getAxis()) continue;
                toTick.add(pos.relative(dir));
                toTick.add(pos.relative(dir).relative(physFace));
                toTick.add(pos.relative(physFace).relative(dir));
            }
        }

        for (BlockPos p : toTick) {
            if (level.getBlockState(p).is(this)) {
                level.scheduleTick(p, this, 1);
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        notifyWrapAround(level, pos, state);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block,
                                net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving) {
        if (level.isClientSide()) return;
        if (!this.canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }

        BlockState currentState = state;
        boolean changed = false;

        for (Direction face : Direction.values()) {
            BooleanProperty p = propFor(face);
            if (p != null && currentState.getValue(p)) {
                BlockPos supportPos = pos.relative(face.getOpposite());
                BlockState supportState = level.getBlockState(supportPos);
                if (supportState.isAir() || !supportState.isFaceSturdy(level, supportPos, face)) {
                    currentState = currentState.setValue(p, false);
                    changed = true;
                }
            }
        }

        boolean hasConnections = false;
        for (Direction face : Direction.values()) {
            BooleanProperty p = propFor(face);
            if (p != null && currentState.getValue(p)) {
                hasConnections = true;
                break;
            }
        }

        if (!hasConnections) {
            level.destroyBlock(pos, true);
            return;
        }

        if (changed) {
            level.setBlock(pos, currentState, 3);
        }

        notifyWrapAround(level, pos, currentState);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (pruneUnsupportedFaces(level, pos, state)) return;
        updateNetworkPower(level, pos);
    }

    private boolean pruneUnsupportedFaces(ServerLevel level, BlockPos pos, BlockState state) {
        // Solo procesar si sigue siendo GearBlock
        if (!state.is(this)) return false;

        BlockState cur = state;
        boolean changed = false;

        for (Direction face : Direction.values()) {
            BooleanProperty p = propFor(face);
            if (p == null || !cur.getValue(p)) continue;

            BlockPos supportPos = pos.relative(face.getOpposite());
            BlockState supportState = level.getBlockState(supportPos);

            boolean hasSupport = false;

            // 🔹 Considerar bloques normales
            if (!supportState.isAir() && supportState.isFaceSturdy(level, supportPos, face)) {
                hasSupport = true;
            }

            // 🔹 Considerar losas (slabs)
            if (supportState.getBlock() instanceof SlabBlock slab) {
                SlabType type = supportState.getValue(BlockStateProperties.SLAB_TYPE);
                if ((face == Direction.UP && type == SlabType.BOTTOM) ||
                        (face == Direction.DOWN && type == SlabType.TOP) ||
                        type == SlabType.DOUBLE) {
                    hasSupport = true;
                }
            }

            if (!hasSupport) {
                changed = true;

                // Solo hacer partículas y drop si todavía es GearBlock
                if (level.getBlockState(pos).is(this)) {
                    BlockState particleState = this.defaultBlockState()
                            .setValue(p, true)
                            .setValue(CLOCKWISE, cur.getValue(CLOCKWISE))
                            .setValue(POWER, cur.getValue(POWER));

                    level.levelEvent(2001, pos, Block.getId(particleState));
                    popResource(level, pos, new ItemStack(this.asItem()));
                }

                cur = cur.setValue(p, false);
            }
        }

        if (!changed) return false;

        if (!hasAnyFace(cur)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        } else {
            level.setBlock(pos, cur, 3);
        }

        // 🔹 Mantener la red de conexiones
        if (level.getBlockState(pos).is(this)) {
            notifyWrapAround(level, pos, level.getBlockState(pos));
        }

        return true;
    }

    private record Node(BlockPos pos, Direction physFace) {}

    private void addNeighbors(ServerLevel level, Node n, ArrayDeque<Node> q, Set<Node> visited) {
        BlockPos p = n.pos();
        Direction f = n.physFace();

        Node front = new Node(p.relative(f), f.getOpposite());
        if (!visited.contains(front) && hasPhysicalFaceAt(level, front.pos(), front.physFace())) {
            visited.add(front);
            q.add(front);
        }

        for (Direction d : Direction.values()) {
            if (!perpendicular(d, f)) continue;
            Node sameBlockPerp = new Node(p, d);
            if (!visited.contains(sameBlockPerp) && hasPhysicalFaceAt(level, p, d)) {
                visited.add(sameBlockPerp);
                q.add(sameBlockPerp);
            }

            Node sideSameFace = new Node(p.relative(d), f);
            if (!visited.contains(sideSameFace) && hasPhysicalFaceAt(level, sideSameFace.pos(), f)) {
                visited.add(sideSameFace);
                q.add(sideSameFace);
            }

            Node diagonal = new Node(p.relative(d).relative(f), d.getOpposite());
            if (!visited.contains(diagonal) && hasPhysicalFaceAt(level, diagonal.pos(), diagonal.physFace())) {
                visited.add(diagonal);
                q.add(diagonal);
            }

            Node diagonal2 = new Node(p.relative(f).relative(d), d.getOpposite());
            if (!visited.contains(diagonal2) && hasPhysicalFaceAt(level, diagonal2.pos(), diagonal2.physFace())) {
                visited.add(diagonal2);
                q.add(diagonal2);
            }
        }
    }

    private int externalPowerAround(ServerLevel level, BlockPos pos, Set<BlockPos> network) {
        int max = 0;

        for (Direction d : Direction.values()) {
            BlockPos npos = pos.relative(d);
            BlockState ns = level.getBlockState(npos);

            if (ns.is(this) && network.contains(npos)) continue;
            if (ns.getBlock() instanceof RedStoneWireBlock) continue;

            int best = level.getDirectSignal(npos, d.getOpposite());
            if (ns.isSignalSource()) {
                int weak = level.getSignal(npos, d.getOpposite());
                if (weak > best) best = weak;
            }

            if (best > max) max = best;
            if (max >= 15) return 15;
        }

        return max;
    }

    private int readWireAsExternalInput(ServerLevel level, BlockPos wirePos, Set<BlockPos> network) {
        BlockState ws = level.getBlockState(wirePos);
        if (!(ws.getBlock() instanceof RedStoneWireBlock)) return 0;

        int wirePower = ws.getValue(BlockStateProperties.POWER);
        if (wirePower == 0) return 0;

        int ext = externalPowerAround(level, wirePos, network);
        return ext > 0 ? wirePower : 0;
    }

    private void updateNetworkPower(ServerLevel level, BlockPos start) {
        BlockState startState = level.getBlockState(start);
        if (!startState.is(this)) return;

        Set<Node> visited = new HashSet<>();
        ArrayDeque<Node> q = new ArrayDeque<>();

        for (Direction propFace : Direction.values()) {
            BooleanProperty p = propFor(propFace);
            if (p != null && startState.getValue(p)) {
                Direction phys = physicalFace(propFace);
                Node n = new Node(start, phys);
                visited.add(n);
                q.add(n);
            }
        }

        if (visited.isEmpty()) return;

        final int LIMIT = 200_000;
        while (!q.isEmpty() && visited.size() <= LIMIT) {
            Node cur = q.poll();
            if (!hasPhysicalFaceAt(level, cur.pos(), cur.physFace())) continue;
            addNeighbors(level, cur, q, visited);
        }

        Set<BlockPos> network = new HashSet<>();
        for (Node n : visited) network.add(n.pos());

        int target = 0;

        for (BlockPos p2 : network) {
            for (Direction d : Direction.values()) {
                BlockPos npos = p2.relative(d);
                BlockState ns = level.getBlockState(npos);

                if (ns.is(this)) continue;

                int best;
                if (ns.getBlock() instanceof RedStoneWireBlock) {
                    best = readWireAsExternalInput(level, npos, network);
                } else {
                    best = level.getDirectSignal(npos, d.getOpposite());
                    if (ns.isSignalSource()) {
                        int weak = level.getSignal(npos, d.getOpposite());
                        if (weak > best) best = weak;
                    }
                }

                if (best > target) target = best;
                if (target >= 15) break;
            }
            if (target >= 15) break;
        }

        boolean changed = false;

        for (BlockPos p2 : network) {
            BlockState ps2 = level.getBlockState(p2);
            if (!ps2.is(this)) continue;

            int curP = ps2.getValue(POWER);
            if (curP != target) {
                changed = true;
                level.setBlock(p2, ps2.setValue(POWER, target), 3);
            }
        }

        if (changed) {
            for (BlockPos p2 : network) {
                level.updateNeighborsAt(p2, this);
                for (Direction d : Direction.values()) {
                    level.updateNeighborsAt(p2.relative(d), this);
                }
            }
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player,
                                       boolean willHarvest, FluidState fluid) {

        Direction hitFace = null;

        HitResult hr = player.pick(5.0D, 0.0F, false);
        if (hr instanceof BlockHitResult bhr && bhr.getBlockPos().equals(pos)) {
            Direction d = bhr.getDirection();
            BooleanProperty p = propFor(d);
            if (p != null && state.getValue(p)) hitFace = d;
        }

        if (hitFace == null) {
            for (Direction d : Direction.values()) {
                BooleanProperty p = propFor(d);
                if (p != null && state.getValue(p)) {
                    hitFace = d;
                    break;
                }
            }
        }

        if (hitFace == null) {
            return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        }

        BooleanProperty faceProp = propFor(hitFace);

        BlockState particleState = this.defaultBlockState()
                .setValue(faceProp, true)
                .setValue(CLOCKWISE, state.getValue(CLOCKWISE))
                .setValue(POWER, state.getValue(POWER));

        BlockState newState = state.setValue(faceProp, false);

        if (!level.isClientSide()) {
            long now = level.getGameTime();
            var data = player.getPersistentData();
            long last = data.getLong("rr_gear_break_tick").orElse(-1L);
            if (last == now) return false;

            data.putLong("rr_gear_break_tick", now);

            if (!hasAnyFace(newState)) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            } else {
                level.setBlock(pos, newState, 3);
            }

            level.levelEvent(2001, pos, Block.getId(particleState));

            if (!player.isCreative()) {
                popResource(level, pos, new ItemStack(this.asItem()));
            }

            ItemStack tool = player.getMainHandItem();
            if (!tool.isEmpty()) {
                player.getCooldowns().addCooldown(tool, 2);
            }

            notifyWrapAround(level, pos, level.getBlockState(pos));

            for (Direction d : Direction.values()) {
                BlockPos n = pos.relative(d);
                if (!level.getBlockState(n).is(this)) {
                    level.updateNeighborsAt(n, this);
                }
            }
        }
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DOWN, UP, NORTH, SOUTH, WEST, EAST, CLOCKWISE, POWER);
    }
}