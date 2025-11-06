package net.tracen.umapyoi.registry.training;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.tracen.umapyoi.utils.UmaSoulUtils;

public class RandomStatusSupport extends TrainingSupport {

    public RandomStatusSupport() {
        super();
    }

    @Override
    public boolean applySupport(ItemStack soul, RandomSource rand, SupportStack stack) {
    	for(int i = 0; i < stack.getLevel(); i++) {
	        int id = rand.nextInt(5);
			if (UmaSoulUtils.getMaxProperty(soul)[id] > UmaSoulUtils.getProperty(soul)[id]) {
	            UmaSoulUtils.getProperty(soul)[id] = Math.min(
	                    UmaSoulUtils.getMaxProperty(soul)[id],
	                    UmaSoulUtils.getProperty(soul)[id] + stack.getLevel());
	            return true;
	        }
    	}
		return false;
    }

}
