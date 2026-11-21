package com.xlxyvergil.tcc.registries;

import com.mojang.serialization.MapCodec;
import com.xlxyvergil.tcc.shop.TccShopMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 泰辛工作台：右键（空手，或手持没有方块交互行为的物品）打开泰辛商店。
 * 不再与村民职业 / POI 有任何关系。
 */
public class TeshinWorkbenchBlock extends Block {

    public TeshinWorkbenchBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(TeshinWorkbenchBlock::new);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        // 潜行右击放行，保留往工作台上叠方块/放物品的原版用法
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new TccShopMenuProvider(pos));
        }
        return InteractionResult.CONSUME;
    }
}
