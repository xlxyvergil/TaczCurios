package com.xlxyvergil.tcc.items.materials;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class JiuChanZhiYuan extends Item {
    public JiuChanZhiYuan(Properties properties) {
        super(properties
            .stacksTo(64)
            .rarity(Rarity.RARE));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.tcc.jiu_chan_zhi_yuan.usage"));
    }
}
