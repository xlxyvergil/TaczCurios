package net.tracen.umapyoi.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;

public abstract class FindUmaSoulEvent extends UmaSoulEvent {
	private final LivingEntity owner;
	public FindUmaSoulEvent(LivingEntity entity, ItemStack soul) {
		super(soul);
		this.owner = entity;
	}
	
	public LivingEntity getLivingEntity() {
		return owner;
	}

	@Cancelable
	public static class Pre extends FindUmaSoulEvent{
		public Pre(LivingEntity entity) {
			super(entity, ItemStack.EMPTY);
		}
	}

	public static class Post extends FindUmaSoulEvent{
		public Post(LivingEntity entity, ItemStack stack) {
			super(entity, stack);
		}
	}
}
