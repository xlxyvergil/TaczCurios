package com.xlxyvergil.tcc.link;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

/**
 * 单条属性联动规则。
 * <p>
 * 当 {@code slot} 槽位中存在注册名为 {@code curio} 的饰品时，
 * 读取该实体 {@code source} 属性总值 × {@code ratio}，
 * 以 {@code operation} 与各自独立的 {@code uuid} 作用到 {@code target} 属性。
 * 每条规则拥有独立 uuid，因此多条规则可同时生效且互不冲突。
 */
public record AttributeLinkData(String slot,
                                String curio,
                                String source,
                                String target,
                                double ratio,
                                AttributeModifier.Operation operation,
                                UUID uuid) {
}
