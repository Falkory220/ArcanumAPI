package com.falkory.arcanumapi.book.content;

import com.falkory.arcanumapi.book.content.AbstractCraftingSection;
import net.minecraft.resources.ResourceLocation;

import static com.falkory.arcanumapi.ArcanumCommon.AmId;

public class CraftingSection extends AbstractCraftingSection {

    public static final ResourceLocation TYPE = AmId("crafting");

    public CraftingSection(ResourceLocation recipe){
        super(recipe);
    }

    public CraftingSection(String s){
        super(s);
    }

    public ResourceLocation getType(){
        return TYPE;
    }
}