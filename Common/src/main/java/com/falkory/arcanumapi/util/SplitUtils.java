package com.falkory.arcanumapi.util;

import com.falkory.arcanumapi.client.ClientUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * For avoiding calling methods that would be unsafe
 * to call on the wrong side with a dedicated server.
 * This is dumb, todo look it up properly
 *
 */
public class SplitUtils {
    public static void openBookSafe(Player player, ResourceLocation book, ItemStack item){
        if (!player.isLocalPlayer()) {return;} //Screens aren't present in the server
        ClientUtils.openBookUI(player, book, null, item);
    }
}
