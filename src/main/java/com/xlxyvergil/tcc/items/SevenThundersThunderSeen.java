package com.xlxyvergil.tcc.items;

import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.core.TccDamageSources;
import com.xlxyvergil.tcc.util.AttributeHelper;
import com.xlxyvergil.tcc.util.BaseCurioItem;
import com.xlxyvergil.tcc.util.CurioSearchHelper;
import com.xlxyvergil.tcc.util.GunTypeChecker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SevenThundersThunderSeen extends BaseCurioItem {

    private static final UUID HEADSHOT_MULTIPLIER_UUID = UUID.fromString("de0a7b0e-ec6f-45e5-8e3a-7f2d8f159f15");
    private static final UUID CRIT_CHANCE_UUID = UUID.fromString("e6e6a5a6-5b3b-4d79-8dbd-9b9c31a6f0f4");
    private static final UUID CRIT_DAMAGE_UUID = UUID.fromString("0f7f3eaa-8db2-4f8c-9f51-f06c9c0b0f17");

    private static final String PROC_KEY = "tcc_seven_thunders_thunder_seen_proc";
    private static final String PROC_USED_KEY = "tcc_seven_thunders_thunder_seen_proc_used";

    public SevenThundersThunderSeen(Properties properties) {
        super(properties);
    }

    @Override
    protected void applyEffects(LivingEntity livingEntity) {
        if (GunTypeChecker.isHoldingSniper(livingEntity)) {
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.HEADSHOT_MULTIPLIER, 2.0, HEADSHOT_MULTIPLIER_UUID,
                "tcc.seven_thunders_thunder_seen.headshot_multiplier", AttributeModifier.Operation.ADDITION);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_CHANCE, 0.5, CRIT_CHANCE_UUID,
                "tcc.seven_thunders_thunder_seen.crit_chance", AttributeModifier.Operation.MULTIPLY_BASE);
            AttributeHelper.applyModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, 1.0, CRIT_DAMAGE_UUID,
                "tcc.seven_thunders_thunder_seen.crit_damage", AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            removeEffects(livingEntity);
        }
    }

    @Override
    protected void removeEffects(LivingEntity livingEntity) {
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.HEADSHOT_MULTIPLIER, HEADSHOT_MULTIPLIER_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_CHANCE, CRIT_CHANCE_UUID);
        AttributeHelper.removeModifier(livingEntity, AttributeHelper.CRIT_DAMAGE, CRIT_DAMAGE_UUID);
    }

    @Override
    public void applyGunSwitchEffect(LivingEntity livingEntity) {
        applyEffects(livingEntity);
    }

    public static boolean hasEquipped(LivingEntity livingEntity) {
        return !CurioSearchHelper.findFirstEquippedStack(livingEntity, stack -> stack.getItem() instanceof SevenThundersThunderSeen).isEmpty();
    }

    @SubscribeEvent
    public static void onGunHurtPre(EntityHurtByGunEvent.Pre event) {
        LivingEntity attacker = event.getAttacker();
        if (attacker == null || !hasEquipped(attacker)) return;
        if (!(attacker.level() instanceof ServerLevel)) return;
        if (!GunTypeChecker.isHoldingSniper(attacker)) return;
        if (!event.isHeadShot()) return;

        if (attacker.getRandom().nextFloat() >= 0.5f) return;

        event.setDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING,
            TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));
        event.setDamageSource(GunDamageSourcePart.ARMOR_PIERCING,
            TccDamageSources.imaginaryDamage(attacker.level(), event.getBullet(), attacker));

        if (event.getBullet() != null) {
            event.getBullet().getPersistentData().putBoolean(PROC_KEY, true);
            event.getBullet().getPersistentData().putBoolean(PROC_USED_KEY, false);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide || target.isDeadOrDying()) return;

        DamageSource source = event.getSource();
        if (!source.is(TccDamageSources.IMAGINARY_DAMAGE_TAG)) return;
        if (!(source.getEntity() instanceof LivingEntity attacker)) return;
        if (!hasEquipped(attacker)) return;
        if (!GunTypeChecker.isHoldingSniper(attacker)) return;

        Entity bullet = source.getDirectEntity();
        if (bullet == null) return;

        var data = bullet.getPersistentData();
        if (!data.getBoolean(PROC_KEY) || data.getBoolean(PROC_USED_KEY)) return;

        float extra = target.getMaxHealth() * 0.2f;
        if (extra > 0) {
            target.setHealth(Math.max(0, target.getHealth() - extra));
        }
        data.putBoolean(PROC_USED_KEY, true);
    }
}
