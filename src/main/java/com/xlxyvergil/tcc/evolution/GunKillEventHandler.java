package com.xlxyvergil.tcc.evolution;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.EntityConditionHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.xlxyvergil.tcc.util.KillEventDedupe;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * gun_kill 触发器的成就/属性规则处理器。
 * 直接监听 TACZ 的 EntityKillByGunEvent（枪械击杀事件）：致死命中时 TACZ 必定发出该事件，
 * 且事件自带 attacker / killedEntity / gunId / headshot，无需依赖 damage source 或 tag 判定。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GunKillEventHandler {
    public static final String TRIGGER_GUN_KILL = "gun_kill";

    /** 多弹头去重：每个触发器独立实例，避免与其它触发器的记录互相干扰。 */
    private static final KillEventDedupe GUN_KILL_DEDUPE = new KillEventDedupe();

    private static final Logger LOGGER = LogManager.getLogger();

    private GunKillEventHandler() {}

    /**
     * 枪械击杀主路径：监听 TACZ 官方击杀事件。
     * 致死命中时 TACZ 只发 EntityKillByGunEvent（与 EntityHurtByGunEvent.Post 互斥），且事件自带 gunId，
     * 因此不需要也无法用 damage source tag 二次确认。
     */
    @SubscribeEvent
    public static void onGunKill(EntityKillByGunEvent event) {
        if (event.getLogicalSide().isClient()) {
            return;
        }
        LivingEntity attacker = event.getAttacker();
        LivingEntity killed = event.getKilledEntity();
        if (attacker == null || killed == null) {
            return;
        }
        Player player = MaidCompat.resolveOwnerPlayer(attacker);
        if (!(player instanceof ServerPlayer sp)) {
            return;
        }
        ResourceLocation gunId = event.getGunId();

        // 多弹头武器（霰弹/多弹头狙击）会在同一次击杀中连续触发多次 EntityKillByGunEvent，
        // 这里按被杀实体做短窗口去重，保证一次击杀只计一次。
        if (GUN_KILL_DEDUPE.isDuplicate(killed.getUUID(), sp.level().getGameTime())) {
            return;
        }

        handleGunKill(sp, killed, gunId);
    }

    public static void handleGunKill(Player player, LivingEntity killed, ResourceLocation gunId) {
        ServerPlayer serverPlayer = player instanceof ServerPlayer sp ? sp : null;

        // 成就驱动的发放 / 进化
        if (serverPlayer != null) {
            // 击杀末影龙时输出判定明细：前置成就缺失 / 条件不满足都会静默跳过，
            // 没有日志就无从判断「杀龙成就无法完成」到底卡在哪一步。
            boolean dragonKill = killed.getType() == EntityType.ENDER_DRAGON;
            if (dragonKill) {
                LOGGER.info("[tcc][achievement] 枪械击杀末影龙 player={} gun={} curios={}",
                        player.getName().getString(), gunId, equippedIds(player));
            }

            for (AchievementDefinitions.AchievementDef def : AchievementDefinitions.getByTrigger(TRIGGER_GUN_KILL)) {
                if (def.reward() == null) continue;

                if (!def.isEnabled()) continue;

                if (!RuleAdvancementMapping.arePrerequisitesMet(serverPlayer, def)) {
                    if (dragonKill) {
                        LOGGER.info("[tcc][achievement] {} 跳过：前置成就不满足 {}",
                                def.id(), def.prerequisites());
                    }
                    continue;
                }

                if (RuleAdvancementMapping.isAdvancementDone(serverPlayer, def.id())) continue;

                if (!AchievementConditionMatcher.matchesKillConditions(player, killed, gunId, def)) {
                    if (dragonKill) {
                        AchievementDefinitions.AchievementConditions c = def.conditions();
                        LOGGER.info("[tcc][achievement] {} 跳过：条件不满足（需要饰品={} 枪种={} 最小距离={}）",
                                def.id(),
                                c != null ? c.equippedCurios() : null,
                                c != null ? c.holdingGunTypes() : null,
                                c != null ? c.minDistance() : null);
                    }
                    continue;
                }

                // 每次击杀授予 1 步进度
                var kills = def.conditions() != null ? def.conditions().kills() : null;
                if (kills != null && kills.size() > 1) {
                    RuleAdvancementMapping.awardMultiTypeKill(
                            serverPlayer, def.id(), def, killed, 1);
                } else {
                    RuleAdvancementMapping.awardSteps(
                            serverPlayer, def.id(), def.targetCount(), 1);
                }
                if (dragonKill) {
                    LOGGER.info("[tcc][achievement] {} 击杀已记入", def.id());
                }
            }
        }

        // 属性规则（来自 evolution_rules.json）
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTriggerOrEmpty(TRIGGER_GUN_KILL)) {
            if (!rule.enabled) continue;
            if (rule.playerKilled) continue;
            if (rule.type != EvolutionRegistry.RuleType.ATTRIBUTE) continue;
            applyAttributeRule(player, killed, gunId, rule);
        }
    }

    private static void applyAttributeRule(Player player, LivingEntity killed,
                                            ResourceLocation gunId, EvolutionRegistry.Rule rule) {
        if (rule.item == null || rule.item.isBlank() || rule.progress == null || killed == null) return;

        ItemStack tracked = findFirstEquippedStack(player, stack -> rule.item.equals(itemId(stack)));
        if (tracked.isEmpty()) return;

        if (!passesExtraRequirements(player, killed, gunId, rule.requirements)) return;

        boolean changed = false;
        for (EvolutionRegistry.KillGain k : rule.kills) {
            if (k == null || k.entity == null) continue;
            if (!EntityConditionHelper.matchesEntityKey(k.entity.key, killed)) continue;
            if (!EntityConditionHelper.matchesNbtFilters(killed, k.entity.nbt)) continue;
            changed |= incrementProgress(tracked, rule.progress.nbtKey,
                    rule.progress.capCounterKey, rule.progress.cap, k.value);
        }

        if (changed && tracked.getItem() instanceof com.xlxyvergil.tcc.items.BaseCurioItem curio) {
            curio.refreshEffects(player, tracked);
        }
    }

    private static boolean passesExtraRequirements(Player player, LivingEntity killed,
                                                    ResourceLocation gunId, EvolutionRegistry.Requirements req) {
        if (!req.requiredEffects.isEmpty()) {
            for (String effectId : req.requiredEffects) {
                net.minecraft.resources.ResourceLocation effectRl = net.minecraft.resources.ResourceLocation.tryParse(effectId);
                if (effectRl == null) return false;
                net.minecraft.world.effect.MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectRl);
                if (effect == null || !player.hasEffect(effect)) return false;
            }
        }
        if (!req.holdingGunTypes.isEmpty()) {
            if (!GunTypeChecker.matchesGunTypes(gunId, req.holdingGunTypes)) return false;
        }
        if (req.minDistance != null) {
            if (killed == null) return false;
            if (player.distanceToSqr(killed) < req.minDistance * req.minDistance) return false;
        }
        return true;
    }

    private static boolean incrementProgress(ItemStack stack, String progressKey,
                                              String capCounterKey, double cap, double value) {
        if (value == 0.0) return false;
        CompoundTag tag = stack.getOrCreateTag();
        if (progressKey == null || progressKey.isBlank() || capCounterKey == null || capCounterKey.isBlank())
            return false;
        double counter = tag.getDouble(capCounterKey);
        if (counter >= cap) return false;
        double delta = Math.min(value, cap - counter);
        if (delta == 0.0) return false;
        tag.putDouble(capCounterKey, counter + delta);
        tag.putDouble(progressKey, tag.getDouble(progressKey) + delta);
        return true;
    }

    private static String itemId(ItemStack stack) {
        if (stack.isEmpty()) return "";
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return key != null ? key.toString() : "";
    }

    /** 当前佩戴的全部饰品 id，仅用于诊断日志。 */
    private static List<String> equippedIds(LivingEntity livingEntity) {
        List<String> ids = new ArrayList<>();
        CurioSearchHelper.forEachEquippedStack(livingEntity, stack -> ids.add(itemId(stack)));
        return ids;
    }

    static ItemStack findFirstEquippedStack(LivingEntity livingEntity, Predicate<ItemStack> predicate) {
        if (livingEntity == null) return ItemStack.EMPTY;
        ICuriosItemHandler inv = CuriosApi.getCuriosInventory(livingEntity).orElse(null);
        if (inv == null) return ItemStack.EMPTY;
        for (var entry : inv.getCurios().entrySet()) {
            ICurioStacksHandler stacksHandler = entry.getValue();
            if (stacksHandler == null) continue;
            var handler = stacksHandler.getStacks();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty() && predicate.test(stack)) return stack;
            }
        }
        return ItemStack.EMPTY;
    }
    
}
