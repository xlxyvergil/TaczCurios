package com.xlxyvergil.tcc.items.materials;

import com.xlxyvergil.tcc.util.CollapseCrystalData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class CollapseCrystal extends Item {

    public CollapseCrystal(Properties properties) {
        super(properties
            .stacksTo(64)
            .rarity(Rarity.RARE));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.tcc.collapse_crystal.usage"));

        int required = CollapseCrystalData.REQUIRED_COUNT;
        TagKey<Item> bound = CollapseCrystalData.getBoundGroup(stack);
        int trueSelf = CollapseCrystalData.getRecordedCount(stack, CollapseCrystalData.TRUE_SELF_MATERIALS);
        int heiyuan = CollapseCrystalData.getRecordedCount(stack, CollapseCrystalData.HEIYUAN_BAIHUA_MATERIALS);

        tooltip.add(Component.literal(""));
        if (bound == null) {
            // 未绑定：可任选一组（神之键/逐火之蛾）开始收集
            tooltip.add(Component.translatable("item.tcc.collapse_crystal.progress_true_self", trueSelf, required)
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
            tooltip.add(Component.translatable("item.tcc.collapse_crystal.progress_heiyuan", heiyuan, required)
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
            tooltip.add(Component.translatable("item.tcc.collapse_crystal.select_group")
                    .withStyle(ChatFormatting.GRAY));
        } else if (bound == CollapseCrystalData.TRUE_SELF_MATERIALS) {
            tooltip.add(Component.translatable("item.tcc.collapse_crystal.progress_true_self", trueSelf, required)
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        } else {
            tooltip.add(Component.translatable("item.tcc.collapse_crystal.progress_heiyuan", heiyuan, required)
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }
}
