package com.xlxyvergil.tcc.jei;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 进化路径类别：仅展示 from → to 的进化连线，不展示达成条件。
 */
public class EvolutionRecipeCategory implements IRecipeCategory<EvolutionRecipe> {
    public static final RecipeType<EvolutionRecipe> TYPE =
            RecipeType.create(TaczCurios.MODID, "evolution", EvolutionRecipe.class);

    public static final int WIDTH = 96;
    public static final int HEIGHT = 44;

    private final IDrawable arrow;
    private final IDrawable icon;

    public EvolutionRecipeCategory(IGuiHelper guiHelper) {
        this.arrow = guiHelper.getRecipeArrow();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(TccItems.SEVEN_THUNDERS));
    }

    @Override
    public RecipeType<EvolutionRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.tcc.category.evolution");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    @Nullable
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EvolutionRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(6, 13).addItemStack(recipe.from());
        builder.addOutputSlot(66, 13).addItemStack(recipe.to());
    }

    @Override
    public void draw(EvolutionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.arrow.draw(guiGraphics, 40, 13);
        String id = recipe.achievementId();
        Component title = id == null
                ? Component.empty()
                : Component.translatable("advancement." + id.replace(':', '.') + ".title");
        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.font.width(title);
        guiGraphics.drawString(minecraft.font, title, (WIDTH - width) / 2, HEIGHT - 10, 0xFFFFFF, false);
    }
}
