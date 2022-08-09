package com.falkory.arcanumapi.book;

import com.falkory.arcanumapi.api.ArcanumAPI;
import com.falkory.arcanumapi.book.content.requirements.ItemRequirement;
import com.falkory.arcanumapi.book.content.requirements.ItemTagRequirement;
import com.falkory.arcanumapi.book.layers.TabLayer;
import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import net.minecraft.world.item.Item;
import org.slf4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

public class BookLoader extends SimpleJsonResourceReloadListener {

    private static final Logger LOG = ArcanumAPI.LOG;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static Map<ResourceLocation, JsonArray> bookQueue = new LinkedHashMap<>();
    private static Map<ResourceLocation, JsonArray> tabQueue = new LinkedHashMap<>();
    private static Map<ResourceLocation, JsonArray> nodeQueue = new LinkedHashMap<>();


    public BookLoader() {super(GSON, "arcanumbooks"); LOG.info("Made a new BookLoader!");}

    //notes: removed prefixes from source
    private static void applyBooksArray(ResourceLocation rl, JsonArray books){
        for(JsonElement bookElement : books){
            if(!bookElement.isJsonObject())
                LOG.error("Non-object found in books array in " + rl + "!");
            else{
                JsonObject book = bookElement.getAsJsonObject();
                // expecting key
                ResourceLocation key = new ResourceLocation(book.get("key").getAsString());
                BookMain bookObject = new BookMain(key, new LinkedHashMap<>());
                Books.BOOKS.putIfAbsent(key, bookObject);
                LOG.info("Loaded book " + key);
            }
        }
    }

    private static void applyTabsArray(ResourceLocation rl, JsonArray tabs){
        for(JsonElement categoryElement : tabs){
            if(!categoryElement.isJsonObject())
                LOG.error("Non-object found in categories array in " + rl + "!");
            else{
                JsonObject tab = categoryElement.getAsJsonObject();
                // expecting key, in, icon, bg, optionally bgs
                ResourceLocation key = new ResourceLocation(tab.get("key").getAsString());
                ResourceLocation bg = new ResourceLocation(tab.get("bg").getAsString());
                bg = new ResourceLocation(bg.getNamespace(), "textures/" + bg.getPath());
                ResourceLocation icon = new ResourceLocation(tab.get("icon").getAsString());
                icon = new ResourceLocation(icon.getNamespace(), "textures/" + icon.getPath());
                String name = tab.get("name").getAsString();
                ResourceLocation requirement = tab.has("requires") ? new ResourceLocation(tab.get("requires").getAsString()) : null;
                BookMain in = Books.BOOKS.get(new ResourceLocation(tab.get("in").getAsString()));
                BookTab tabObject = new BookTab(new LinkedHashMap<>(), key, icon, bg, requirement, name, in);
                if(tab.has("bgs")){
                    JsonArray layers = tab.getAsJsonArray("bgs");
                    for(JsonElement layerElem : layers){
                        JsonObject layerObj = layerElem.getAsJsonObject();
                        TabLayer layer = TabLayer.makeLayer(
                          new ResourceLocation(layerObj.getAsJsonPrimitive("type").getAsString()),
                          layerObj,
                          rl,
                          layerObj.getAsJsonPrimitive("speed").getAsFloat(),
                          layerObj.has("vanishZoom") ? layerObj.getAsJsonPrimitive("vanishZoom").getAsFloat() : -1);
                        if(layer != null)
                            tabObject.getBgs().add(layer);
                    }
                }
                in.tabs.putIfAbsent(key, tabObject);
            }
        }
    }

    private static void applyNodesArray(ResourceLocation rl, JsonArray nodes){
        for(JsonElement entryElement : nodes){
            if(!entryElement.isJsonObject())
                LOG.error("Non-object found in entries array in " + rl + "!");
            else{
                JsonObject entry = entryElement.getAsJsonObject();

                // expecting key, name, desc, icons, category, x, y, sections
                ResourceLocation key = new ResourceLocation(entry.get("key").getAsString());
                String name = entry.get("name").getAsString();
                String desc = entry.has("desc") ? entry.get("desc").getAsString() : "";
                List<Icon> icons = idsToIcons(entry.getAsJsonArray("icons"), rl);
                BookTab category = Books.getTab(new ResourceLocation(entry.get("category").getAsString()));
                int x = entry.get("x").getAsInt();
                int y = entry.get("y").getAsInt();
                List<BookPage> pages = jsonToPages(entry.getAsJsonArray("sections"), rl);

                // optionally parents, meta
                List<NodeParent> parents = new ArrayList<>();
                if(entry.has("parents"))
                    parents = StreamSupport.stream(entry.getAsJsonArray("parents").spliterator(), false).map(JsonElement::getAsString).map(NodeParent::parse).collect(Collectors.toList());

                List<String> meta = new ArrayList<>();
                if(entry.has("meta"))
                    meta = StreamSupport.stream(entry.getAsJsonArray("meta").spliterator(), false).map(JsonElement::getAsString).collect(Collectors.toList());

                BookNode entryObject = new BookNode(key, pages, icons, meta, parents, category, name, desc, x, y);
                category.nodes.putIfAbsent(key, entryObject);
                pages.forEach(page -> page.node = entryObject.key());
            }
        }
    }

    public static void applyJson(JsonObject json, ResourceLocation rl){
        if(json.has("books")){
            JsonArray books = json.getAsJsonArray("books");
            bookQueue.put(rl, books);
        }
        if(json.has("categories")){
            JsonArray categories = json.getAsJsonArray("categories");
            tabQueue.put(rl, categories);
        }
        if(json.has("entries")){
            JsonArray entries = json.getAsJsonArray("entries");
            nodeQueue.put(rl, entries);
        }
    }

    private static List<Icon> idsToIcons(JsonArray itemIds, ResourceLocation rl){
        List<Icon> ret = new ArrayList<>();
        for(JsonElement element : itemIds){
            ret.add(Icon.fromString(element.getAsString()));
        }
        if(ret.isEmpty())
            LOG.error("An entry has 0 icons in " + rl + "!");
        return ret;
    }

    private static List<BookPage> jsonToPages(JsonArray pages, ResourceLocation file){
        List<BookPage> ret = new ArrayList<>();
        for(JsonElement sectionElement : pages)
            if(sectionElement.isJsonObject()){
                // expecting type, content
                JsonObject page = sectionElement.getAsJsonObject();
                ResourceLocation type = new ResourceLocation(page.get("type").getAsString());
                String content = page.get("content").getAsString();
                BookPage es = BookPage.makeSection(type, content);
                if(es != null){
                    if(page.has("requirements"))
                        if(page.get("requirements").isJsonArray()){
                            for(Requirement requirement : jsonToRequirements(page.get("requirements").getAsJsonArray(), file))
                                if(requirement != null)
                                    es.addRequirement(requirement);
                        }else
                            LOG.error("Non-array named \"requirements\" found in " + file + "!");
                    es.addOwnRequirements();
                    ret.add(es);
                }else if(BookPage.getFactory(type) == null)
                    LOG.error("Invalid EntrySection type \"" + type + "\" referenced in " + file + "!");
                else
                    LOG.error("Invalid EntrySection content \"" + content + "\" for type \"" + type + "\" used in file " + file + "!");
            }else
                LOG.error("Non-object found in sections array in " + file + "!");
        return ret;
    }

    private static List<Requirement> jsonToRequirements(JsonArray requirements, ResourceLocation file){
        List<Requirement> ret = new ArrayList<>();
        for(JsonElement requirementElement : requirements){
            if(requirementElement.isJsonPrimitive()){
                String desc = requirementElement.getAsString();
                int amount = 1;
                // if it has * in it, then its amount is not one
                if(desc.contains("*")){
                    String[] parts = desc.split("\\*");
                    if(parts.length != 2)
                        LOG.error("Multiple \"*\"s found in requirement in " + file + "!");
                    desc = parts[parts.length - 1];
                    amount = Integer.parseInt(parts[0]);
                }
                List<String> params = new ArrayList<>();
                // document this better.
                // If this has a "{" it has parameters; remove those
                if(desc.contains("{") && desc.endsWith("}")){
                    String[] param_parts = desc.split("\\{", 2);
                    desc = param_parts[0];
                    params = Arrays.asList(param_parts[1].substring(0, param_parts[1].length() - 1).split(", "));
                }
                // If this has "::" it's a custom requirement
                if(desc.contains("::")){
                    String[] parts = desc.split("::");
                    if(parts.length != 2)
                        LOG.error("Multiple \"::\"s found in requirement in " + file + "!");
                    ResourceLocation type = new ResourceLocation(parts[0], parts[1]);
                    Requirement add = Requirement.makeRequirement(type, params);
                    if(add != null){
                        add.amount = amount;
                        ret.add(add);
                    }else
                        LOG.error("Invalid requirement type " + type + " found in file " + file + "!");
                    // if this begins with a hash
                }else if(desc.startsWith("#")){
                    // its a tag
                    ResourceLocation itemTagLoc = new ResourceLocation(desc.substring(1));
                    TagKey<Item> itemTag = TagKey.create(Registry.ITEM_REGISTRY, itemTagLoc);
                    // todo remove? - macy
                    if(itemTag != null){
                        ItemTagRequirement tagReq = new ItemTagRequirement(itemTag, itemTagLoc);
                        tagReq.amount = amount;
                        ret.add(tagReq);
                    }else
                        LOG.error("Invalid item tag " + itemTagLoc + " found in file " + file + "!");
                }else{
                    // its an item
                    ResourceLocation item = new ResourceLocation(desc);
                    Item value = Registry.ITEM.get(item);
                    if(value != null){
                        ItemRequirement add = new ItemRequirement(value);
                        add.amount = amount;
                        ret.add(add);
                    }else
                        LOG.error("Invalid item " + item + " found in file " + file + "!");
                }
            }
        }
        return ret;
    }

    @Override @ParametersAreNonnullByDefault
    protected void apply(Map<ResourceLocation, JsonElement> jobject, ResourceManager manager, ProfilerFiller prof) {
        LOG.info("BookLoader Running !");
        bookQueue.clear();
        tabQueue.clear();
        nodeQueue.clear();

        jobject.forEach((location, object1) -> {
            if(object1.isJsonObject())
                applyJson(object1.getAsJsonObject(), location);
        });

        bookQueue.forEach(BookLoader::applyBooksArray);
        tabQueue.forEach(BookLoader::applyTabsArray);
        nodeQueue.forEach(BookLoader::applyNodesArray);
    }
}
