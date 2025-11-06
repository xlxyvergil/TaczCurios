package artifacts.fabric.mixin.item.wearable.pickaxeheater;

import artifacts.item.wearable.hands.PickaxeHeaterItem;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootTable.class)
public class LootTableMixin {

    @ModifyReturnValue(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", at = @At("RETURN"))
    public ObjectArrayList<ItemStack> getDrops(ObjectArrayList<ItemStack> original, LootContext context) {
        return PickaxeHeaterItem.getModifiedBlockDrops(original, context, ConventionalBlockTags.ORES, ConventionalItemTags.RAW_ORES);
    }
}
