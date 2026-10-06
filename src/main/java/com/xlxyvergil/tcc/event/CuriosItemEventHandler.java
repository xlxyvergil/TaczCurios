package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.items.curios.kongbai.KongbaiZhijian;
import com.xlxyvergil.tcc.link.AttributeLinkRegistry;
import com.xlxyvergil.tcc.util.CurioEffectRefresher;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.api.item.IGun;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.event.CurioEquipEvent;
import top.theillusivec4.curios.api.event.CurioUnequipEvent;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CuriosItemEventHandler {
    
    @SubscribeEvent
    public static void onCurioEquipped(CurioEquipEvent event) {
        LivingEntity entity = event.getEntity();
        CurioSearchHelper.invalidate(entity);
        // Curios 内部已调用 onEquip（由 BaseCurioItem 处理属性+TACZ缓存）
        // 此处仅作为兜底确保缓存更新（支持玩家、女仆等所有 LivingEntity）
        updateTacZCache(entity);
    }
    
    @SubscribeEvent
    public static void onCurioUnequipped(CurioUnequipEvent event) {
        LivingEntity entity = event.getEntity();
        CurioSearchHelper.invalidate(entity);
        // Curios 内部已调用 onUnequip（由 BaseCurioItem 处理属性+TACZ缓存）
        // 此处仅作为兜底确保缓存更新（支持玩家、女仆等所有 LivingEntity）
        updateTacZCache(entity);
    }

    /**
     * 槽内物品变化（装上/卸下规则饰品）后，实时重算佩戴者身上空白之键的联动规则。
     * <p>
     * 这里用 {@link CurioChangeEvent}（服务端、槽位内容变动后触发），而不用
     * {@link CurioEquipEvent}/{@link CurioUnequipEvent}——后两者是 canEquip/canUnequip
     * 检查阶段的前置事件，触发时槽位内容尚未变更，且可能因模拟取出被重复调用。
     */
    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        String slot = event.getIdentifier();
        // 槽位内容已变化，先失效饰品快照，保证本 tick 内后续查询立即看到新内容
        CurioSearchHelper.invalidate(entity);
        // 事件驱动饰品的效果可能依赖其它槽位/饰品的状态，槽位内容变化后统一重建
        CurioEffectRefresher.refreshEventDriven(entity);
        // 只关心数据包规则涉及到的槽位，其它槽位变化直接忽略
        if (slot == null || AttributeLinkRegistry.getLinks(slot).isEmpty()) {
            return;
        }
        ItemStack kongbai = CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof KongbaiZhijian);
        if (!(kongbai.getItem() instanceof KongbaiZhijian item)) {
            return;
        }
        // 重新解析当前槽位命中的规则：卸下的规则随之移除，装回的规则立即恢复
        item.refreshEffects(entity, kongbai);
    }

    /**
     * 装备变更（穿脱护甲、切换手持物品等）会改变源属性值，实时重算佩戴者的联动规则。
     * <p>
     * 饰品槽位变化由 {@link #onCurioChange} 处理，两者合起来覆盖「属性会被拆装改变」的来源；
     * 其余无法监听的属性来源由空白之键的低频 tick 兜底。
     */
    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        // 装备变更可能改变饰品效果的输入（手持武器等），统一重建事件驱动饰品
        CurioEffectRefresher.refreshEventDriven(entity);
        if (AttributeLinkRegistry.getLinksBySlot().isEmpty()) {
            return;
        }
        ItemStack kongbai = CurioSearchHelper.findFirstEquippedStack(entity,
                stack -> stack.getItem() instanceof KongbaiZhijian);
        if (kongbai.getItem() instanceof KongbaiZhijian item) {
            item.refreshEffects(entity, kongbai);
        }
    }

    /** 上一次已处理的规则表版本；与当前版本不同即说明发生过 reload。 */
    private static int seenRuleVersion = -1;

    /** 依赖其它属性值的饰品的兜底重算间隔（tick）。这类属性没有变更事件，只能定期对齐；幂等化后重算开销极低。 */
    private static final int DERIVED_REFRESH_INTERVAL = 20;

    /**
     * 数据包规则表 reload（含世界加载）后，在下一 tick 用新规则重算所有佩戴者。
     * reload 在资源加载线程完成，故那里只自增版本号，真正的重算放在服务端主线程执行。
     * <p>
     * 同时对依赖其它属性值的饰品做低频兜底轮询。
     */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        int current = AttributeLinkRegistry.getVersion();
        if (current != seenRuleVersion) {
            seenRuleVersion = current;
            KongbaiZhijian.refreshAll();
        }
        MinecraftServer server = event.getServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            // 用玩家自身的 tick 计数错峰，避免所有玩家在同一 tick 集中重算
            if (player.tickCount % DERIVED_REFRESH_INTERVAL == 0) {
                CurioEffectRefresher.refreshDerivedAttributes(player);
            }
        }
    }
    
    public static void onCurioEquip(LivingEntity entity, ItemStack stack) {
        updateTacZCache(entity);
    }
    
    public static void onCurioUnequip(LivingEntity entity, ItemStack stack) {
        updateTacZCache(entity);
    }
    
    public static void onGunSwitchEvent(LivingEntity entity) {
        updateTacZCache(entity);
    }
    
    /**
     * 饰品状态变化时触发属性重新计算，支持玩家、女仆等所有 LivingEntity。
     */
    private static void updateTacZCache(LivingEntity entity) {
        ItemStack mainHandItem = entity.getMainHandItem();
        ItemStack offHandItem = entity.getOffhandItem();
        
        if (mainHandItem.getItem() instanceof IGun) {
            AttachmentPropertyManager.postChangeEvent(entity, mainHandItem);
            return;
        }
        
        if (offHandItem.getItem() instanceof IGun) {
            AttachmentPropertyManager.postChangeEvent(entity, offHandItem);
            return;
        }
        
        // 即使没有持枪也触发一次，确保属性正确应用
        AttachmentPropertyManager.postChangeEvent(entity, ItemStack.EMPTY);
    }
}
