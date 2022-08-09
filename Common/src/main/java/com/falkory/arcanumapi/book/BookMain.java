package com.falkory.arcanumapi.book;

import com.falkory.arcanumapi.util.IDisableable;
import com.falkory.arcanumapi.util.Identifiable;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BookMain implements IDisableable, Identifiable, AbstractBindable<BookTab> {

    protected Map<ResourceLocation, BookTab> tabs;
    private ResourceLocation key;

    public BookMain(ResourceLocation key, Map<ResourceLocation, BookTab> tabs){
        this.tabs = tabs;
        this.key = key;
    }

    public BookTab getTab(ResourceLocation key){
        return tabs.get(key);
    }

    public List<BookTab> getTabs(){
        return new ArrayList<>(tabs.values());
    }

    public Stream<BookTab> streamCategories(){
        return tabs.values().stream();
    }

    public Stream<BookNode> streamNodes(){
        return streamCategories().flatMap(BookTab::streamEntries);
    }

    public List<BookNode> getNodes(){
        return streamNodes().collect(Collectors.toList());
    }

    public BookNode getNode(ResourceLocation key){
        return streamNodes().filter(entry -> entry.key().equals(key)).findFirst().orElse(null);
    }

    public Map<ResourceLocation, BookTab> getCategoriesMap(){
        return Collections.unmodifiableMap(tabs);
    }

    @Override
    public void setHeld(Map<ResourceLocation, BookTab> map) {}

    @Override
    public ResourceLocation key() {
        return key;
    }
}
