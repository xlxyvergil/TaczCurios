package com.xlxyvergil.tcc.items;

import com.xlxyvergil.tcc.api.items.IBindable;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.compat.maid.MaidCompat;
import com.xlxyvergil.tcc.evolution.EvolutionRegistry;
import com.xlxyvergil.tcc.helpers.ImaginaryResistanceHelper;
import com.xlxyvergil.tcc.items.materials.CollapseCrystal;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;

import java.util.List;


public abstract class BoundCurioItem extends BaseCurioItem implements IBindable {
    
    private static final String[] BOUND_SLOT_IDS = {"tcc_3rd", "tcc_tdk"};

    public BoundCurioItem(Properties properties) {
        super(properties);
    }

    
    protected boolean isBoundItem() {
        return true;
    }

    
    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        if (MaidCompat.isMaid(slotContext.entity())) {
            return DropRule.DEFAULT;
        }
        return DropRule.ALWAYS_KEEP;
    }

    
    public boolean requiresCollapseCrystal() {
        return isBoundItem();
    }

    
    private boolean isBoundSlot(SlotContext context) {
        String slotId = context.identifier();
        for (String boundSlot : BOUND_SLOT_IDS) {
            if (boundSlot.equals(slotId)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCollapseCrystal(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (isConsumableCrystal(stack)) {
                return true;
            }
        }
        return false;
    }

    
    private void consumeCollapseCrystal(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (isConsumableCrystal(stack)) {
                stack.shrink(1);
                return;
            }
        }
    }

    
    private static boolean isConsumableCrystal(ItemStack stack) {
        if (!(stack.getItem() instanceof CollapseCrystal)) return false;
        CompoundTag tag = stack.getTag();
        return tag == null || tag.isEmpty();
    }

    
    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        
        
        
        if (MaidCompat.isMaid(slotContext.entity()) && slotContext.entity().isDeadOrDying()) {
            return true;
        }
        Player player = isBoundItem() ? MaidCompat.resolveOwnerPlayer(slotContext.entity()) : null;
        if (player == null) {
            return true;
        }
        if (player.isCreative()) {
            return true;
        }
        if (isBoundSlot(slotContext) && hasCollapseCrystal(player)) {
            return true;
        }
        return false;
    }

    
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        
        boolean maidDead = MaidCompat.isMaid(slotContext.entity()) && slotContext.entity().isDeadOrDying();
        Player player = isBoundItem() ? MaidCompat.resolveOwnerPlayer(slotContext.entity()) : null;
        if (player != null && !player.isCreative() && isBoundSlot(slotContext) && !maidDead) {
            consumeCollapseCrystal(player);
        }
        super.onUnequip(slotContext, newStack, stack);
    }

    
    private void bindIfNeeded(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) {
            return;
        }
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.getBoolean("IsBound")) {
            return;
        }
        tag.putBoolean("IsBound", true);
        tag.putString("BoundPlayer", player.getStringUUID());
        tag.putString("BoundPlayerName", player.getGameProfile().getName());
    }

    
    private boolean canEquipOwner(SlotContext slotContext, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.getBoolean("IsBound")) {
            return true;
        }
        String bound = tag.getString("BoundPlayer");
        
        Player boundOwner = MaidCompat.resolveOwnerPlayer(slotContext.entity());
        if (boundOwner == null) {
            return true;
        }
        return boundOwner.getStringUUID().equals(bound);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        bindIfNeeded(slotContext, stack);
        ensureCapCounters(stack);
        super.onEquip(slotContext, prevStack, stack);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return canEquipOwner(slotContext, stack) && super.canEquip(slotContext, stack);
    }

    
    private static void ensureCapCounters(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        ResourceLocation key = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (key == null) {
            return;
        }
        String itemId = key.toString();
        CompoundTag tag = stack.getOrCreateTag();
        for (EvolutionRegistry.Rule rule : EvolutionRegistry.getRulesByTypeAndItemOrEmpty(EvolutionRegistry.RuleType.ATTRIBUTE, itemId)) {
            EvolutionRegistry.Progress progress = rule.progress;
            if (progress == null || progress.capCounterKey == null || progress.capCounterKey.isBlank()) {
                continue;
            }
            if (!tag.contains(progress.capCounterKey)) {
                tag.putDouble(progress.capCounterKey, 0.0);
            }
        }
    }

    
    protected void appendImaginaryResistance(ItemStack stack, List<Component> tooltip) {
        CompoundTag tag = stack.getTag();
        double total = 1.0 + ImaginaryResistanceHelper.getExtraResistanceFromProgress(tag);
        tooltip.add(Component.literal(""));
        tooltip.add(formatModifierTooltip(total, "%.0f",
                Component.translatable(TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get().getDescriptionId()))
                .withStyle(ChatFormatting.GOLD));
    }

    
    protected void appendBoundPlayer(ItemStack stack, List<Component> tooltip) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.getBoolean("IsBound")) {
            String boundPlayerName = tag.getString("BoundPlayerName");
            tooltip.add(Component.literal(""));
            tooltip.add(Component.translatable("tcc.tooltip.bound", boundPlayerName)
                    .withStyle(ChatFormatting.RED));
        }
    }
}
