package com.xlxyvergil.tcc.shop;

import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

/**
 * 商店货币。
 *
 * 货币物品由配置项 {@code teshin_shop.currencyItem} 决定，默认下界合金碎片
 * （minecraft:netherite_scrap）；价格数字始终是"多少个货币物品"，换货币不改价。
 *
 * 名称一律取物品自身的语言键，不写死译文 —— 各版本/各物品的译名不同，硬编码会与物品栏对不上。
 * 客户端与服务端共用本类：COMMON 配置在玩家登录时由服务端同步下来，因此两侧解析出的是同一件物品。
 */
public final class ShopCurrency {

    /** 配置值解析失败时的兜底物品。 */
    private static final Item FALLBACK = Items.NETHERITE_SCRAP;

    /** 上次解析的配置字符串与结果，避免每帧重复解析 ResourceLocation。 */
    private static String cachedId;
    private static Item cachedItem = FALLBACK;

    private ShopCurrency() {
    }

    /** 当前货币物品。配置写错/物品未注册时退回下界合金碎片。 */
    public static Item item() {
        String id = TaczCuriosConfig.COMMON.shopCurrencyItem.get();
        if (id != null && id.equals(cachedId)) {
            return cachedItem;
        }
        cachedId = id;
        cachedItem = resolve(id);
        return cachedItem;
    }

    @Nullable
    private static Item resolve(@Nullable String id) {
        if (id == null || id.isBlank()) {
            return FALLBACK;
        }
        ResourceLocation key;
        try {
            key = new ResourceLocation(id.trim());
        } catch (RuntimeException e) {
            return FALLBACK;
        }
        Item item = ForgeRegistries.ITEMS.getValue(key);
        return item == null ? FALLBACK : item;
    }

    /** 该物品堆是否为本次交易的货币。 */
    public static boolean isCurrency(@Nullable ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(item());
    }

    /** 货币显示名，例如「下界合金碎片」。 */
    public static Component name() {
        return new ItemStack(item()).getHoverName();
    }
}
