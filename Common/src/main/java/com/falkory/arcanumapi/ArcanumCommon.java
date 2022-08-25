package com.falkory.arcanumapi;

import com.falkory.arcanumapi.api.ArcanumAPI;
import com.falkory.arcanumapi.book.BookLoader;
import com.falkory.arcanumapi.book.BookPage;
import com.falkory.arcanumapi.book.Requirement;
import com.falkory.arcanumapi.book.layers.TabLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/** Our shared main class.
 *  this mostly contains methods delegated from loader-specific classes
 * @see ArcanumForge forge initialization
 * @see ArcanumFabric fabric initialization
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
        TabLayer.init();
        Requirement.init(); // nya
    }

    // This method serves as a hook to modify item tooltips. The vanilla game
    // has no mechanism to load tooltip listeners so this must be registered
    // by a mod loader like Forge or Fabric.
    public static void onItemTooltip(ItemStack stack, TooltipFlag context, List<Component> tooltip) {

        if (!stack.isEmpty()) {

            final FoodProperties food = stack.getItem().getFoodProperties();

            if (food != null) {

                tooltip.add(Component.literal("Nutrition: " + food.getNutrition()));
                tooltip.add(Component.literal("Saturation: " + food.getSaturationModifier()));
            }
        }
    }

    public static BookLoader startBookLoader() {
        return ArcanumAPI.bookLoader = new BookLoader();
    }
}