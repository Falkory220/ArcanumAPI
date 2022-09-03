package com.falkory.arcanumapi;

import com.falkory.arcanumapi.api.ArcanumAPI;
import com.falkory.arcanumapi.book.BookLoader;
import com.falkory.arcanumapi.book.BookPage;
import com.falkory.arcanumapi.book.Requirement;
import com.falkory.arcanumapi.book.layers.BookLayer;
import net.minecraft.resources.ResourceLocation;

/** Our shared main class. <br>
 * This class mostly contains methods delegated from loader-specific classes
 * @see ArcanumForge ArcanumForge (Forge delegations)
 * @see ArcanumFabric ArcanumFabric (Fabric delegations)
 * */
public class ArcanumCommon {

    public static ResourceLocation AmId(String id){
        return new ResourceLocation(ArcanumAPI.MOD_ID, id);
    }
    // This method serves as an initialization hook for the mod. The vanilla
    // game has no mechanism to load tooltip listeners so this must be
    // invoked from a mod loader specific project like Forge or Fabric.
    public static void init() {
        // adding default book contents to the relevant factory lists
        BookPage.init();
        BookLayer.init();
        Requirement.init(); // nya
    }

    public static BookLoader startBookLoader() {
        return ArcanumAPI.bookLoader = new BookLoader();
    }
}