package com.falkory.arcanumapi.book;

import com.falkory.arcanumapi.book.layers.BookLayer;
import com.falkory.arcanumapi.book.layers.ImageLayer;
import com.falkory.arcanumapi.book.layers.NodeLayer;
import com.falkory.arcanumapi.client.gui.BookMainScreen;
import com.falkory.arcanumapi.util.Identifiable;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.falkory.arcanumapi.ArcanumCommon.AmId;

public class BookTab implements Identifiable {
    
    private BookMain in;
    private String name; //TODO should be a translatable, yeah?
    private ResourceLocation key;

    private ResourceLocation icon, requirement;
    protected Map<ResourceLocation, BookLayer> layers;

    // this instance's position in the load order! (i get it now I'm sorry luna)
    protected int serializationIndex = 0;

    @ParametersAreNonnullByDefault
    public BookTab(ResourceLocation key, Map<ResourceLocation, BookLayer> layers, ResourceLocation icon, ResourceLocation requirement, String name, BookMain in){
        this.layers = layers;
        this.key = key;
        this.requirement = requirement;
        this.in = in;
        this.icon = icon;
        this.name = name;
    }

    public ResourceLocation key(){
        return key;
    }


    public Stream<BookNode> streamEntries(){
        return streamLayers()
          .filter(layer -> layer instanceof NodeLayer)
          .flatMap(nodelayer -> ((NodeLayer) nodelayer).streamNodes());
    }
    
    public Stream<BookLayer> streamLayers(){
        return layers.values().stream();
    }

    public BookMain book(){
        return in;
    }

    public ResourceLocation icon(){
        return icon;
    }

    public String name(){
        return name;
    }

    public List<BookLayer> getLayers(){
        return new ArrayList<>(layers.values());
    }

    public int serializationIndex(){
        return serializationIndex;
    }

    public ResourceLocation requirement(){
        return requirement;
    }

    public void render(PoseStack stack, BookMainScreen parent, float drawSize, float spd){
        layers.values().forEach(layer -> layer.render(stack, parent, drawSize, spd));
    }

    //this is just a funny ok? just a little humor
    public static BookTab makeUnfound(BookMain book) {
        ResourceLocation unfoundImage = AmId("textures/gui/book/tab_not_found.png");
        HashMap<ResourceLocation, BookLayer> layermap = new HashMap<>();
        layermap.put(new ResourceLocation("unfound_layer"), new ImageLayer(unfoundImage.toString()));
        return new BookTab(AmId("tab_not_found"), layermap, unfoundImage, null, "Tab not found", book);
    }
}
