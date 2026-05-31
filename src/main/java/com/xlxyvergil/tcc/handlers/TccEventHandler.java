package com.xlxyvergil.tcc.handlers;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.helpers.LootTableHelper;
import com.xlxyvergil.tcc.registries.TaczItems;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = TaczCurios.MODID)
public class TccEventHandler {
    private static final TccEventHandler INSTANCE = new TccEventHandler();
    private static final Random RANDOM = new Random();
    
    public static TccEventHandler getInstance() {
        return INSTANCE;
    }
    
    // 定义要添加饰品的原版箱子列表
    private static final List<ResourceLocation> VANILLA_CHESTS = new ArrayList<>();
    
    static {
        // 所有原版箱子战利品表
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/abandoned_mineshaft"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/ancient_city"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/ancient_city_ice_box"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/bastion_bridge"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/bastion_hoglin_stable"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/bastion_other"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/bastion_treasure"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/buried_treasure"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/desert_pyramid"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/end_city_treasure"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/igloo_chest"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/jungle_temple"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/jungle_temple_dispenser"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/nether_bridge"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/pillager_outpost"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/ruined_portal"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/shipwreck_map"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/shipwreck_supply"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/shipwreck_treasure"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/simple_dungeon"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/spawn_bonus_chest"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/stronghold_corridor"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/stronghold_crossing"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/stronghold_library"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/intersection"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/intersection_barrel"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/entrance"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/corridor"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/supply"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/reward"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/reward_common"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/reward_rare"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/reward_unique"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/reward_ominous"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/reward_ominous_common"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/reward_ominous_rare"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/trial_chambers/chests/reward_ominous_unique"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/underwater_ruin_big"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/underwater_ruin_small"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_armorer"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_butcher"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_cartographer"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_desert_house"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_fisher"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_fletcher"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_mason"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_plains_house"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_savanna_house"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_shepherd"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_snowy_house"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_taiga_house"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_tannery"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_temple"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_toolsmith"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/village/village_weaponsmith"));
        VANILLA_CHESTS.add(new ResourceLocation("minecraft", "chests/woodland_mansion"));
    }
    
    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
    public void onLootTablesLoaded(LootTableLoadEvent event) {
        ResourceLocation tableName = event.getName();
        
        // 只处理原版箱子战利品表
        if (VANILLA_CHESTS.contains(tableName)) {
            // 根据配置的几率决定是否生成裂隙碎银
            if (RANDOM.nextFloat() <= com.xlxyvergil.tcc.config.TaczCuriosConfig.COMMON.riftSilverChestSpawnChance.get()) {
                // 获取裂隙碎银物品
                ItemStack riftSilverStack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(TaczCurios.MODID, "rift_silver")));
                
                // 如果成功获取到裂隙碎银物品，则添加到战利品表中
                if (!riftSilverStack.isEmpty()) {
                    // 根据概率确定裂隙碎银的数量: 70%概率1个，20%概率2个，10%概率3个
                    int count = 1;
                    float chance = RANDOM.nextFloat();
                    if (chance < 0.1f) { // 10%概率
                        count = 3;
                    } else if (chance < 0.3f) { // 20%概率
                        count = 2;
                    }
                    // 70%概率保持count=1
                    
                    // 使用固定数量而非范围，因为数量已经根据概率确定
                    LootTableHelper.addLootEntryToChest(event.getTable(), riftSilverStack, 1, count, 15); // 权重15，数量根据概率确定
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onEffectRemove(MobEffectEvent.Remove event) {
        MobEffectInstance effect = event.getEffectInstance();
        if (effect != null && (
            effect.getEffect() == TccMobEffects.HEAVEN_FIRE_BLEEDING.get() ||
            effect.getEffect() == TccMobEffects.HEAVEN_FIRE_APOCALYPSE_BUFF.get() ||
            effect.getEffect() == TccMobEffects.HEAVEN_FIRE_APOCALYPSE_DELAY.get() ||
            effect.getEffect() == TccMobEffects.IMAGINARY_INFECTION.get() ||
            effect.getEffect() == TccMobEffects.IMAGINARY_COLLAPSE.get()
        )) {
            event.setResult(Event.Result.DENY);
        }
    }

    // ========== Phase 3: Buff触发逻辑 ==========

    private static boolean hasCurio(LivingEntity entity, Item curio) {
        return CuriosApi.getCuriosInventory(entity).resolve()
            .map(inv -> inv.findFirstCurio(curio).isPresent())
            .orElse(false);
    }

    private static boolean isHoldingMeleeWeapon(LivingEntity entity) {
        return !entity.getMainHandItem().getAttributeModifiers(EquipmentSlot.MAINHAND)
            .get(Attributes.ATTACK_DAMAGE).isEmpty();
    }

    private static void applyNonStackingBuff(Player player, MobEffect effect, int durationSeconds) {
        player.addEffect(new MobEffectInstance(effect, durationSeconds * 20, 0, false, false, true));
    }

    private static void applyStackingBuff(Player player, MobEffect effect, int durationSeconds, int maxStacks) {
        MobEffectInstance existing = player.getEffect(effect);
        if (existing != null) {
            int newAmp = Math.min(existing.getAmplifier() + 1, maxStacks - 1);
            player.addEffect(new MobEffectInstance(effect, durationSeconds * 20, newAmp, false, false, true));
        } else {
            player.addEffect(new MobEffectInstance(effect, durationSeconds * 20, 0, false, false, true));
        }
    }

    /**
     * 爆头命中 — 触发爆头Buff
     */
    @SubscribeEvent
    public static void onGunHeadshot(EntityHurtByGunEvent.Post event) {
        if (!event.isHeadShot()) return;
        LivingEntity attacker = event.getAttacker();
        if (!(attacker instanceof Player player)) return;
        if (player.level().isClientSide) return;

        // R-03 氩晶瞄具
        if (GunTypeChecker.isHoldingDmgBoostGunType(player) && hasCurio(player, TaczItems.ARGON_SCOPE.get())) {
            applyNonStackingBuff(player, TccMobEffects.ARGON_SCOPE.get(), TaczCuriosConfig.COMMON.argonScopeDuration.get());
        }
        // R-04 镀层氩晶瞄具
        if (GunTypeChecker.isHoldingDmgBoostGunType(player) && hasCurio(player, TaczItems.GILDED_ARGON_SCOPE.get())) {
            applyStackingBuff(player, TccMobEffects.GILDED_ARGON_SCOPE.get(),
                TaczCuriosConfig.COMMON.gildedArgonScopeDuration.get(),
                TaczCuriosConfig.COMMON.gildedArgonScopeMaxStacks.get());
        }
        // S-05 雷射瞄具
        if (GunTypeChecker.isHoldingShotgun(player) && hasCurio(player, TaczItems.LASER_SCOPE.get())) {
            applyNonStackingBuff(player, TccMobEffects.LASER_SCOPE.get(), TaczCuriosConfig.COMMON.laserScopeDuration.get());
        }
        // P-06 液压准心
        if (GunTypeChecker.isHoldingPistol(player) && hasCurio(player, TaczItems.HYDRAULIC_CROSSHAIR.get())) {
            applyNonStackingBuff(player, TccMobEffects.HYDRAULIC_CROSSHAIR.get(), TaczCuriosConfig.COMMON.hydraulicCrosshairDuration.get());
        }
        // P-07 镀层液压准心
        if (GunTypeChecker.isHoldingPistol(player) && hasCurio(player, TaczItems.GILDED_HYDRAULIC_CROSSHAIR.get())) {
            applyStackingBuff(player, TccMobEffects.GILDED_HYDRAULIC_CROSSHAIR.get(),
                TaczCuriosConfig.COMMON.gildedHydraulicCrosshairDuration.get(),
                TaczCuriosConfig.COMMON.gildedHydraulicCrosshairMaxStacks.get());
        }
    }

    /**
     * 枪械击杀 — 触发击杀Buff
     */
    @SubscribeEvent
    public static void onGunKill(EntityKillByGunEvent event) {
        LivingEntity killer = event.getAttacker();
        if (!(killer instanceof Player player)) return;
        if (player.level().isClientSide) return;

        // R-04 镀层氩晶瞄具: 爆头击杀额外层数
        if (event.isHeadShot() && GunTypeChecker.isHoldingDmgBoostGunType(player) && hasCurio(player, TaczItems.GILDED_ARGON_SCOPE.get())) {
            applyStackingBuff(player, TccMobEffects.GILDED_ARGON_SCOPE.get(),
                TaczCuriosConfig.COMMON.gildedArgonScopeDuration.get(),
                TaczCuriosConfig.COMMON.gildedArgonScopeMaxStacks.get());
        }
        // P-07 镀层液压准心: 爆头击杀额外层数
        if (event.isHeadShot() && GunTypeChecker.isHoldingPistol(player) && hasCurio(player, TaczItems.GILDED_HYDRAULIC_CROSSHAIR.get())) {
            applyStackingBuff(player, TccMobEffects.GILDED_HYDRAULIC_CROSSHAIR.get(),
                TaczCuriosConfig.COMMON.gildedHydraulicCrosshairDuration.get(),
                TaczCuriosConfig.COMMON.gildedHydraulicCrosshairMaxStacks.get());
        }
        // R-05 尖刃弹头
        if (GunTypeChecker.isHoldingDmgBoostGunType(player) && hasCurio(player, TaczItems.SHARP_BULLET.get())) {
            applyNonStackingBuff(player, TccMobEffects.SHARP_BULLET.get(), TaczCuriosConfig.COMMON.sharpBulletDuration.get());
        }
        // R-07 镀层分裂膛室
        if (GunTypeChecker.isHoldingDmgBoostGunType(player) && hasCurio(player, TaczItems.GILDED_SPLIT_CHAMBER.get())) {
            applyStackingBuff(player, TccMobEffects.GILDED_SPLIT_CHAMBER.get(),
                TaczCuriosConfig.COMMON.gildedSplitChamberDuration.get(),
                TaczCuriosConfig.COMMON.gildedSplitChamberMaxStacks.get());
        }
        // S-06 破片射击
        if (GunTypeChecker.isHoldingShotgun(player) && hasCurio(player, TaczItems.FRAGMENT_SHOT.get())) {
            applyNonStackingBuff(player, TccMobEffects.FRAGMENT_SHOT.get(), TaczCuriosConfig.COMMON.fragmentShotDuration.get());
        }
        // S-08 镀层地狱弹膛
        if (GunTypeChecker.isHoldingShotgun(player) && hasCurio(player, TaczItems.GILDED_INFERNAL_CHAMBER.get())) {
            applyStackingBuff(player, TccMobEffects.GILDED_INFERNAL_CHAMBER.get(),
                TaczCuriosConfig.COMMON.gildedInfernalChamberDuration.get(),
                TaczCuriosConfig.COMMON.gildedInfernalChamberMaxStacks.get());
        }
        // P-08 尖锐子弹
        if (GunTypeChecker.isHoldingPistol(player) && hasCurio(player, TaczItems.SHARP_AMMO.get())) {
            applyNonStackingBuff(player, TccMobEffects.SHARP_AMMO.get(), TaczCuriosConfig.COMMON.sharpAmmoDuration.get());
        }
        // P-10 镀层弹头扩散
        if (GunTypeChecker.isHoldingPistol(player) && hasCurio(player, TaczItems.GILDED_BULLET_SPREAD.get())) {
            applyStackingBuff(player, TccMobEffects.GILDED_BULLET_SPREAD.get(),
                TaczCuriosConfig.COMMON.gildedBulletSpreadDuration.get(),
                TaczCuriosConfig.COMMON.gildedBulletSpreadMaxStacks.get());
        }
    }

    /**
     * 实体死亡 — 近战击杀触发Buff
     */
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;
        // M-05 镀层斩铁
        if (isHoldingMeleeWeapon(player) && hasCurio(player, TaczItems.GILDED_STEEL_SLASH.get())) {
            applyStackingBuff(player, TccMobEffects.GILDED_STEEL_SLASH.get(),
                TaczCuriosConfig.COMMON.gildedSteelSlashDuration.get(),
                TaczCuriosConfig.COMMON.gildedSteelSlashMaxStacks.get());
        }
    }

    // ========== Phase 4: LivingHurtEvent 有害效果乘算 ==========

    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW)
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) return;

        Entity source = event.getSource().getEntity();
        if (!(source instanceof Player player)) return;

        long harmfulCount = target.getActiveEffects().stream()
            .filter(e -> e.getEffect().getCategory() == MobEffectCategory.HARMFUL)
            .count();

        if (harmfulCount == 0) return;

        // R-06 镀层步枪才能: 手持步枪时，每负面效果直接乘算
        if (GunTypeChecker.isHoldingDmgBoostGunType(player) && hasCurio(player, TaczItems.GILDED_RIFLE_APTITUDE.get())) {
            double perHarmful = TaczCuriosConfig.COMMON.gildedRifleAptitudePerHarmful.get();
            double multiplier = 1.0 + harmfulCount * perHarmful;
            event.setAmount(event.getAmount() * (float)multiplier);
        }

        // S-07 镀层通晓霰弹枪: 手持霰弹枪时，每负面效果直接乘算
        if (GunTypeChecker.isHoldingShotgun(player) && hasCurio(player, TaczItems.GILDED_SHOTGUN_SAVVY.get())) {
            double perHarmful = TaczCuriosConfig.COMMON.gildedShotgunSavvyPerHarmful.get();
            double multiplier = 1.0 + harmfulCount * perHarmful;
            event.setAmount(event.getAmount() * (float)multiplier);
        }

        // P-09 镀层准确射手: 手持手枪时，每负面效果直接乘算
        if (GunTypeChecker.isHoldingPistol(player) && hasCurio(player, TaczItems.GILDED_MARKSMAN.get())) {
            double perHarmful = TaczCuriosConfig.COMMON.gildedMarksmanPerHarmful.get();
            double multiplier = 1.0 + harmfulCount * perHarmful;
            event.setAmount(event.getAmount() * (float)multiplier);
        }

        // M-06 异况超量: 手持近战时，每负面效果直接乘算（始终生效）
        if (isHoldingMeleeWeapon(player) && hasCurio(player, TaczItems.CONDITION_OVERLOAD.get())) {
            double perHarmful = TaczCuriosConfig.COMMON.conditionOverloadPerHarmful.get();
            double multiplier = 1.0 + harmfulCount * perHarmful;
            event.setAmount(event.getAmount() * (float)multiplier);
        }
    }
}
