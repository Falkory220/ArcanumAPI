package com.falkory.arcanumapi;

import com.falkory.arcanumapi.api.ArcanumAPI;
import com.falkory.arcanumapi.item.BookItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.function.BiConsumer;

/** ArcanumAPI's main class for the forge mod loader. <br>
 * This class mostly contains methods to safely delegate to {@link ArcanumCommon}
 * @see ArcanumCommon
 * @see ArcanumFabric ArcanumFabric (Fabric analogue of this class)
 * */
//"hey! did you just add this here to quick link between the three in dev" heck yeah I did
@Mod(ArcanumAPI.MOD_ID)
public class ArcanumForge {
    
    public ArcanumForge() {
    
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        ArcanumAPI.LOG.info("Hello Forge world!");
        ArcanumCommon.init();
    
        // Some code like events require special initialization from the
        // loader specific code.
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegistry);
        MinecraftForge.EVENT_BUS.addListener(this::addReloadListeners);
    }

    @SubscribeEvent
    public void onRegistry(RegisterEvent event){
        event.register(ForgeRegistries.Keys.ITEMS, nya -> {
            BookItems.releaseBooks(registrar(nya));
        });
    }
    @SubscribeEvent
    public void addReloadListeners(AddReloadListenerEvent event){
        event.addListener(ArcanumCommon.startBookLoader());
    }


    private static <T> BiConsumer<T, ResourceLocation> registrar(RegisterEvent.RegisterHelper<T> hinderer){
        return (t, id) -> hinderer.register(id, t);
    }

}