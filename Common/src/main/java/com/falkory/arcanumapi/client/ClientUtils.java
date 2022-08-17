package com.falkory.arcanumapi.client;

import com.falkory.arcanumapi.book.Books;
import com.falkory.arcanumapi.client.gui.BookTabScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ClientUtils {

    /**
     * unsafe to call from server.
     * use {@link com.falkory.arcanumapi.util.SplitUtils#openBookSafe(Player, ResourceLocation, ItemStack)} instead, pretty please
     *  */
    public static void openBookUI(Player player, ResourceLocation book, Screen parentScreen, ItemStack item){
        if(!Books.DISABLED.contains(book)){
            Minecraft.getInstance().setScreen(new BookTabScreen(Books.BOOKS.get(book), parentScreen, item));
        }
    }
}
