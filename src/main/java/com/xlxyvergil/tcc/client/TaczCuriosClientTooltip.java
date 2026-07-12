package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.items.curios.HeavenFireApocalypse;
import com.xlxyvergil.tcc.items.curios.HeavenFireApocalypseEndless;
import com.xlxyvergil.tcc.items.curios.HeavenFireJudgment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * 客户端 Tooltip 工具类
 * 仅处理需要访问 Minecraft.getInstance().player 的动态计算逻辑，
 * 避免在物品类（服务端也会加载）中出现客户端类的字节码引用。
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TaczCuriosClientTooltip {

    /**
     * 根据玩家当前虚数抗性，计算最终伤害保留率
     * @return double[] {totalRetentionPct, resistanceBonusPct}
     */
    public static double[] getImaginaryResistanceRetention(double baseRetentionPct, double bonusPerPoint) {
        double resistanceBonusPct = 0;
        double totalRetentionPct = baseRetentionPct;
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            double resistance = player.getAttributeValue(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
            resistanceBonusPct = resistance * bonusPerPoint;
            totalRetentionPct = Math.max(0, baseRetentionPct + resistanceBonusPct);
        }
        return new double[]{totalRetentionPct, resistanceBonusPct};
    }

    /**
     * 监听 ItemTooltipEvent，为天火系列饰品追加虚数抗性动态信息
     */
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();

        if (stack.getItem() instanceof HeavenFireApocalypse) {
            appendApocalypseDynamicInfo(tooltip);
        } else if (stack.getItem() instanceof HeavenFireJudgment) {
            appendJudgmentDynamicInfo(tooltip);
        } else if (stack.getItem() instanceof HeavenFireApocalypseEndless) {
            appendEndlessDynamicInfo(tooltip);
        }
    }

    private static void appendApocalypseDynamicInfo(List<Component> tooltip) {
        double baseRetentionPct = TaczCuriosConfig.COMMON.heavenFireApocalypseDamageConversionRatio.get() * 100;
        double bonusPerPoint = TaczCuriosConfig.COMMON.imaginaryDamageResistanceBonusPerPoint.get() * 100;
        double[] retention = getImaginaryResistanceRetention(baseRetentionPct, bonusPerPoint);
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse.damage_conversion",
                String.format("%.0f", retention[0]),
                String.format("%.0f", baseRetentionPct),
                String.format("%.0f", retention[1])));
    }

    private static void appendJudgmentDynamicInfo(List<Component> tooltip) {
        double baseRetentionPct = TaczCuriosConfig.COMMON.heavenFireJudgmentDamageConversionRatio.get() * 100;
        double bonusPerPoint = TaczCuriosConfig.COMMON.imaginaryDamageResistanceBonusPerPoint.get() * 100;
        double[] retention = getImaginaryResistanceRetention(baseRetentionPct, bonusPerPoint);
        tooltip.add(Component.translatable("item.tcc.heaven_fire_judgment.damage_conversion",
                String.format("%.0f", retention[0]),
                String.format("%.0f", baseRetentionPct),
                String.format("%.0f", retention[1])));
    }

    private static void appendEndlessDynamicInfo(List<Component> tooltip) {
        double baseRetentionPct = TaczCuriosConfig.COMMON.endlessDamageConversionRatio.get() * 100;
        double bonusPerPoint = TaczCuriosConfig.COMMON.imaginaryDamageResistanceBonusPerPoint.get() * 100;
        double[] retention = getImaginaryResistanceRetention(baseRetentionPct, bonusPerPoint);
        tooltip.add(Component.translatable("item.tcc.heaven_fire_apocalypse_endless.damage_conversion",
                String.format("%.0f", retention[0]),
                String.format("%.0f", baseRetentionPct),
                String.format("%.0f", retention[1])));
    }
}
