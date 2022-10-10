package com.falkory.arcanumapi.book;

//modified from net.arcanamod.systems.research.ResearchCategory

import com.falkory.arcanumapi.book.layers.BookLayer;
import com.falkory.arcanumapi.book.layers.ImageLayer;
import com.falkory.arcanumapi.book.layers.NodeLayer;
import com.falkory.arcanumapi.client.gui.widget.menu.LayerWindow;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.falkory.arcanumapi.ArcanumCommon.AmId;

public class BookTab implements IconsHaver{
    
    private BookMain in;
    private String name; //TODO should be a translatable, yeah?
    private ResourceLocation key;

    private List<Icon> icons;
    private ResourceLocation requirement;
    protected Map<ResourceLocation, BookLayer> layers;

    // this instance's position in the load order! (i get it now I'm sorry luna)
    protected int serializationIndex = 0;

    @ParametersAreNonnullByDefault
    public BookTab(ResourceLocation key, Map<ResourceLocation, BookLayer> layers, List<Icon> icons, ResourceLocation requirement, String name, BookMain in){
        this.layers = layers;
        this.key = key;
        this.requirement = requirement;
        this.in = in;
        this.icons = icons;
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

    public void render(PoseStack stack, LayerWindow parent, float drawSize, float spd){
        layers.values().forEach(layer -> layer.render(stack, parent, drawSize, spd));
    }

    //todo missing deserialize

    public static BookTab unfoundTab = makeUnfound();

    //this is just a funny ok? just a little humor
    private static BookTab makeUnfound() {
        ResourceLocation unfoundImage = AmId("textures/gui/book/tab_not_found.png");
        HashMap<ResourceLocation, BookLayer> layermap = new HashMap<>();
        layermap.put(new ResourceLocation("unfound_layer"), new ImageLayer(unfoundImage.toString()));
        return new BookTab(AmId("tab_not_found"), layermap, new ArrayList<>(), null, "Tab not found", Books.BOOKS.get(AmId("arcanum")));
    }

    @Override public List<Icon> getIcons() {
        return icons;
    }
}
