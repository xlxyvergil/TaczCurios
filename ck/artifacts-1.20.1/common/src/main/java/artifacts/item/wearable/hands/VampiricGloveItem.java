package artifacts.item.wearable.hands;

import artifacts.item.wearable.WearableArtifactItem;
import artifacts.registry.ModGameRules;
import artifacts.registry.ModItems;
import artifacts.util.DamageSourceHelper;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class VampiricGloveItem extends WearableArtifactItem {

    @Override
    public boolean hasNonCosmeticEffects() {
        return ModGameRules.VAMPIRIC_GLOVE_ABSORPTION_RATIO.get() > 0
                && ModGameRules.VAMPIRIC_GLOVE_MAX_HEALING_PER_HIT.get() > 0
                && ModGameRules.VAMPIRIC_GLOVE_ABSORPTION_CHANCE.get() > 0;
    }

    @Override
    protected void addEffectsTooltip(ItemStack stack, List<MutableComponent> tooltip) {
        if (ModGameRules.VAMPIRIC_GLOVE_ABSORPTION_CHANCE.fuzzyEquals(1)) {
            tooltip.add(tooltipLine("constant"));
        } else {
            tooltip.add(tooltipLine("chance"));
        }
    }

    public static void onLivingDamage(LivingEntity entity, DamageSource damageSource, float amount) {
        LivingEntity attacker = DamageSourceHelper.getAttacker(damageSource);
        if (attacker != null
                && ModItems.VAMPIRIC_GLOVE.get().isEquippedBy(attacker)
                && DamageSourceHelper.isMeleeAttack(damageSource)
        ) {
            int maxHealthAbsorbed = ModGameRules.VAMPIRIC_GLOVE_MAX_HEALING_PER_HIT.get();
            double absorptionRatio = ModGameRules.VAMPIRIC_GLOVE_ABSORPTION_RATIO.get();
            double absorptionProbability = ModGameRules.VAMPIRIC_GLOVE_ABSORPTION_CHANCE.get();

            float damageDealt = Math.min(amount, entity.getHealth());
            float damageAbsorbed = Math.min(maxHealthAbsorbed, (float) absorptionRatio * damageDealt);

            if (damageAbsorbed > 0 && entity.getRandom().nextFloat() < absorptionProbability) {
                attacker.heal(damageAbsorbed);
            }
        }
    }
}
