package com.falkory.arcanumapi.book.content;

//modified from net.arcanamod.systems.research.Pin

import com.falkory.arcanumapi.book.BookNode;
import com.falkory.arcanumapi.book.BookPage;
import com.falkory.arcanumapi.book.Icon;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * A quick reference to a specific page in a research book, which may point to an item's recipe.
 */
public class Pin{

    @Nullable
    Item result;
    BookNode node;
    int stage;
    Icon icon;

    public Pin(@Nullable Item result, BookNode node, int stage, Icon icon){
        this.result = result;
        this.node = node;
        this.stage = stage;
        this.icon = icon;
    }

    // Grabs the icon and item from the entry.
    // If a recipe isn't being pointed to, uses the icon of the entry its in.
    public Pin(BookNode entry, int stage, Level world){
        // Check if the section is a recipe.
        if(entry.pages().size() > stage){
            this.stage = stage;
            BookPage page = entry.pages().get(stage);
            Optional<? extends Recipe<?>> recipeOpt;
            if(page instanceof AbstractCraftingSection && (recipeOpt = world.getRecipeManager().byKey(((AbstractCraftingSection)page).recipe)).isPresent()){
                Recipe<?> recipe = recipeOpt.get();
                this.icon = new Icon(recipe.getResultItem(RegistryAccess.EMPTY));
                this.result = recipe.getResultItem(RegistryAccess.EMPTY).getItem();
            } else {
                this.icon = entry.icons().get(0);
            }
        }else{
            this.stage = 0;
        }

        this.node = entry;
    }

    public BookNode getNode(){
        return node;
    }

    public int getStage(){
        return stage;
    }

    @Nullable
    public Item getResult(){
        return result;
    }

    public Icon getIcon(){
        return icon;
    }
}
