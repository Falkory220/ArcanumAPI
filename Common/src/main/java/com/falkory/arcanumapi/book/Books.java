package com.falkory.arcanumapi.book;

import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Books {

    public static ArrayList<ResourceLocation> DISABLED = new ArrayList<>();
    public static Map<ResourceLocation, BookMain> BOOKS = new LinkedHashMap<>(); //TODO make private with accessor?

    public static List<BookMain> getBooks(){
        return new ArrayList<>(BOOKS.values());
    }

    public static Stream<BookTab> streamTabs(){
        return BOOKS.values().stream().flatMap(BookMain::streamCategories);
    }

    public static List<BookTab> getTabs(){
        return streamTabs().collect(Collectors.toList());
    }

    public static BookTab getTab(ResourceLocation key){
        return streamTabs().filter(x -> x.key().equals(key)).findFirst().orElse(null);
    }

    public static Stream<BookNode> streamNodes(){
        return streamTabs().flatMap(BookTab::streamEntries);
    }

    public static List<BookNode> getNodes(){
        return streamNodes().collect(Collectors.toList());
    }

    public static BookNode getNode(ResourceLocation key){
        return streamNodes().filter(x -> x.key().equals(key)).findFirst().orElse(null);
    }

    public static Stream<BookNode> streamChildrenOf(BookNode parent){
        return streamNodes().filter(n -> n.parents().stream().anyMatch(it -> it.entry.equals(parent.key())));
    }

    public static List<BookNode> getChildrenOf(BookNode parent){
        return streamChildrenOf(parent).collect(Collectors.toList());
    }

    static void initBooks(){
        getBooks().forEach(BookMain::init);
    }


}
