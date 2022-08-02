package com.falkory.arcanumapi;

import com.falkory.arcanumapi.item.BookItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class ArcanumFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        Constants.LOG.info("Hello Fabric world!");
        ArcanumCommon.init();
        registryStuff();
        
        // Some code like events require special initialization from the
        // loader specific code.
        ItemTooltipCallback.EVENT.register(ArcanumCommon::onItemTooltip);
    }

    private void registryStuff(){
        BookItems.releaseBooks(registrar(Registry.ITEM));
    }

    private static <T> BiConsumer<T, ResourceLocation> registrar(Registry<T> registry){
        return (t, id) -> Registry.register(registry, id, t);
    }
}
