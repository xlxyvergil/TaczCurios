package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.tcc.shop.TccShopMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

/**
 * 泰辛工作台：右键（空手，或手持没有方块交互行为的物品）打开泰辛商店。
 * 不再与村民职业 / POI 有任何关系。
 */
public class TeshinWorkbenchBlock extends Block {

    public TeshinWorkbenchBlock(Properties properties) {
        super(properties);
    }

    /**
     * 1.20.1 没有 useWithoutItem，这里用通用的 use 覆盖：
     * 潜行右键直接放行（保留往工作台上垫方块的行为），其余右键都开商店。
     */
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            // 3 参重载会把 BlockPos 写进高级开界面包，客户端据此校验 stillValid
            NetworkHooks.openScreen(serverPlayer, new TccShopMenuProvider(pos), pos);
        }
        return InteractionResult.CONSUME;
    }
}
