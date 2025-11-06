package net.tracen.umapyoi.block;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StatuesUpperBlock extends Block {

    private final Supplier<Block> bottomBlock;
    
    protected final VoxelShape shape;
    
    public StatuesUpperBlock(Supplier<Block> bottom) {
        this(bottom, Shapes.block());
    }
    
    public StatuesUpperBlock(Supplier<Block> bottom, VoxelShape shape) {
        super(Properties.copy(Blocks.STONE).noOcclusion());
        this.bottomBlock = bottom;
        this.shape = shape;
    }
    
    @Override
    public PushReaction getPistonPushReaction(BlockState state){
        return PushReaction.BLOCK;
    }

    @Override
    public Item asItem() {
        return this.bottomBlock.get().asItem();
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    public Supplier<Block> getBottomBlock() {
        return bottomBlock;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return this.shape;
    }
    
    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return pLevel.getBlockState(pPos.below()).is(this.getBottomBlock().get());
    }
    
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest,
    		FluidState fluid) {
        if (level.getBlockState(pos.below()).is(this.getBottomBlock().get())) {
            level.destroyBlock(pos.below(), willHarvest);
        }
    	return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
    
    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        if (level.getBlockState(pos.below()).is(this.getBottomBlock().get())) {
            level.destroyBlock(pos.below(), false);
        }
    	super.onBlockExploded(state, level, pos, explosion);
    }

    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos,
            boolean pIsMoving) {
        if (!pLevel.isClientSide) {
            if (!pState.canSurvive(pLevel, pPos)) {
                pLevel.removeBlock(pPos, false);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHit) {
        return pLevel.getBlockState(pPos.below()).getBlock().use(pLevel.getBlockState(pPos.below()), pLevel,
                pPos.below(), pPlayer, pHand, pHit);
    }
}
