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
 * 崩坏结晶吸收不同素材饰品累计，凑满 REQUIRED_COUNT 种即可合成（每种只记录一次）。
 * NBT 用 ListTag 存物品注册 ID 分两组记录：真我素材（逐火之蛾 tcc_3rd 的 12 个 3 阶饰品）与
 * 黑渊白花素材（神之键 tcc_tdk 的 12 个 3 阶饰品），分别存入 TAG_TRUE_SELF / TAG_HEIYUAN。
 */
public class CollapseCrystalData {

    /** 合成所需的不同素材种类数 */
    public static final int REQUIRED_COUNT = 12;

    private static final String TAG_TRUE_SELF = "tcc_recorded_true_self";
    private static final String TAG_HEIYUAN = "tcc_recorded_heiyuan";
    /** 水晶绑定的目标组（可能是 true_self 或 heiyuan），记录后只能与该组合成，防止两组混记录导致进度错乱 */
    private static final String TAG_ACTIVE_GROUP = "tcc_recorded_group";

    /** 真我素材（逐火之蛾 tcc_3rd 的 12 个 3 阶饰品） */
    public static final TagKey<Item> TRUE_SELF_MATERIALS = TagKey.create(Registries.ITEM,
            new ResourceLocation("tcc", "true_self_materials"));
    /** 黑渊白花素材（神之键 tcc_tdk 的 12 个 3 阶饰品） */
    public static final TagKey<Item> HEIYUAN_BAIHUA_MATERIALS = TagKey.create(Registries.ITEM,
            new ResourceLocation("tcc", "heiyuan_baihua_materials"));

    private CollapseCrystalData() {
    }

    public static boolean isCrystal(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == TccItems.COLLAPSE_CRYSTAL;
    }

    /** 返回素材归属的组（真我/黑渊白花），不属于任何组时为 null。 */
    public static TagKey<Item> groupOf(ItemStack stack) {
        if (!stack.isEmpty() && stack.is(TRUE_SELF_MATERIALS)) return TRUE_SELF_MATERIALS;
        if (!stack.isEmpty() && stack.is(HEIYUAN_BAIHUA_MATERIALS)) return HEIYUAN_BAIHUA_MATERIALS;
        return null;
    }

    public static int getRecordedCount(ItemStack stack, TagKey<Item> group) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return 0;
        return tag.getList(groupKey(group), Tag.TAG_STRING).size();
    }

    /** 水晶当前绑定的目标组；null 表示尚未绑定，可任选一组开始记录。 */
    public static TagKey<Item> getBoundGroup(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_ACTIVE_GROUP)) return null;
        String g = tag.getString(TAG_ACTIVE_GROUP);
        if ("true_self".equals(g)) return TRUE_SELF_MATERIALS;
        if ("heiyuan".equals(g)) return HEIYUAN_BAIHUA_MATERIALS;
        return null;
    }

    /** 绑定水晶目标组；绑定后只能与该组合成（初次合成时调用）。 */
    public static void bindGroup(ItemStack stack, TagKey<Item> group) {
        String value = group == TRUE_SELF_MATERIALS ? "true_self" : "heiyuan";
        stack.getOrCreateTag().putString(TAG_ACTIVE_GROUP, value);
    }

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
