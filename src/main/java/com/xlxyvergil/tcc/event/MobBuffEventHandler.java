package com.xlxyvergil.tcc.event;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.attribute.TccAttributes;
import com.xlxyvergil.tcc.util.AttributeHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

/**
 * 怪物虚数抗性事件处理器。
 * 非玩家实体生成时添加持久化的虚数抗性属性修饰符（30点，不可被牛奶等效果移除）。
 */
@Mod.EventBusSubscriber(modid = TaczCurios.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobBuffEventHandler {

    /** 持久化虚数抗性修饰符的固定UUID */
    private static final UUID RESISTANCE_MODIFIER_UUID =
            UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof LivingEntity living)) return;

        // 仅对非玩家实体生效
        if (living instanceof Player) return;

        // 持久化的虚数抗性修饰符（30点，重复进入世界时幂等）
        AttributeHelper.applyModifier(
                living,
                TccAttributes.IMAGINARY_DAMAGE_RESISTANCE.get(),
                30.0,
                RESISTANCE_MODIFIER_UUID,
                "Mob Imaginary Resistance",
                AttributeModifier.Operation.ADDITION
        );
    }
}
