package com.falkory.arcanumapi.book.content;

//modified from net.arcanamod.systems.research.impls.AbstractCraftingSection

import com.falkory.arcanumapi.book.BookNode;
import com.falkory.arcanumapi.book.BookPage;
import com.falkory.arcanumapi.book.Icon;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractCraftingSection extends BookPage {

    ResourceLocation recipe;

    public AbstractCraftingSection(ResourceLocation recipe){
        this.recipe = recipe;
    }

    public AbstractCraftingSection(String s){
        this(new ResourceLocation(s));
    }

    public CompoundTag getData(){
        CompoundTag compound = new CompoundTag();
        compound.putString("recipe", recipe.toString());
        return compound;
    }

    public ResourceLocation getRecipe(){
        return recipe;
    }

    public Stream<Pin> getPins(int index, Level world, BookNode node){
        // if the recipe exists,
        Optional<? extends Recipe<?>> recipe = world.getRecipeManager().byKey(this.recipe);
        if(recipe.isPresent()){
            // get the item as the icon
            ItemStack output = recipe.get().getResultItem();
            Icon icon = new Icon(Registry.ITEM.getKey(output.getItem()), output);
            // and return a pin that points to this
            return Stream.of(new Pin(output.getItem(), node, index, icon));
        }
        return super.getPins(index, world, node);
    }
}
