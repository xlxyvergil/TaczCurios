package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.shop.TccShopMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 菜单类型注册。菜单只承载泰辛商店的客户端界面，不含任何可交互槽位。
 */
public final class TccMenus {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, TaczCurios.MODID);

    public static final RegistryObject<MenuType<TccShopMenu>> TESHIN_SHOP =
            MENU_TYPES.register("teshin_shop", () -> IForgeMenuType.create(
                    (windowId, playerInventory, extraData) ->
                            new TccShopMenu(windowId, playerInventory, extraData.readBlockPos())));

    private TccMenus() {
    }
}
