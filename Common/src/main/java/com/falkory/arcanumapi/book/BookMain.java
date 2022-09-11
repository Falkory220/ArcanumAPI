package com.falkory.arcanumapi.book;

import com.falkory.arcanumapi.api.ArcanumAPI;
import com.falkory.arcanumapi.util.IDisableable;
import com.falkory.arcanumapi.util.Identifiable;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.falkory.arcanumapi.book.BookTab.makeUnfound;

public class BookMain implements IDisableable, Identifiable {

    protected Map<ResourceLocation, BookTab> tabs;
    private ResourceLocation key;
    private ResourceLocation tabKey;

    @ParametersAreNonnullByDefault
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

    public BookTab getCurrentTab() {
        return tabs.getOrDefault(tabKey, makeUnfound(this));
    }

    public void setTabKey(ResourceLocation newKey) {
        if (!tabs.containsKey(newKey)){
            ArcanumAPI.LOG.error("Tried to set "+this.key+"'s current tab to invalid key "+newKey);
            return;
        }
        tabKey = newKey;
    }

    public ResourceLocation getTabKey() {
        return tabKey;
    }
    public void incrementTab(int increment){
        var keys = new ArrayList<>(tabs.keySet());
        int curIndex = keys.indexOf(tabKey);
        int newIndex = ((curIndex + increment) % keys.size());
        setTabKey(keys.get(newIndex >= 0 ? newIndex : newIndex + keys.size()));
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
    public ResourceLocation key() {
        return key;
    }
}
