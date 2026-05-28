package com.xlxyvergil.tcc.events;

import com.xlxyvergil.tcc.items.HeavenFireJudgment;
import com.xlxyvergil.tcc.items.SummerBeach;
import com.xlxyvergil.tcc.items.BrahmaBeasts;
import com.xlxyvergil.tcc.items.HeavenFireApocalypse;
import com.xlxyvergil.tcc.registries.TaczItems;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.List;

@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SummerBeachDropEvent {
    
    private static final String SUMMER_BEACH_OBTAINED_TAG = "SummerBeachObtained";

    // TACZ 子弹伤害 Tag，用于精确判断枪械击杀
    private static final TagKey<DamageType> TACZ_BULLETS_TAG = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("tacz", "bullets"));
    
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        
        Entity killer = event.getSource().getEntity();
        if (killer == null) return;
        
        String targetEntityKey = TaczCuriosConfig.COMMON.summerBeachObtainEntity.get();
        String killerKey = BuiltInRegistries.ENTITY_TYPE.getKey(killer.getType()).toString();
        if (!targetEntityKey.equals(killerKey)) return;
        
        if (!HeavenFireJudgment.hasHeavenFireJudgmentEquipped(player)) return;
        
        CompoundTag playerData = player.getPersistentData();
        if (playerData.getBoolean(SUMMER_BEACH_OBTAINED_TAG)) return;
        
        playerData.putBoolean(SUMMER_BEACH_OBTAINED_TAG, true);
        
        ItemStack summerBeachStack = new ItemStack(TaczItems.SUMMER_BEACH.get());
        CompoundTag tag = summerBeachStack.getOrCreateTag();
        tag.putString("BoundPlayer", player.getStringUUID());
        tag.putString("BoundPlayerName", player.getName().getString());
        tag.putBoolean("IsBound", true);
        
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            ICurioStacksHandler stacksHandler = handler.getCurios().get("tcc_3rd");
            if (stacksHandler != null) {
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
     * 统一通过 LivingDeathEvent + tacz:bullets 伤害 Tag + 生效枪械类型处理所有 TACZ 枪械击杀。
     * 覆盖正常实体、末影龙、mod boss 等所有场景。
     */
    @SubscribeEvent
    public static void onGunLivingDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();
        if (killed.level().isClientSide) return;

        // 必须为 TACZ 枪械伤害
        if (!event.getSource().is(TACZ_BULLETS_TAG)) return;

        Entity source = event.getSource().getEntity();
        if (!(source instanceof Player player)) return;

        // 根据当前装备的天火武器获取对应枪械类型配置
        List<? extends String> gunTypes = getEquippedHeavenFireGunTypes(player);
        if (!GunTypeChecker.isHoldingConfiguredGunTypes(player, gunTypes)) return;

        String entityKey = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();

        // 夏日沙滩：进化 + 抵抗计数
        if (SummerBeach.hasSummerBeachEquipped(player)) {
            boolean countedForEvolution = false;
            List<? extends List<String>> summerEvoReqs = TaczCuriosConfig.COMMON.summerBeachEvolutionRequirements.get();
            for (List<String> req : summerEvoReqs) {
                if (entityKey.equals(req.get(0))) {
                    SummerBeach.incrementEntityKillCount(player, entityKey, Integer.parseInt(req.get(1)));
                    countedForEvolution = true;
                    break;
                }
            }
            if (!countedForEvolution) {
                List<? extends List<String>> summerResistList = TaczCuriosConfig.COMMON.summerBeachResistanceEntities.get();
                for (List<String> entry : summerResistList) {
                    if (entityKey.equals(entry.get(0))) {
                        if (SummerBeach.getResistanceFromKills(player) < TaczCuriosConfig.COMMON.summerBeachMaxKillResistance.get()) {
                            SummerBeach.incrementEntityKillCount(player, entityKey, Integer.MAX_VALUE);
                        }
                        break;
                    }
                }
            }
        }

        // 梵天百兽：进化 + 抵抗计数
        if (BrahmaBeasts.hasBrahmaBeastsEquipped(player)) {
            boolean countedForEvolution = false;
            List<? extends List<String>> brahmaEvoReqs = TaczCuriosConfig.COMMON.brahmaBeastsEvolutionRequirements.get();
            for (List<String> req : brahmaEvoReqs) {
                if (entityKey.equals(req.get(0))) {
                    BrahmaBeasts.incrementEntityKillCount(player, entityKey, Integer.parseInt(req.get(1)));
                    countedForEvolution = true;
                    break;
                }
            }
            if (!countedForEvolution) {
                List<? extends List<String>> brahmaResistList = TaczCuriosConfig.COMMON.brahmaBeastsResistanceEntities.get();
                for (List<String> entry : brahmaResistList) {
                    if (entityKey.equals(entry.get(0))) {
                        if (BrahmaBeasts.getResistanceFromKills(player) < TaczCuriosConfig.COMMON.brahmaBeastsMaxKillResistance.get()) {
                            BrahmaBeasts.incrementEntityKillCount(player, entityKey, Integer.MAX_VALUE);
                        }
                        break;
                    }
                }
            }
        }

        // 检查进化
        checkSummerBeachEvolution(player);
        checkSalvationEvolution(player);
    }

    /**
     * 夏日沙滩 → 梵天百兽
     * 条件：同时装备天火劫灭 + 夏日沙滩，且夏日沙滩的所有进化需求均已满足
     */
    private static void checkSummerBeachEvolution(Player player) {
        if (!HeavenFireApocalypse.hasHeavenFireApocalypseEquipped(player)) return;
        if (!SummerBeach.hasSummerBeachEquipped(player)) return;
        if (!SummerBeach.areAllRequirementsMet(player)) return;
        
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            var stacksHandler3rd = handler.getCurios().get("tcc_3rd");
            if (stacksHandler3rd == null) return;
            top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler stackHandler = stacksHandler3rd.getStacks();
            for (int i = 0; i < stackHandler.getSlots(); i++) {
                ItemStack oldStack = stackHandler.getStackInSlot(i);
                if (oldStack.getItem() instanceof SummerBeach oldBeach) {
                    int carriedResistance = Math.min(SummerBeach.getResistanceFromKills(player), TaczCuriosConfig.COMMON.summerBeachMaxKillResistance.get());
                    
                    ItemStack newStack = new ItemStack(TaczItems.BRAHMA_BEASTS.get());
                    CompoundTag newTag = newStack.getOrCreateTag();
                    newTag.putInt("CarriedResistance", carriedResistance);
                    
                    CompoundTag oldTag = oldStack.getTag();
                    if (oldTag != null) {
                        if (oldTag.getBoolean("IsBound")) {
                            newTag.putBoolean("IsBound", true);
                            newTag.putString("BoundPlayer", oldTag.getString("BoundPlayer"));
                            newTag.putString("BoundPlayerName", oldTag.getString("BoundPlayerName"));
                        }
                    }
                    
                    boolean hasRenderer = stacksHandler3rd.getRenders().size() > i && stacksHandler3rd.getRenders().get(i);
                    top.theillusivec4.curios.api.SlotContext slotContext = new top.theillusivec4.curios.api.SlotContext("tcc_3rd", player, i, false, hasRenderer);
                    oldBeach.onUnequip(slotContext, ItemStack.EMPTY, oldStack);
                    stackHandler.setStackInSlot(i, newStack);
                    if (newStack.getItem() instanceof BrahmaBeasts newBb) {
                        newBb.onEquip(slotContext, oldStack, newStack);
                    }
                    break;
                }
            }
        });
    }
    
    /**
     * 梵天百兽 + 天火劫灭 → 救世 + 无烬终焉
     */
    private static void checkSalvationEvolution(Player player) {
        double resistance = player.getAttributeValue(com.xlxyvergil.tcc.core.TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
        if (resistance <= 80.0) return;
        
        if (!BrahmaBeasts.hasBrahmaBeastsEquipped(player)) return;
        if (!HeavenFireApocalypse.hasHeavenFireApocalypseEquipped(player)) return;
        if (!BrahmaBeasts.areAllRequirementsMet(player)) return;
        
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            var stacksHandler3rd = handler.getCurios().get("tcc_3rd");
            if (stacksHandler3rd != null) {
                top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler stackHandler3rd = stacksHandler3rd.getStacks();
                for (int i = 0; i < stackHandler3rd.getSlots(); i++) {
                    ItemStack oldStack = stackHandler3rd.getStackInSlot(i);
                    if (oldStack.getItem() instanceof BrahmaBeasts oldBb) {
                        ItemStack newStack = new ItemStack(TaczItems.SALVATION.get());
                        
                        int bbBase = TaczCuriosConfig.COMMON.brahmaBeastsBaseResistance.get();
                        int carriedFromSummer = BrahmaBeasts.getCarriedResistance(player);
                        int fromKills = Math.min(BrahmaBeasts.getResistanceFromKills(player), 
                            TaczCuriosConfig.COMMON.brahmaBeastsMaxKillResistance.get());
                        int brahmaTotal = Math.min(bbBase + carriedFromSummer + fromKills, 
                            bbBase + TaczCuriosConfig.COMMON.brahmaBeastsMaxKillResistance.get());
                        
                        CompoundTag newTag = newStack.getOrCreateTag();
                        newTag.putInt("CarriedResistance", brahmaTotal);
                        CompoundTag oldTag = oldStack.getTag();
                        if (oldTag != null) {
                            if (oldTag.getBoolean("IsBound")) {
                                newTag.putBoolean("IsBound", true);
                                newTag.putString("BoundPlayer", oldTag.getString("BoundPlayer"));
                                newTag.putString("BoundPlayerName", oldTag.getString("BoundPlayerName"));
                            }
                        }
                        
                        boolean hasRenderer = stacksHandler3rd.getRenders().size() > i && stacksHandler3rd.getRenders().get(i);
                        top.theillusivec4.curios.api.SlotContext slotContext = new top.theillusivec4.curios.api.SlotContext("tcc_3rd", player, i, false, hasRenderer);
                        oldBb.onUnequip(slotContext, ItemStack.EMPTY, oldStack);
                        stackHandler3rd.setStackInSlot(i, newStack);
                        if (newStack.getItem() instanceof com.xlxyvergil.tcc.items.Salvation newSalvation) {
                            newSalvation.onEquip(slotContext, oldStack, newStack);
                        }
                        break;
                    }
                }
            }
            
            var stacksHandlerSlot = handler.getCurios().get("tcc_slot");
            if (stacksHandlerSlot != null) {
                top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler stackHandlerSlot = stacksHandlerSlot.getStacks();
                for (int i = 0; i < stackHandlerSlot.getSlots(); i++) {
                    ItemStack oldStack = stackHandlerSlot.getStackInSlot(i);
                    if (oldStack.getItem() instanceof HeavenFireApocalypse oldApo) {
                        ItemStack newStack = new ItemStack(TaczItems.HEAVEN_FIRE_APOCALYPSE_ENDLESS.get());
                        CompoundTag inheritedTag = oldStack.getTag();
                        if (inheritedTag != null) {
                            newStack.setTag(inheritedTag.copy());
                        }
                        boolean hasRenderer = stacksHandlerSlot.getRenders().size() > i && stacksHandlerSlot.getRenders().get(i);
                        top.theillusivec4.curios.api.SlotContext slotContext = new top.theillusivec4.curios.api.SlotContext("tcc_slot", player, i, false, hasRenderer);
                        oldApo.onUnequip(slotContext, ItemStack.EMPTY, oldStack);
                        stackHandlerSlot.setStackInSlot(i, newStack);
                        if (newStack.getItem() instanceof com.xlxyvergil.tcc.items.HeavenFireApocalypseEndless newEndless) {
                            newEndless.onEquip(slotContext, oldStack, newStack);
                        }
                        com.tacz.guns.resource.modifier.AttachmentPropertyManager.postChangeEvent(player, player.getMainHandItem());
                        break;
                    }
                }
            }
        });
    }

    /**
     * 获取玩家当前装备的天火武器（圣裁或劫灭）配置的枪械类型列表，均未装备返回 null
     */
    private static List<? extends String> getEquippedHeavenFireGunTypes(Player player) {
        if (HeavenFireApocalypse.hasHeavenFireApocalypseEquipped(player))
            return TaczCuriosConfig.COMMON.heavenFireApocalypseGunTypes.get();
        if (HeavenFireJudgment.hasHeavenFireJudgmentEquipped(player))
            return TaczCuriosConfig.COMMON.heavenFireJudgmentGunTypes.get();
        return List.of();
    }
}
