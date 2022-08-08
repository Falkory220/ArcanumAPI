package com.falkory.arcanumapi.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class BookItems {
    public static final Item.Properties bookProperties;
    public static final Map<ResourceLocation, Item> REGISTRY_BOOKSHELF = new HashMap<>();

    public static final Item ARCANUM_ITEM;

    public static void aLittleIdentity(){} //(it never hurt nobody)

    /**Adds a provided Item to the arcanum api bookshelf, for later registry.*/
    public static Item stockBook(ResourceLocation name, Item item){
        REGISTRY_BOOKSHELF.put(name, item);
        return item;
    }

    /**Adds a provided BookItem to the arcanum api bookshelf*/

    //Our book register event, passed a consumer from the current loader-specific init class
    public static void releaseBooks(BiConsumer<Item, ResourceLocation> registrar){
        for (var book : REGISTRY_BOOKSHELF.entrySet()){
            registrar.accept(book.getValue(), book.getKey());
        }
    }

    static {
        bookProperties = new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC).rarity(Rarity.UNCOMMON);
        ARCANUM_ITEM = stockBook(new ResourceLocation("arcanumapi","arcanum"), new BookItem(bookProperties, new ResourceLocation("arcanumapi", "arcanum")));
    }
}
