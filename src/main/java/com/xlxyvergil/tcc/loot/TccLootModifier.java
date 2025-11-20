package com.xlxyvergil.tcc.loot;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xlxyvergil.tcc.TaczCurios;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Random;

public class TccLootModifier extends LootModifier {
    private static final ResourceLocation TCC_CURIOS_CHEST = new ResourceLocation(TaczCurios.MODID, "tcc_curios_chest");
    
    public static final Codec<TccLootModifier> CODEC = RecordCodecBuilder.create(builder -> codecStart(builder)
            .apply(builder, TccLootModifier::new));

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the loot conditions that need to be matched before the loot is modified.
     */
    protected TccLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        // 使用1-100的随机数控制饰品生成：
        // 1-50时不生成饰品（50%概率）
        // 51-85时生成一个饰品（35%概率）
        // 86-100时生成两个饰品（15%概率）
        Random random = new Random();
        int randomNumber = random.nextInt(100) + 1; // 生成1-100之间的随机整数
        int rolls = 0;
        
        if (randomNumber >= 51 && randomNumber <= 85) {
            rolls = 1; // 51-85时roll1次
        } else if (randomNumber >= 86 && randomNumber <= 100) {
            rolls = 2; // 86-100时roll2次
        }
        // 1-50时rolls保持为0，不生成饰品

        // 根据roll次数添加饰品
        for (int i = 0; i < rolls; i++) {
            // 从我们的战利品表中获取随机饰品
            context.getLevel().getServer().getLootData().getLootTable(TCC_CURIOS_CHEST).getRandomItems(context, generatedLoot::add);
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}