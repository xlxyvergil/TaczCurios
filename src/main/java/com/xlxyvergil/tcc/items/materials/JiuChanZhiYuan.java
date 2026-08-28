package com.xlxyvergil.tcc.items.materials;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 纠缠之缘 —— 合成材料。与神之键/逐火之蛾饰品合成时，不消耗该饰品，仅消耗本物品，
 * 获得与其 NBT 等数据一致的副本。（真我与黑渊白花除外。）
 */
public class JiuChanZhiYuan extends Item {

    public JiuChanZhiYuan(Properties properties) {
        super(properties
            .stacksTo(64)
            .rarity(Rarity.RARE));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.tcc.jiu_chan_zhi_yuan.usage"));
    }
}
