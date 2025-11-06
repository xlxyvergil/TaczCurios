package net.tracen.umapyoi.recipe;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.tracen.umapyoi.Umapyoi;
import net.tracen.umapyoi.utils.GachaRanking;
import net.tracen.umapyoi.utils.UmaSoulUtils;

public class RequestUma {
	private final Optional<ResourceLocation> name;
    private final Optional<ResourceLocation> identifier;
    private final List<GachaRanking> ranking;
    
    public static final Codec<RequestUma> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("name")
                    .forGetter(RequestUma::getName),
            ResourceLocation.CODEC.optionalFieldOf("identifier")
                    .forGetter(RequestUma::getIdentifier),
            GachaRanking.CODEC.listOf().optionalFieldOf("ranking", Lists.newArrayList())
                    .forGetter(RequestUma::getRanking))
            
            .apply(instance, RequestUma::new));
    
    public RequestUma() {
		this.name = Optional.empty();
		this.identifier = Optional.empty();
		this.ranking = Lists.newArrayList();
	}
    
    public RequestUma(Optional<ResourceLocation> name, Optional<ResourceLocation> identifier, List<GachaRanking> ranking) {
		this.name = name;
		this.identifier = identifier;
		this.ranking = ranking;
	}

	public Optional<ResourceLocation> getName() {
		return name;
	}

	public Optional<ResourceLocation> getIdentifier() {
		return identifier;
	}

	public List<GachaRanking> getRanking() {
		return ranking;
	}
    
	public static RequestUma fromJSON(JsonObject json) {
        return CODEC.parse(JsonOps.INSTANCE, json).resultOrPartial(msg -> {
        	Umapyoi.getLogger().error("Failed to parse : {}", msg);
        }).orElseGet(RequestUma::new);
    }

    public JsonElement toJson() {
        return CODEC.encodeStart(JsonOps.INSTANCE, this).resultOrPartial(msg -> {
            Umapyoi.getLogger().error("Failed to encode : {}", msg);
        }).orElseThrow();
    }

    public void toNetwork(FriendlyByteBuf buffer) {
    	String utf = this.toJson().toString();
    	buffer.writeUtf(utf);
    }

    public static RequestUma fromNetwork(FriendlyByteBuf buffer) {
        String utf = buffer.readUtf();
		return fromJSON(JsonParser.parseString(utf).getAsJsonObject());
    }
    
    public boolean test(ItemStack soul) {
    	var name = this.name.isPresent() ? UmaSoulUtils.getName(soul).equals(this.name.get()): true;
		var id = this.identifier.isPresent() ? 
				ResourceLocation.tryParse(soul.getOrCreateTag().getString("identifier")) 
    			.equals(this.identifier.get()): true;
    	var ranking = !this.ranking.isEmpty() ? this.ranking.contains(GachaRanking.getGachaRanking(soul)) : true;
    	return name && id && ranking;
    }

	public void initItemStack(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		this.name.ifPresent(loc->tag.putString("name", loc.toString()));
		this.identifier.ifPresent(loc->tag.putString("identifier", loc.toString()));
	}
}
