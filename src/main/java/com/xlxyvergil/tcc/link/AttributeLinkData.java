package com.xlxyvergil.tcc.link;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 单条属性联动规则。
 * <p>
 * 当 slot 槽位中存在注册名为 curio 的饰品时，
 * 读取该实体 source 属性总值 × ratio，
 * 以 operation 与各自独立的 id 作用到 target 属性。
 * 每条规则拥有独立 id，因此多条规则可同时生效且互不冲突。
 */
public record AttributeLinkData(String slot,
                                String curio,
                                String source,
                                String target,
                                double ratio,
                                AttributeModifier.Operation operation,
                                ResourceLocation id) {
}
