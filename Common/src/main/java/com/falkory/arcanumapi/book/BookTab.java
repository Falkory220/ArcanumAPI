package com.falkory.arcanumapi.book;

import com.falkory.arcanumapi.book.layers.BookLayer;
import com.falkory.arcanumapi.book.layers.NodeLayer;
import com.falkory.arcanumapi.client.gui.BookMainScreen;
import com.falkory.arcanumapi.util.Identifiable;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class BookTab implements Identifiable {
    
    private BookMain in;
    private String name; //TODO should be a translatable, yeah?
    private ResourceLocation key;

    private ResourceLocation icon, requirement;
    private Map<Number, BookLayer> layers;


    protected int serializationIndex = 0; //todo dunno what this is but it sound like jank

    public BookTab(Map<Number, BookLayer> layers, ResourceLocation key, ResourceLocation icon, ResourceLocation requirement, String name, BookMain in){
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

    int serializationIndex(){
        return serializationIndex;
    }

    public ResourceLocation requirement(){
        return requirement;
    }

    public void render(PoseStack stack, BookMainScreen parent, float drawSize, float spd){
        layers.values().forEach(layer -> layer.render(stack, parent, drawSize, spd));
    }

}
