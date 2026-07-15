package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

// 参照 AE2 InitBlocks：不用 DeferredRegister，而是在 RegisterEvent<BLOCK> 时直接注册
public final class TccBlocks {

    public static final Block TESHIN_WORKBENCH = new Block(
        BlockBehaviour.Properties.copy(Blocks.DIRT).strength(0.5F).lightLevel(state -> 8));
    public static final Item TESHIN_WORKBENCH_ITEM = new BlockItem(TESHIN_WORKBENCH, new Item.Properties());

    private TccBlocks() {
    }

    public static void init(IForgeRegistry<Block> blockRegistry) {
        blockRegistry.register(id("teshin_workbench"), TESHIN_WORKBENCH);
        ForgeRegistries.ITEMS.register(id("teshin_workbench"), TESHIN_WORKBENCH_ITEM);
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(TaczCurios.MODID, path);
    }
}
