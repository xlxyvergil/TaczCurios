package com.xlxyvergil.tcc.link;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 单条属性联动规则。
 * 当固定槽位 1 号槽位装着的饰品 id 命中 {@code curio} 时，
 * 读取该实体 {@code attribute} 属性总值 × {@code ratio}，按 {@code operation} 作用到 TAA 通用枪伤。
 */
public record AttributeLinkData(String curio, String attribute, double ratio, AttributeModifier.Operation operation) {
}
