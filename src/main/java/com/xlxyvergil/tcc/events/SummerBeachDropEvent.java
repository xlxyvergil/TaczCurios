package com.xlxyvergil.tcc.events;

import com.xlxyvergil.tcc.items.HeavenFireJudgment;
import com.xlxyvergil.tcc.items.SummerBeach;
import com.xlxyvergil.tcc.items.BrahmaBeasts;
import com.xlxyvergil.tcc.items.Salvation;
import com.xlxyvergil.tcc.items.HeavenFireApocalypseEndless;
import com.xlxyvergil.tcc.registries.TaczItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

/**
 * 夏日沙滩获取事件监听器
 * 当玩家装备天火圣裁被凋零击杀时，掉落绑定的夏日沙滩饰品
 */
@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SummerBeachDropEvent {
    
    private static final String SUMMER_BEACH_OBTAINED_TAG = "SummerBeachObtained";
    
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        // 只处理玩家死亡
        if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
            return;
        }
        
        // 只在服务端执行
        if (player.level().isClientSide) {
            return;
        }
        
        // 检查是否被凋零击杀
        if (!(event.getSource().getEntity() instanceof WitherBoss)) {
            return;
        }
        
        // 检查是否装备了天火圣裁
        if (!HeavenFireJudgment.hasHeavenFireJudgmentEquipped(player)) {
            return;
        }
        
        // 检查是否已经获得过夏日沙滩（使用玩家数据）
        CompoundTag playerData = player.getPersistentData();
        if (playerData.getBoolean(SUMMER_BEACH_OBTAINED_TAG)) {
            return;  // 已经获得过，不再掉落
        }
        
        // 标记已获得
        playerData.putBoolean(SUMMER_BEACH_OBTAINED_TAG, true);
        
        // 创建绑定的夏日沙滩饰品
        ItemStack summerBeachStack = new ItemStack(TaczItems.SUMMER_BEACH.get());
        
        // 绑定到当前玩家（使用UUID）
        CompoundTag tag = summerBeachStack.getOrCreateTag();
        tag.putString("BoundPlayer", player.getStringUUID());
        tag.putString("BoundPlayerName", player.getName().getString());
        tag.putBoolean("IsBound", true);
        
        // 直接放入玩家的3rd饰品栏
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            ICurioStacksHandler stacksHandler = handler.getCurios().get("tcc_3rd");
            if (stacksHandler != null) {
                // 查找空槽位
                for (int i = 0; i < stacksHandler.getSlots(); i++) {
                    if (stacksHandler.getStacks().getStackInSlot(i).isEmpty()) {
                        stacksHandler.getStacks().setStackInSlot(i, summerBeachStack);
                        break;
                    }
                }
            }
        });
    }
    
    /**
     * 监听实体死亡事件，当装备夏日沙滩的玩家击杀凋零时增加计数
     */
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        // 检查是否是凋零死亡
        if (!(event.getEntity() instanceof WitherBoss)) {
            return;
        }
        
        // 获取击杀者
        if (!(event.getSource().getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
            return;
        }
        
        // 只在服务端执行
        if (player.level().isClientSide) {
            return;
        }
        
        // 检查是否装备了夏日沙滩
        if (SummerBeach.hasSummerBeachEquipped(player)) {
            // 增加击杀计数
            SummerBeach.incrementWitherKillCount(player);
        }
        
        // 检查是否装备了梵天百兽
        if (BrahmaBeasts.hasBrahmaBeastsEquipped(player)) {
            // 增加击杀计数
            BrahmaBeasts.incrementEnderDragonKillCount(player);
        }
    }
    
    /**
     * 监听末影龙死亡事件，当玩家同时装备天火劫灭和夏日沙滩时，替换为梵天百兽
     */
    @SubscribeEvent
    public static void onEnderDragonDeath(LivingDeathEvent event) {
        // 检查是否是末影龙死亡
        if (!(event.getEntity() instanceof EnderDragon)) {
            return;
        }
        
        // 获取击杀者
        if (!(event.getSource().getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
            return;
        }
        
        // 只在服务端执行
        if (player.level().isClientSide) {
            return;
        }
        
        // 检查是否装备了梵天百兽
        if (BrahmaBeasts.hasBrahmaBeastsEquipped(player)) {
            // 增加击杀计数
            BrahmaBeasts.incrementEnderDragonKillCount(player);
        }
        
        // 检查是否满足救世+无烬终焉的获取条件
        checkSalvationEvolution(player);
    }
    
    /**
     * 检查并执行救世+无烬终焉的进化
     */
    private static void checkSalvationEvolution(net.minecraft.world.entity.player.Player player) {
        // 1. 检查虚数抗性 > 80
        double resistance = player.getAttributeValue(com.xlxyvergil.tcc.core.TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        if (resistance <= 80.0) {
            return;
        }
        
        // 2. 检查是否装备梵天百兽
        if (!BrahmaBeasts.hasBrahmaBeastsEquipped(player)) {
            return;
        }
        
        // 3. 检查是否装备天火劫灭
        if (!com.xlxyvergil.tcc.items.HeavenFireApocalypse.hasHeavenFireApocalypseEquipped(player)) {
            return;
        }
        
        // 4. 检查梵天百兽的末影龙击杀数 >= 30
        int killCount = BrahmaBeasts.getEnderDragonKillCount(player);
        if (killCount < 30) {
            return;
        }
        
        // 满足所有条件，执行进化
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            // 替换梵天百兽为救世
            var stacksHandler3rd = handler.getCurios().get("tcc_3rd");
            if (stacksHandler3rd != null) {
                for (int i = 0; i < stacksHandler3rd.getSlots(); i++) {
                    ItemStack stack = stacksHandler3rd.getStacks().getStackInSlot(i);
                    if (stack.getItem() instanceof BrahmaBeasts) {
                        // 创建救世并继承NBT
                        ItemStack salvationStack = new ItemStack(TaczItems.SALVATION.get());
                        CompoundTag inheritedTag = stack.getTag();
                        if (inheritedTag != null) {
                            salvationStack.setTag(inheritedTag.copy());
                        }
                        stacksHandler3rd.getStacks().setStackInSlot(i, salvationStack);
                        break;
                    }
                }
            }
            
            // 替换天火劫灭为天火劫灭·无烬终焉
            var stacksHandlerSlot = handler.getCurios().get("tcc_slot");
            if (stacksHandlerSlot != null) {
                for (int i = 0; i < stacksHandlerSlot.getSlots(); i++) {
                    ItemStack stack = stacksHandlerSlot.getStacks().getStackInSlot(i);
                    if (stack.getItem() instanceof com.xlxyvergil.tcc.items.HeavenFireApocalypse) {
                        // 创建无烬终焉并继承NBT
                        ItemStack endlessStack = new ItemStack(TaczItems.HEAVEN_FIRE_APOCALYPSE_ENDLESS.get());
                        CompoundTag inheritedTag = stack.getTag();
                        if (inheritedTag != null) {
                            endlessStack.setTag(inheritedTag.copy());
                        }
                        stacksHandlerSlot.getStacks().setStackInSlot(i, endlessStack);
                        break;
                    }
                }
            }
        });
    }
}
