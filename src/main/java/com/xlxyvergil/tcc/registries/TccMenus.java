package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.shop.TccShopMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * 菜单类型注册。菜单只承载泰辛商店的客户端界面，不含任何可交互槽位。
 */
public final class TccMenus {

    public static final MenuType<TccShopMenu> TESHIN_SHOP = IMenuTypeExtension.create(TccShopMenu::new);

    private TccMenus() {
    }

    public static void init(RegisterEvent.RegisterHelper<MenuType<?>> helper) {
        helper.register(ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "teshin_shop"), TESHIN_SHOP);
    }
}
