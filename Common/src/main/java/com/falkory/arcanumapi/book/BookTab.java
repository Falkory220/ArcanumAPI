package com.falkory.arcanumapi.book;

import com.falkory.arcanumapi.book.layers.TabLayer;
import com.falkory.arcanumapi.util.Identifiable;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class BookTab implements Identifiable {

    protected Map<ResourceLocation, BookNode> nodes;
    private BookMain in; //should this really be needed ?
    private String name; //TODO should be a translatable, yeah?
    private ResourceLocation key;

    private ResourceLocation icon, bg, requirement;
    private List<TabLayer> layers = new ArrayList<>();


    protected int serializationIndex = 0; //todo dunno what this is but it sound like jank

    public BookTab(Map<ResourceLocation, BookNode> nodes, ResourceLocation key, ResourceLocation icon, ResourceLocation bg, ResourceLocation requirement, String name, BookMain in){
        this.nodes = nodes;
        this.key = key;
        this.requirement = requirement;
        this.in = in;
        this.icon = icon;
        this.name = name;
        this.bg = bg;
    }

    public ResourceLocation key(){
        return key;
    }

    public BookNode entry(BookNode entry){
        return nodes.get(entry.key());
    }

    public List<BookNode> entries(){
        return new ArrayList<>(nodes.values());
    }

    public Stream<BookNode> streamEntries(){
        return nodes.values().stream();
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

    public ResourceLocation bg(){
        return bg;
    }

    public List<TabLayer> getBgs(){
        return layers;
    }

    int serializationIndex(){
        return serializationIndex;
    }

    public ResourceLocation requirement(){
        return requirement;
    }

    public BookNode getNode(ResourceLocation key){
        return nodes.get(key);
    }

    public Map<ResourceLocation, BookNode> getNodes() {
        return nodes;
    }
}
