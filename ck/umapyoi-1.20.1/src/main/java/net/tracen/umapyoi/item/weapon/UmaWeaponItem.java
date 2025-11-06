package net.tracen.umapyoi.item.weapon;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.tracen.umapyoi.api.UmapyoiAPI;

public class UmaWeaponItem extends TieredItem implements Vanishable {
	private final float attackDamage;
	/** Modifiers applied when the item is in the mainhand of a user. */
	private final Multimap<Attribute, AttributeModifier> defaultModifiers;

	public UmaWeaponItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Item.Properties pProperties) {
		super(pTier, pProperties);
		this.attackDamage = (float) pAttackDamageModifier + pTier.getAttackDamageBonus();
		Multimap<Attribute, AttributeModifier> result = ArrayListMultimap.create();
		result.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
				(double) this.attackDamage, AttributeModifier.Operation.ADDITION));
		result.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
				(double) pAttackSpeedModifier, AttributeModifier.Operation.ADDITION));
		this.defaultModifiers = result;
	}

	public float getDamage() {
		return this.attackDamage;
	}

	public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
		return !pPlayer.isCreative();
	}
	
	@Override
	public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {

		super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
		if (pStack == null)
			return;
		if (pEntity == null)
			return;
		if(!pIsSelected)
			return;
		
		if (pEntity instanceof LivingEntity living) {
			ItemStack soul = UmapyoiAPI.getUmaSoul(living);
			if(soul.isEmpty()) {
				living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 1));
				living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
			}
		}
	}
	
	/**
	 * Current implementations of this method in child classes do not use the entry
	 * argument beside ev. They just raise the damage on the stack.
	 */
	public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
		pStack.hurtAndBreak(1, pAttacker, (p_43296_) -> {
			p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
		});
		return true;
	}

	/**
	 * Called when a {@link net.minecraft.world.level.block.Block} is destroyed
	 * using this Item. Return {@code true} to trigger the "Use Item" statistic.
	 */
	public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos,
			LivingEntity pEntityLiving) {
		if (pState.getDestroySpeed(pLevel, pPos) != 0.0F) {
			pStack.hurtAndBreak(2, pEntityLiving, (p_43276_) -> {
				p_43276_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
			});
		}

		return true;
	}

	/**
	 * Gets a map of item attribute modifiers, used by ItemSword to increase hit
	 * damage.
	 */

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		return slot == EquipmentSlot.MAINHAND ? this.defaultModifiers
				: super.getAttributeModifiers(slot, stack);
	}
}
