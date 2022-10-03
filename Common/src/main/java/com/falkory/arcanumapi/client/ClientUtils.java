package com.falkory.arcanumapi.client;

import com.falkory.arcanumapi.api.ArcanumAPI;
import com.falkory.arcanumapi.book.Books;
import com.falkory.arcanumapi.client.gui.BookMainScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ClientUtils {

    /**
     * unsafe to call from server. <p>
     * use {@link com.falkory.arcanumapi.util.SplitUtils#openBookSafe(Player, ResourceLocation, ItemStack)} instead, pretty please
     *  */
    public static void openBookUI(Player player, ResourceLocation book, Screen parentScreen, ItemStack item){
        if(Books.DISABLED.contains(book)){
            ArcanumAPI.LOG.info("Player "+player.getName().getString()+" tried to open disabled book "+book);
            return;
        }
        if(!Books.BOOKS.containsKey(book)){
            ArcanumAPI.LOG.error("Player "+player.getName().getString()+" tried to open non-existent book "+book);
            return;
        }
        Minecraft.getInstance().setScreen(new BookMainScreen(Books.BOOKS.get(book), parentScreen, item));
    }
}
