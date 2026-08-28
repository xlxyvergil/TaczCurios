---
alwaysApply: true
scene: attribute_tooltip
---

属性修饰符在物品 tooltip 中的展示必须统一使用 `BaseCurioItem.formatModifierTooltip(...)`。

- 通过 `Attributes.*` / `TccAttributes.*` / `ALObjects.Attributes.*` 等属性赋予的修饰符，一律用：
  `formatModifierTooltip(value, "%.0f", Component.translatable(attribute.getDescriptionId()))`，并 `.withStyle(ChatFormatting.GOLD)`。
- 不要为单个属性修饰符新建自定义 lang 键（如 `xxx.knockback_resistance`、`xxx.creative_flight`），统一复用
  `attributeslib.modifier.plus` / `attributeslib.modifier.take` 的显示格式。
- 布尔属性（如 `attributeslib:creative_flight`）同样用 `formatModifierTooltip(1.0, "%.0f", ...)`。
- 非属性类的效果说明（如减伤比例、免疫有害效果、结界触发等）仍可用自定义 lang 键。
