package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import net.minecraft.resources.ResourceLocation;
/**
 * 非玩家实体生成时添加持久化虚数抗性修饰符（牛奶等效果不可移除）。
 */
@EventBusSubscriber(modid = TaczCurios.MODID)
public class MobBuffEventHandler {

    /** 持久化虚数抗性修饰符的固定 ID */
    private static final ResourceLocation RESISTANCE_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(TaczCurios.MODID, "mob_buff_event_handler_a1b2c3d4_7890");

    /** 非玩家实体持久化虚数抗性值 */
    private static final double RESISTANCE_VALUE = 40.0;

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof LivingEntity living)) return;

        // 仅对非玩家实体生效
        if (living instanceof Player) return;

        // 持久化的虚数抗性修饰符（重复进入世界时幂等）
        AttributeHelper.applyModifier(
                living,
                TccAttributes.IMAGINARY_DAMAGE_RESISTANCE,
                RESISTANCE_VALUE,
                RESISTANCE_MODIFIER_ID,
                AttributeModifier.Operation.ADD_VALUE
        );
    }
}
