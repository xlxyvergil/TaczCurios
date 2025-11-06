package net.tracen.umapyoi.item.weapon;

import java.util.UUID;

import com.google.common.collect.Multimap;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeMod;
import net.tracen.umapyoi.Umapyoi;
import net.tracen.umapyoi.item.ItemRegistry;

public class GrassNaginataItem extends UmaWeaponItem {
	private static final UUID REACH_UUID = UUID.fromString("1F199A02-626F-13A3-2365-3D4D6D075737");

	public GrassNaginataItem() {
		super(new NaginataTier(), 7, -2.7F, Umapyoi.defaultItemProperties().stacksTo(1));
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		Multimap<Attribute, AttributeModifier> attributeModifiers = super.getAttributeModifiers(slot, stack);
		if (slot == EquipmentSlot.MAINHAND) {
			AttributeModifier value = new AttributeModifier(REACH_UUID, "Weapon modifier", 2D,
					AttributeModifier.Operation.ADDITION);
			if(!attributeModifiers.containsValue(value))
				attributeModifiers.put(ForgeMod.ENTITY_REACH.get(), value);
		}
		return attributeModifiers;
	}

	private static class NaginataTier implements Tier {

		@Override
		public int getUses() {
			return 1561;
		}

		@Override
		public float getSpeed() {
			return 0F;
		}

		@Override
		public float getAttackDamageBonus() {
			return 1F;
		}

		@Override
		public int getLevel() {
			return 0;
		}

		@Override
		public int getEnchantmentValue() {
			return 20;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.of(ItemRegistry.HORSESHOE_RAINBOW.get());
		}

	}
}
