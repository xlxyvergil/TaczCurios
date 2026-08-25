package com.xlxyvergil.tcc.util;

import com.xlxyvergil.tcc.registries.TccItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 崩坏结晶的收集记录数据工具。
 * <p>
 * 崩坏结晶用于合成真我（逐火之蛾）与黑渊白花（神之键）；
 * 通过吸收（记录）素材饰品类型来累计，凑满 {@link #REQUIRED_COUNT} 种不同饰品即可合成。
 * 每种素材饰品类型只被记录一次（已记录的无法再次合成）。
 * <p>
 * NBT 存两组记录集合（ListTag&lt;String&gt;，存物品注册 ID）：
 * <ul>
 *   <li>{@link #TRUE_SELF_MATERIALS} → 真我素材（tcc_3rd 的 12 个 3 阶饰品），记录到 {@link #TAG_TRUE_SELF}</li>
 *   <li>{@link #HEIYUAN_BAIHUA_MATERIALS} → 黑渊白花素材（tcc_tdk 的 12 个 3 阶饰品），记录到 {@link #TAG_HEIYUAN}</li>
 * </ul>
 */
public class CollapseCrystalData {

    /** 合成所需的不同素材种类数 */
    public static final int REQUIRED_COUNT = 12;

    private static final String TAG_TRUE_SELF = "tcc_recorded_true_self";
    private static final String TAG_HEIYUAN = "tcc_recorded_heiyuan";

    /** 真我素材（逐火之蛾 tcc_3rd 的 12 个 3 阶饰品） */
    public static final TagKey<Item> TRUE_SELF_MATERIALS = TagKey.create(Registries.ITEM,
            new ResourceLocation("tcc", "true_self_materials"));
    /** 黑渊白花素材（神之键 tcc_tdk 的 12 个 3 阶饰品） */
    public static final TagKey<Item> HEIYUAN_BAIHUA_MATERIALS = TagKey.create(Registries.ITEM,
            new ResourceLocation("tcc", "heiyuan_baihua_materials"));

    private CollapseCrystalData() {
    }

    /** 判断物品是否为崩坏结晶 */
    public static boolean isCrystal(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == TccItems.COLLAPSE_CRYSTAL;
    }

    /** 返回该素材饰品归属的组（真我 or 黑渊白花）；不属于任何组时返回 null */
    public static TagKey<Item> groupOf(ItemStack stack) {
        if (!stack.isEmpty() && stack.is(TRUE_SELF_MATERIALS)) return TRUE_SELF_MATERIALS;
        if (!stack.isEmpty() && stack.is(HEIYUAN_BAIHUA_MATERIALS)) return HEIYUAN_BAIHUA_MATERIALS;
        return null;
    }

    /** 读取水晶某一组的已记录种类数 */
    public static int getRecordedCount(ItemStack stack, TagKey<Item> group) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return 0;
        return tag.getList(groupKey(group), Tag.TAG_STRING).size();
    }

    /** 该素材饰品类型是否已被水晶记录 */
    public static boolean isRecorded(ItemStack stack, TagKey<Item> group, Item item) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return false;
        ListTag list = tag.getList(groupKey(group), Tag.TAG_STRING);
        String path = itemPath(item);
        for (int i = 0; i < list.size(); i++) {
            if (list.getString(i).equals(path)) return true;
        }
        return false;
    }

    /** 记录一种素材饰品到水晶，同类型去重 */
    public static void record(ItemStack stack, TagKey<Item> group, Item item) {
        CompoundTag tag = stack.getOrCreateTag();
        String key = groupKey(group);
        ListTag list = tag.getList(key, Tag.TAG_STRING);
        String path = itemPath(item);
        for (int i = 0; i < list.size(); i++) {
            if (list.getString(i).equals(path)) return;
        }
        list.add(StringTag.valueOf(path));
        tag.put(key, list);
    }

    private static String groupKey(TagKey<Item> group) {
        return group == TRUE_SELF_MATERIALS ? TAG_TRUE_SELF : TAG_HEIYUAN;
    }

    private static String itemPath(Item item) {
        ResourceLocation name = ForgeRegistries.ITEMS.getKey(item);
        return name != null ? name.toString() : "unknown";
    }
}
