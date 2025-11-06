package net.tracen.umapyoi.registry;

import java.util.function.Supplier;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import net.tracen.umapyoi.Umapyoi;
import net.tracen.umapyoi.registry.training.BorealisSupport;
import net.tracen.umapyoi.registry.training.ExtraStatusSupport;
import net.tracen.umapyoi.registry.training.RandomStatusSupport;
import net.tracen.umapyoi.registry.training.SkillSupport;
import net.tracen.umapyoi.registry.training.StatusSupport;
import net.tracen.umapyoi.registry.training.TrainingSupport;
import net.tracen.umapyoi.utils.UmaStatusUtils.StatusType;

public class TrainingSupportRegistry {
    public static final DeferredRegister<TrainingSupport> SUPPORTS = DeferredRegister
            .create(TrainingSupport.REGISTRY_KEY, Umapyoi.MODID);

    public static final Supplier<IForgeRegistry<TrainingSupport>> REGISTRY = SUPPORTS
            .makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<TrainingSupport> SPEED_SUPPORT = SUPPORTS.register("speed_support",
            () -> new StatusSupport(StatusType.SPEED));

    public static final RegistryObject<TrainingSupport> STAMINA_SUPPORT = SUPPORTS.register("stamina_support",
            () -> new StatusSupport(StatusType.STAMINA));

    public static final RegistryObject<TrainingSupport> STRENGTH_SUPPORT = SUPPORTS.register("strength_support",
            () -> new StatusSupport(StatusType.STRENGTH));

    public static final RegistryObject<TrainingSupport> GUTS_SUPPORT = SUPPORTS.register("guts_support",
            () -> new StatusSupport(StatusType.GUTS));

    public static final RegistryObject<TrainingSupport> WISDOM_SUPPORT = SUPPORTS.register("wisdom_support",
            () -> new StatusSupport(StatusType.WISDOM));

    public static final RegistryObject<TrainingSupport> SKILL_SUPPORT = SUPPORTS.register("skill_support",
            SkillSupport::new);
    
    public static final RegistryObject<TrainingSupport> AP_SUPPORT = SUPPORTS.register("actionpoint_support",
            () -> new ExtraStatusSupport(3));
    
    public static final RegistryObject<TrainingSupport> MEMORY_SUPPORT = SUPPORTS.register("memory_support",
            () -> new ExtraStatusSupport(2));
    
    public static final RegistryObject<TrainingSupport> RANDOM_STATUS_SUPPORT = SUPPORTS.register("random_status_support",
            RandomStatusSupport::new);

    public static final RegistryObject<TrainingSupport> ACUPUNCTUIST_SUPPORT = SUPPORTS.register("acupuncturist_support",
            BorealisSupport::new);

}
