package net.tracen.umapyoi.data.compat;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.compress.utils.Lists;

import com.google.gson.JsonObject;

import cn.mcmod_mmf.mmlib.data.compat.BetterCombatWeaponAttributesProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.tracen.umapyoi.Umapyoi;
import net.tracen.umapyoi.item.ItemRegistry;

public class BetterCombatProvider extends BetterCombatWeaponAttributesProvider {

	public BetterCombatProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, existingFileHelper, Umapyoi.MODID);
	}

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        this.datas.clear();
        this.addNewData();
        final Path outputFolder = output.getOutputFolder();
        List<CompletableFuture<?>> futureList = Lists.newArrayList();

        this.datas.forEach( (loc, data) -> {
            String pathString = String.join("/", PackType.SERVER_DATA.getDirectory(), loc.getNamespace(), "weapon_attributes", loc.getPath()+".json");
            Path path = outputFolder.resolve(pathString);

            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("parent", data);

            futureList.add(DataProvider.saveStable(cache, jsonObj, path));
        });
        return CompletableFuture.allOf(futureList.stream().toArray(CompletableFuture<?>[]::new));
    }

	private void addNewData() {
		this.addData(ItemRegistry.BASEBALL_BAT.get(), "bettercombat:mace");
		this.addData(ItemRegistry.NAGINATA.get(), "bettercombat:glaive");
	}
}
