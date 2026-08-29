package com.xlxyvergil.tcc.jei;

import com.xlxyvergil.tcc.TaczCurios;
import com.xlxyvergil.tcc.registries.TccItems;
import com.xlxyvergil.tcc.util.CollapseCrystalData;
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
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 崩坏结晶合成类别：崩坏结晶 + 同组 12 种素材 → 真我 / 黑渊白花。
 */
public class CollapseCrystalRecipeCategory implements IRecipeCategory<CollapseCrystalRecipe> {
    public static final RecipeType<CollapseCrystalRecipe> TYPE =
            RecipeType.create(TaczCurios.MODID, "collapse_crystal", CollapseCrystalRecipe.class);

    public static final int WIDTH = 196;
    public static final int HEIGHT = 64;

    private static final int COLS = 6;
    private static final int SLOT = 16;
    private static final int GAP = 2;
    private static final int STEP = SLOT + GAP;

    private final IDrawable arrow;
    private final IDrawable icon;

    public CollapseCrystalRecipeCategory(IGuiHelper guiHelper) {
        this.arrow = guiHelper.getRecipeArrow();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(TccItems.COLLAPSE_CRYSTAL));
    }

    @Override
    public RecipeType<CollapseCrystalRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.tcc.category.collapse");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CollapseCrystalRecipe recipe, IFocusGroup focuses) {
        // 崩坏结晶作为输入，垂直居中
        builder.addInputSlot(8, 22).addItemStack(recipe.crystal());

        // 素材以 6 列网格排布
        List<ItemStack> materials = recipe.materials();
        int startX = 36;
        int startY = 12;
        for (int i = 0; i < materials.size(); i++) {
            int col = i % COLS;
            int row = i / COLS;
            builder.addInputSlot(startX + col * STEP, startY + row * STEP).addItemStack(materials.get(i));
        }

        builder.addOutputSlot(172, 22).addItemStack(recipe.output());
    }

    @Override
    public void draw(CollapseCrystalRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.arrow.draw(guiGraphics, 146, 24);

        Minecraft minecraft = Minecraft.getInstance();
        int count = recipe.materials().size();
        Component note = Component.translatable("jei.tcc.collapse.complete", count, CollapseCrystalData.REQUIRED_COUNT);
        guiGraphics.drawString(minecraft.font, note, 36, 56, 0xFFFFFF, false);
        guiGraphics.drawString(minecraft.font, Component.translatable("jei.tcc.collapse.hint"), 172, 56, 0xAAAAAA, false);
    }
}
