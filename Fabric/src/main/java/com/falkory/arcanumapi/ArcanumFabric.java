package com.falkory.arcanumapi;

import com.falkory.arcanumapi.api.ArcanumAPI;
import com.falkory.arcanumapi.bookdata.BookLoadListener;
import com.falkory.arcanumapi.config.ModConfig;
import com.falkory.arcanumapi.item.BookItems;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.util.function.BiConsumer;

/** ArcanumAPI's main class for the fabric (and quilt!) mod loaders. <br>
 *  This class mostly contains methods to safely delegate to {@link ArcanumCommon}
 * @see ArcanumCommon
 * @see ArcanumForge ArcanumForge (Forge analogue of this class)
 * */
//"hey! did you just add this here to quick link between the three in dev" heck yeah I did
public class ArcanumFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);

        // Use Fabric to bootstrap the Common mod.
        ArcanumAPI.LOG.info("Hello Fabric world!");
        ArcanumCommon.init();
        registryStuff();
        addReloadListener();
        
        // Some code like events require special initialization from the loader specific code.
    }

    public static void addReloadListener(){
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new BookLoadListener());
    }

    private void registryStuff(){
        BookItems.releaseBooks(registrar(BuiltInRegistries.ITEM));
    }


    private static <T> BiConsumer<T, ResourceLocation> registrar(Registry<T> registry){
        return (t, id) -> Registry.register(registry, id, t);
    }
}
