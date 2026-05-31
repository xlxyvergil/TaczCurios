package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.items.HeavenFireJudgment;
import com.xlxyvergil.tcc.items.SummerBeach;
import com.xlxyvergil.tcc.items.BrahmaBeasts;
import com.xlxyvergil.tcc.items.HeavenFireApocalypse;
import com.xlxyvergil.tcc.registries.TaczItems;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SummerBeachDropEvent {
    
    private static final String SUMMER_BEACH_OBTAINED_TAG = "SummerBeachObtained";
    
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        
        Entity killer = event.getSource().getEntity();
        if (killer == null) return;
        
        String targetConfig = TaczCuriosConfig.COMMON.summerBeachObtainEntity.get();
        String baseEntityId = getBaseEntityId(targetConfig);
        String nbtFilter = extractNbtFilter(targetConfig);
        String killerKey = BuiltInRegistries.ENTITY_TYPE.getKey(killer.getType()).toString();
        if (!baseEntityId.equals(killerKey)) return;
        if (killer instanceof LivingEntity livingKiller && !matchesNbtFilter(livingKiller, nbtFilter)) return;

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
    
    @SubscribeEvent
    public static void onGunLivingDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();
        if (killed.level().isClientSide) return;

        // 虚数伤害 = 天火已装备 + 枪型已验证（onGunHurtPre），无需再校验
        if (!event.getSource().is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) return;
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        String entityKey = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();

        // 夏日沙滩：进化 + 抵抗计数
        if (SummerBeach.hasSummerBeachEquipped(player)) {
            boolean countedForEvolution = false;
            List<? extends List<String>> summerEvoReqs = TaczCuriosConfig.COMMON.summerBeachEvolutionRequirements.get();
            for (List<String> req : summerEvoReqs) {
                String reqEntity = req.get(0);
                String nbtFilter = req.size() > 2 ? req.get(2) : null;
                if (entityKey.equals(reqEntity) && matchesNbtFilter(killed, nbtFilter)) {
                    String matchKey = getMatchKey(entityKey, nbtFilter);
                    SummerBeach.incrementEntityKillCount(player, matchKey, Integer.parseInt(req.get(1)));
                    countedForEvolution = true;
                    break;
                }
            }
            if (!countedForEvolution) {
                List<? extends List<String>> summerResistList = TaczCuriosConfig.COMMON.summerBeachResistanceEntities.get();
                for (List<String> entry : summerResistList) {
                    String resistEntity = entry.get(0);
                    String nbtFilter = entry.size() > 2 ? entry.get(2) : null;
                    if (entityKey.equals(resistEntity) && matchesNbtFilter(killed, nbtFilter)) {
                        if (SummerBeach.getResistanceFromKills(player) < TaczCuriosConfig.COMMON.summerBeachMaxKillResistance.get()) {
                            String matchKey = getMatchKey(entityKey, nbtFilter);
                            SummerBeach.incrementEntityKillCount(player, matchKey, Integer.MAX_VALUE);
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
                String reqEntity = req.get(0);
                String nbtFilter = req.size() > 2 ? req.get(2) : null;
                if (entityKey.equals(reqEntity) && matchesNbtFilter(killed, nbtFilter)) {
                    String matchKey = getMatchKey(entityKey, nbtFilter);
                    BrahmaBeasts.incrementEntityKillCount(player, matchKey, Integer.parseInt(req.get(1)));
                    countedForEvolution = true;
                    break;
                }
            }
            if (!countedForEvolution) {
                List<? extends List<String>> brahmaResistList = TaczCuriosConfig.COMMON.brahmaBeastsResistanceEntities.get();
                for (List<String> entry : brahmaResistList) {
                    String resistEntity = entry.get(0);
                    String nbtFilter = entry.size() > 2 ? entry.get(2) : null;
                    if (entityKey.equals(resistEntity) && matchesNbtFilter(killed, nbtFilter)) {
                        if (BrahmaBeasts.getResistanceFromKills(player) < TaczCuriosConfig.COMMON.brahmaBeastsMaxKillResistance.get()) {
                            String matchKey = getMatchKey(entityKey, nbtFilter);
                            BrahmaBeasts.incrementEntityKillCount(player, matchKey, Integer.MAX_VALUE);
                        }
                        break;
                    }
                }
            }
        }

        checkSummerBeachEvolution(player);
        checkSalvationEvolution(player);
    }

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
                    int carriedResistance = TaczCuriosConfig.COMMON.summerBeachBaseResistance.get() + Math.min(SummerBeach.getResistanceFromKills(player), TaczCuriosConfig.COMMON.summerBeachMaxKillResistance.get());
                    
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
    
    private static void checkSalvationEvolution(Player player) {
        double resistance = player.getAttributeValue(com.xlxyvergil.tcc.attribute.TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get());
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
                        
                        int carriedFromSummer = BrahmaBeasts.getCarriedResistance(player);
                        int fromKills = Math.min(BrahmaBeasts.getResistanceFromKills(player), 
                            TaczCuriosConfig.COMMON.brahmaBeastsMaxKillResistance.get());
                        int brahmaTotal = carriedFromSummer + fromKills;
                        
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

    // ==================== NBT 条件匹配工具方法 ====================

    /**
     * 检查实体是否满足 NBT 条件过滤。
     * @param entity 被杀实体
     * @param nbtFilter NBT 条件字符串，如 "apoth.boss=true" 或 "apoth.boss=true,apoth.rarity=apotheosis:mythic"
     *                  null 或空字符串表示无条件通过
     * @return true 如果满足所有条件
     */
    public static boolean matchesNbtFilter(LivingEntity entity, @Nullable String nbtFilter) {
        if (nbtFilter == null || nbtFilter.isEmpty()) return true;
        CompoundTag data = entity.getPersistentData();
        String[] conditions = nbtFilter.split(",");
        for (String condition : conditions) {
            String[] kv = condition.split("=", 2);
            if (kv.length != 2) continue;
            String key = kv[0].trim();
            String expectedValue = kv[1].trim();
            if (!data.contains(key)) return false;
            if ("true".equals(expectedValue)) {
                if (!data.getBoolean(key)) return false;
            } else if ("false".equals(expectedValue)) {
                if (data.getBoolean(key)) return false;
            } else {
                if (!expectedValue.equals(data.getString(key))) return false;
            }
        }
        return true;
    }

    /**
     * 生成复合匹配键。
     * 无 NBT 条件时直接返回实体 ID；有条件时返回 "entityId[nbtFilter]"。
     */
    public static String getMatchKey(String entityId, @Nullable String nbtFilter) {
        if (nbtFilter == null || nbtFilter.isEmpty()) return entityId;
        return entityId + "[" + nbtFilter + "]";
    }

    /**
     * 从复合键中提取基础实体 ID。
     * e.g. "minecraft:wither[apoth.boss=true]" → "minecraft:wither"
     */
    public static String getBaseEntityId(String key) {
        int bracketIdx = key.indexOf('[');
        return bracketIdx > 0 ? key.substring(0, bracketIdx) : key;
    }

    /**
     * 从复合键中提取 NBT 过滤条件。
     * e.g. "minecraft:wither[apoth.boss=true]" → "apoth.boss=true"
     */
    public static String extractNbtFilter(String key) {
        int bracketIdx = key.indexOf('[');
        return bracketIdx > 0 ? key.substring(bracketIdx + 1, key.length() - 1) : "";
    }
}