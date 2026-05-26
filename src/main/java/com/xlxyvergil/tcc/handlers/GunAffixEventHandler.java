package com.xlxyvergil.tcc.handlers;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.affix.GunMobEffectAffix;
import com.xlxyvergil.tcc.integration.ApothicCuriosIntegration;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Map;

/**
 * 枪械词缀事件处理器 — 词缀来源从 Curios 饰品遍历，
 * 触发 TCC 自定义 {@link GunMobEffectAffix} 的 onGunshotPost。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GunAffixEventHandler {

    @SubscribeEvent
    public static void onGunHurt(EntityHurtByGunEvent.Post event) {
        if (event.getLogicalSide().isClient()) {
            return;
        }
        if (!ApothicCuriosIntegration.isInitialized()) {
            return;
        }
        var shooter = event.getAttacker();
        if (shooter == null) {
            return;
        }

        CuriosApi.getCuriosInventory(shooter).ifPresent(handler -> {
            handler.getCurios().forEach((slotId, stacksHandler) -> {
                for (int i = 0; i < stacksHandler.getSlots(); i++) {
                    ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                    if (stack.isEmpty()) continue;

                    Map<DynamicHolder<? extends dev.shadowsoffire.apotheosis.adventure.affix.Affix>, AffixInstance> affixes
                            = AffixHelper.getAffixes(stack);

                    for (var entry : affixes.entrySet()) {
                        AffixInstance instance = entry.getValue();
                        if (!instance.isValid()) continue;

                        if (entry.getKey().get() instanceof GunMobEffectAffix gunAffix) {
                            gunAffix.onGunshotPost(stack, instance, event);
                        }
                    }
                }
            });
        });
    }
}
