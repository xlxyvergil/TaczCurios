package com.xlxyvergil.tcc.client;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.FusionData;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 饰品 Tooltip 统一处理 — 在每个饰品 tooltip 的道具名称下方插入：
 * <ol>
 *   <li>融合等级（仅 tcc_slot 通用槽位饰品）</li>
 *   <li>稀有度</li>
 *   <li>武器类型限制（如有）</li>
 * </ol>
 * 栏位名称由 Curios 自行处理，避免重复。
 * 原有道具自定义信息（效果数值等）保持不动，被顺势下移。
 * 统一适用于 tcc_slot / tcc_tdk / tcc_3rd 所有槽位，融合等级仅 tcc_slot 显示。
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CurioFusionTooltipHandler {

    private static final TagKey<Item> TCC_SLOT = TagKey.create(Registries.ITEM,
            new ResourceLocation("curios", "tcc_slot"));

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        if (!(stack.getItem() instanceof BaseCurioItem curioItem)) return;

        List<Component> tooltip = event.getToolTip();

        FusionData data = FusionData.from(stack);

        // 融合等级（仅 tcc_slot 通用槽位饰品）
        String levelText = null;
        if (stack.is(TCC_SLOT) && data.isUpgradeable()) {
            int maxLevel = data.maxLevel();
            levelText = Component.translatable("tcc.tooltip.fusion_level",
                    Math.min(data.level(), maxLevel), maxLevel).getString();
        }

        // 稀有度
        String rarityKey = getRarityKey(data.rarity());
        String rarityName = rarityKey != null
                ? Component.translatable(rarityKey).getString()
                : null;

        // 从 index 1 开始插入（index 0 是道具名称）
        int insertIdx = 1;
        if (levelText != null) {
            tooltip.add(insertIdx++, Component.literal(levelText));
        }
        if (rarityName != null) {
            tooltip.add(insertIdx++, Component.literal(rarityName));
        }

        // 武器类型限制
        List<String> restriction = curioItem.getWeaponTypeRestriction();
        if (restriction != null && !restriction.isEmpty()) {
            Component restrictionText = buildRestrictionComponent(restriction);
            if (restrictionText != null) {
                tooltip.add(insertIdx, restrictionText);
            }
        }
    }

    /** 构建武器类型限制的 tooltip 文本 */
    @Nullable
    private static Component buildRestrictionComponent(List<String> restriction) {
        if (restriction.size() == 1 && "melee".equals(restriction.get(0))) {
            return Component.translatable("tcc.tooltip.restricted_melee");
        }
        String gunTypes = GunTypeChecker.formatGunTypes(restriction);
        return Component.translatable("tcc.tooltip.restricted_gun_types", gunTypes);
    }

    /** 根据稀有度返回 tcc.tooltip.rarity.* 翻译键 */
    private static String getRarityKey(Rarity rarity) {
        if (rarity == Rarity.COMMON)    return "tcc.tooltip.rarity.common";
        if (rarity == Rarity.UNCOMMON)  return "tcc.tooltip.rarity.uncommon";
        if (rarity == Rarity.RARE)      return "tcc.tooltip.rarity.rare";
        if (rarity == Rarity.EPIC)      return "tcc.tooltip.rarity.epic";
        // RIFT 或其它自定义稀有度
        return "tcc.tooltip.rarity.rift";
    }
}
