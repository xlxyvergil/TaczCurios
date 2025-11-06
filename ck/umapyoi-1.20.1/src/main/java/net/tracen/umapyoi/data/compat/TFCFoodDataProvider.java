package net.tracen.umapyoi.data.compat;

import cn.mcmod_mmf.mmlib.data.compat.TFCFoodDefinitionProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.tracen.umapyoi.Umapyoi;
import net.tracen.umapyoi.item.ItemRegistry;

public class TFCFoodDataProvider extends TFCFoodDefinitionProvider {

	public TFCFoodDataProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, existingFileHelper, Umapyoi.MODID);
	}
	@Override
	public void addDatas() {
		ItemRegistry.ITEMS.getEntries().forEach(entry->{
			this.addData(entry.get());
		});
	}
}
