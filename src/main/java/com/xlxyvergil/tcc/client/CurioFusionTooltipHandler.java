package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.BaseCurioItem;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.FusionData;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 饰品 Tooltip 统一处理：在道具名称下方插入融合等级、稀有度与武器类型限制；
 * 融合等级仅 tcc_slot 槽位显示，栏位名称由 Curios 自行处理。
 */
@EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID)
public class CurioFusionTooltipHandler {

    private static final TagKey<Item> TCC_SLOT = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("curios", "tcc_slot"));

    /** 本模组饰品 tooltip 的宽度上限（像素）。
     * 取 160：与 MoonsTeams 的排版对齐（其语言行宽度 p50=99、p75=144、p80=159、
     * p90=195，160 能让约 80% 的效果行不折行，再宽就比它胖了）。 */
    static final int MAX_TOOLTIP_WIDTH = 160;
    /** 屏幕底部为其它模组的固定 UI（如 JEI 右下角面板）预留的高度，避免 tooltip 伸进去被盖住。 */
    private static final int RESERVED_BOTTOM = 64;

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        if (!(stack.getItem() instanceof BaseCurioItem curioItem)) return;

        List<Component> tooltip = event.getToolTip();

        FusionData data = FusionData.from(stack);

        // 融合等级（仅 tcc_slot）
        String levelText = null;
        if (stack.is(TCC_SLOT) && data.isUpgradeable()) {
            int maxLevel = data.maxLevel();
            levelText = Component.translatable("tcc.tooltip.fusion_level",
                    Math.min(data.level(), maxLevel), maxLevel).getString();
        }

        String rarityKey = getRarityKey(data.rarity());
        String rarityName = rarityKey != null
                ? Component.translatable(rarityKey).getString()
                : null;

        // 从 index 1 插入（index 0 是道具名称）
        int insertIdx = 1;
        if (levelText != null) {
            tooltip.add(insertIdx++, Component.literal(levelText));
        }
        if (rarityName != null) {
            tooltip.add(insertIdx++, Component.literal(rarityName));
        }

        List<String> restriction = curioItem.getWeaponTypeRestriction();
        if (restriction != null && !restriction.isEmpty()) {
            for (Component line : buildRestrictionComponents(restriction)) {
                tooltip.add(insertIdx++, line);
            }
        }
    }

    /** 「限定：xxx」统一两行：前缀一行、内容一行。 */
    private static List<Component> buildRestrictionComponents(List<String> restriction) {
        if (restriction.size() == 1 && "melee".equals(restriction.get(0))) {
            return List.of(
                Component.translatable("tcc.tooltip.restricted_melee.line1"),
                Component.translatable("tcc.tooltip.restricted_melee.line2"));
        }
        if (restriction.equals(GunTypeChecker.ALL_GUN_TYPES_LIST)) {
            return List.of(
                Component.translatable("tcc.tooltip.restricted_all_guns.line1"),
                Component.translatable("tcc.tooltip.restricted_all_guns.line2"));
        }
        String gunTypes = GunTypeChecker.formatGunTypes(restriction);
        return List.of(
            Component.translatable("tcc.tooltip.restricted_gun_types.line1"),
            Component.translatable("tcc.tooltip.restricted_gun_types.line2", gunTypes));
    }

    @Nullable
    private static String getRarityKey(Rarity rarity) {
        if (rarity == Rarity.COMMON)    return "tcc.tooltip.rarity.common";
        if (rarity == Rarity.UNCOMMON)  return "tcc.tooltip.rarity.uncommon";
        if (rarity == Rarity.RARE)      return "tcc.tooltip.rarity.rare";
        if (rarity == Rarity.EPIC)      return "tcc.tooltip.rarity.epic";
        if (rarity == TccItems.RIFT)    return "tcc.tooltip.rarity.rift";
        return null;
    }

    /**
     * 限宽：本模组饰品的 tooltip 宽度压到 {@link #MAX_TOOLTIP_WIDTH} 以内。
     * 原版按「屏幕宽度的一半」换行，宽屏下会长到伸进屏幕底部/右侧的其它 mod UI。
     */
    @SubscribeEvent
    public static void onGatherComponents(RenderTooltipEvent.GatherComponents event) {
        if (!(event.getItemStack().getItem() instanceof BaseCurioItem)) {
            return;
        }
        int max = event.getMaxWidth();
        if (max < 0 || max > MAX_TOOLTIP_WIDTH) {
            event.setMaxWidth(MAX_TOOLTIP_WIDTH);
        }
    }

    /**
     * 夹紧：把 tooltip 往上夹，使其底边不进入屏幕底部的预留区域（JEI 等固定 UI 所在处）。
     * <p>
     * 原版 {@code DefaultTooltipPositioner} 的最终 y 是「锚点 y - 12」，只在超出屏幕时才夹到
     * {@code screenHeight - height - 6}，因此这里提前把锚点降低，让最终位置落在预留区之上。
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onTooltipPre(RenderTooltipEvent.Pre event) {
        if (!(event.getItemStack().getItem() instanceof BaseCurioItem)) {
            return;
        }
        Font font = event.getFont();
        int height = 8; // 上下内边距
        for (ClientTooltipComponent component : event.getComponents()) {
            height += component.getHeight();
        }
        int screenHeight = event.getScreenHeight();
        int desiredY = Math.max(4, screenHeight - RESERVED_BOTTOM - height);
        int maxAnchorY = desiredY + 12;
        if (event.getY() > maxAnchorY) {
            event.setY(maxAnchorY);
        }
    }
}
