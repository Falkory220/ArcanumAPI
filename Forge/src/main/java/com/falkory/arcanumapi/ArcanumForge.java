package com.falkory.arcanumapi;

import com.falkory.arcanumapi.item.BookItems;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

import java.util.function.BiConsumer;

@Mod(Constants.MOD_ID)
public class ArcanumForge {
    
    public ArcanumForge() {
    
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        Constants.LOG.info("Hello Forge world!");
        ArcanumCommon.init();
    
        // Some code like events require special initialization from the
        // loader specific code.
        MinecraftForge.EVENT_BUS.addListener(this::onItemTooltip);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegistry);
    }
    
    // This method exists as a wrapper for the code in the Common project.
    // It takes Forge's event object and passes the parameters along to
    // the Common listener.
    private void onItemTooltip(ItemTooltipEvent event) {

        ArcanumCommon.onItemTooltip(event.getItemStack(), event.getFlags(), event.getToolTip());
    }

    @SubscribeEvent
    public void onRegistry(RegisterEvent event){
        event.register(ForgeRegistries.Keys.ITEMS, nya -> {
            BookItems.releaseBooks(registrar(nya));
        });
    }

    private static <T> BiConsumer<T, ResourceLocation> registrar(RegisterEvent.RegisterHelper<T> hinderer){
        return (t, id) -> hinderer.register(id, t);
    }

}