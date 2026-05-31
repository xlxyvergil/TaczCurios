package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.config.TaczCuriosConfig;
import com.xlxyvergil.tcc.registries.TaczItems;
import com.xlxyvergil.tcc.registries.TccMobEffects;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = TaczCurios.MODID)
public class CurioCombatEventHandler {

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

    /**
     * 有害效果乘算：R-06/S-07/P-09/M-06 在 LivingHurtEvent 直接乘算伤害
     */
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
