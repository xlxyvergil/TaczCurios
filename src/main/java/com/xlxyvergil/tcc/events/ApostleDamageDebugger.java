package com.xlxyvergil.tcc.events;

import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "tcc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ApostleDamageDebugger {

    private static final String P = "&d[TACZ=>Apostle]&r ";

    private static final Map<Integer, Hit> M = new HashMap<>();

    private static class Hit {
        int atk, hurt, dmg;
        float hp;
    }

    private static Hit h(Apostle a) {
        return M.computeIfAbsent(a.getId(), k -> new Hit());
    }

    private static void msg(LivingEntity e, String s) {
        if (e.level().isClientSide) return;
        for (Player p : e.level().players())
            p.sendSystemMessage(Component.literal(P + e.getName().getString() + ": " + s));
    }

    private static String ty(net.minecraft.world.damagesource.DamageSource s) {
        if (s == null) return "null";
        try { return s.type().msgId(); } catch (Exception ex) { return "err"; }
    }

    private static String f(float v) { return String.format("%.1f", v); }

    // ==================== 1. TACZ Pre ====================

    @SubscribeEvent
    public static void onPre(EntityHurtByGunEvent.Pre evt) {
        if (!(evt.getHurtEntity() instanceof Apostle a)) return;
        if (evt.getLogicalSide().isClient()) return;

        Hit t = h(a);
        t.atk = 0; t.hurt = 0; t.dmg = 0; t.hp = a.getHealth();

        var n = evt.getDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING);
        var p = evt.getDamageSource(GunDamageSourcePart.ARMOR_PIERCING);

        msg(a, "=== [枪械Pre] 基础=" + f(evt.getBaseAmount())
                + " 爆头倍率=" + f(evt.getHeadshotMultiplier())
                + " 爆头=" + evt.isHeadShot());
        msg(a, "  第1段(普通) 伤害类型=" + ty(n));
        msg(a, "  第2段(穿甲) 伤害类型=" + ty(p));
        msg(a, "  攻击者=" + (evt.getAttacker() != null ? evt.getAttacker().getName().getString() : "null"));
        msg(a, "  自定义无敌=" + a.moddedInvul
                + " 黑曜石无敌=" + a.obsidianInvul
                + " 血量=" + f(a.getHealth()));
        msg(a, "  受伤计数=" + a.getHitTimes()
                + " 传送阈值=" + a.hitTimeTeleport()
                + " 第二阶段=" + a.isSettingUpSecond()
                + " 无AI=" + a.isNoAi());
        msg(a, "  地狱=" + a.isInNether()
                + " 濒死=" + a.isDeadOrDying()
                + " 冷却=" + a.coolDown);
        msg(a, "  绕过冷却=" + n.is(DamageTypeTags.BYPASSES_COOLDOWN)
                + " 绕过无敌=" + n.is(DamageTypeTags.BYPASSES_INVULNERABILITY));
    }

    // ==================== 2. LivingAttackEvent ====================

    @SubscribeEvent
    public static void onAttack(LivingAttackEvent evt) {
        if (!(evt.getEntity() instanceof Apostle a)) return;
        if (a.level().isClientSide) return;
        Hit t = h(a);
        t.atk++;
        msg(a, "  [攻击#" + t.atk + "] " + ty(evt.getSource())
                + " 伤害=" + f(evt.getAmount())
                + " " + (evt.isCanceled() ? "拦截" : "通过")
                + " 自定义无敌=" + a.moddedInvul
                + " 黑曜石无敌=" + a.obsidianInvul);
    }

    // ==================== 3. LivingHurtEvent ====================

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent evt) {
        if (!(evt.getEntity() instanceof Apostle a)) return;
        if (a.level().isClientSide) return;
        Hit t = h(a);
        t.hurt++;
        msg(a, "  [受伤#" + t.hurt + "] " + ty(evt.getSource())
                + " 伤害=" + f(evt.getAmount())
                + " 自定义无敌=" + a.moddedInvul
                + " 血量=" + f(a.getHealth()));
    }

    // ==================== 4. LivingDamageEvent ====================

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent evt) {
        if (!(evt.getEntity() instanceof Apostle a)) return;
        if (a.level().isClientSide) return;
        Hit t = h(a);
        t.dmg++;
        float n = a.getHealth() - evt.getAmount();
        msg(a, "  [扣血#" + t.dmg + "] " + ty(evt.getSource())
                + " 最终=" + f(evt.getAmount())
                + " 自定义无敌=" + a.moddedInvul
                + " 血量 " + f(a.getHealth()) + "->" + f(n));
    }

    // ==================== 5. TACZ Post ====================

    @SubscribeEvent
    public static void onPost(EntityHurtByGunEvent.Post evt) {
        if (!(evt.getHurtEntity() instanceof Apostle a)) return;
        if (evt.getLogicalSide().isClient()) return;
        Hit t = h(a);
        msg(a, "=== [枪械Post] 攻击=" + t.atk + " 受伤=" + t.hurt + " 扣血=" + t.dmg + " (期望2+2+2)");
        msg(a, "  自定义无敌=" + a.moddedInvul + " 黑曜石无敌=" + a.obsidianInvul
                + " 血量 " + f(t.hp) + "->" + f(a.getHealth())
                + " 差值=" + f(a.getHealth() - t.hp));
        msg(a, "  受伤计数=" + a.getHitTimes() + " 传送阈值=" + a.hitTimeTeleport() + " 地狱=" + a.isInNether());
    }
}
