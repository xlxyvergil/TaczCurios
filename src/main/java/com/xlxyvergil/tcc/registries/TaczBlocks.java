package com.xlxyvergil.tcc.registries;

import com.xlxyvergil.tcc.TaczCurios;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TaczBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TaczCurios.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TaczCurios.MODID);

    // 注册Teshin工作台方块，使用与泥土相同的属性
    public static final RegistryObject<Block> TESHIN_WORKBENCH = BLOCKS.register("teshin_workbench", 
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIRT).strength(0.5F)));
    
    // 为方块注册物品
    public static final RegistryObject<Item> TESHIN_WORKBENCH_ITEM = ITEMS.register("teshin_workbench", 
        () -> new BlockItem(TESHIN_WORKBENCH.get(), new Item.Properties()));
        
    public static void init() {}
}