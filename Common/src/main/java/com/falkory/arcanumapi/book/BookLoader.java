package com.falkory.arcanumapi.book;

//modified from net.arcanamod.systems.research.ResearchLoader

import com.falkory.arcanumapi.api.ArcanumAPI;
import com.falkory.arcanumapi.book.content.requirements.ItemRequirement;
import com.falkory.arcanumapi.book.content.requirements.ItemTagRequirement;
import com.falkory.arcanumapi.book.layers.BookLayer;
import com.falkory.arcanumapi.book.layers.NodeLayer;
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
    private static Map<ResourceLocation, JsonArray> layerQueue = new LinkedHashMap<>();
    private static Map<ResourceLocation, JsonArray> nodeQueue = new LinkedHashMap<>();

    //TODO custom namespacing method for un-namespaced resource locations in read locations, to reduce clutter in the case of hand-writing

    public BookLoader() {
        super(GSON, "arcanumbooks");
        LOG.info("Made a new BookLoader!");
    }

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
                Books.BOOKS.put(key, bookObject);
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

                // expecting key, in, icon
                ResourceLocation key = new ResourceLocation(tab.get("key").getAsString());
                ResourceLocation icon = new ResourceLocation(tab.get("icon").getAsString());
                icon = new ResourceLocation(icon.getNamespace(), "textures/" + icon.getPath());
                String name = tab.get("name").getAsString();
                ResourceLocation requirement = tab.has("requires") ? new ResourceLocation(tab.get("requires").getAsString()) : null;
                BookMain in = Books.BOOKS.get(new ResourceLocation(tab.get("in").getAsString()));
                BookTab tabObject = new BookTab(key, new LinkedHashMap<>(), icon, requirement, name, in);

                //TODO make adding layers to existing tabs ,, a thing?
                if(tab.has("layers")){
                    JsonArray layers = tab.getAsJsonArray("layers");
                    for(JsonElement layerElem : layers){
                        ResourceLocation loc = new ResourceLocation(layerElem.getAsString());
                        tabObject.layers.put(loc, null);
                    }
                }
                in.tabs.put(key, tabObject);
            }
        }
    }

    private static void applyLayersArray(ResourceLocation rl, JsonArray layers){
        for(JsonElement layerElement : layers){
            if(!layerElement.isJsonObject()){
                LOG.error("Non-object found in layers array in " + rl + "!");
                continue;
            }
            JsonObject layerObj = layerElement.getAsJsonObject();

            // expecting key, in, and type. optionals are speed and vanishZoom
            if(!parseIsSafe("layer", layerObj, rl, "key","in","type")){continue;}


            ResourceLocation type = new ResourceLocation(layerObj.getAsJsonPrimitive("type").getAsString());
            BookLayer layer = BookLayer.makeLayer(
              new ResourceLocation(layerObj.getAsJsonPrimitive("key").getAsString()),
              type,
              layerObj.has("priority") ? layerObj.getAsJsonPrimitive("priority").getAsFloat() : 0,
              layerObj,
              rl,
              layerObj.has("speed") ? layerObj.getAsJsonPrimitive("speed").getAsFloat() : 1f,
              layerObj.has("vanishZoom") ? layerObj.getAsJsonPrimitive("vanishZoom").getAsFloat() : -1);

            if(layer==null){
                if(BookLayer.getFactory(type) == null){LOG.error("Invalid Layer type \"" + type + "\" referenced in " + rl + "!");}
                else{LOG.error("Invalid Layer content for type \"" + type + "\" used in file " + rl + "!");}
                continue;
            }
            JsonElement inObj = layerObj.get("in");
            LinkedList<ResourceLocation> in = new LinkedList<>();
            if(inObj.isJsonArray()) for(JsonElement i :layerObj.getAsJsonArray("in")) in.add(new ResourceLocation(i.getAsString()));
            else in.add(new ResourceLocation(inObj.getAsString()));

            for(ResourceLocation t : in) Books.getTab(t).layers.put(layer.key(), layer);

        }
    }


    //TODO: call for NodeLayers instead of all tabs.
    private static void applyNodesArray(ResourceLocation rl, JsonArray nodes){
        for(JsonElement nodeElement : nodes){
            if(!nodeElement.isJsonObject()){
                LOG.error("Non-object found in entries array in " + rl + "!");
                continue;
            }

            JsonObject node = nodeElement.getAsJsonObject();

            // expecting key, name, desc, icons, layer, x, y, sections
            if(!parseIsSafe("node", node, rl, "key","name","desc","icons","layer","x","y","sections")){continue;}

            ResourceLocation key = new ResourceLocation(node.get("key").getAsString());
            String name = node.get("name").getAsString();
            String desc = node.has("desc") ? node.get("desc").getAsString() : "";
            List<Icon> icons = idsToIcons(node.getAsJsonArray("icons"), rl);
            ResourceLocation layerid = new ResourceLocation(node.get("layer").getAsString());

            BookLayer layer = Books.getLayer(layerid);
            if(!(layer instanceof NodeLayer)){ //everything after is pointless if our node won't be put in a node layer
                if(layer == null){LOG.error("Node "+name+" is bound to unreachable or nonexistent layer "+layerid+" in file "+rl+"!");}
                else{LOG.error("Node "+name+" is bound to non-node layer "+layer+" in file "+rl+"!");}
                continue;
            }

            int x = node.get("x").getAsInt();
            int y = node.get("y").getAsInt();
            List<BookPage> pages = jsonToSections(node.getAsJsonArray("sections"), rl);

            // optionally parents, meta
            List<NodeParent> parents = new ArrayList<>();
            if(node.has("parents"))
                parents = StreamSupport.stream(node.getAsJsonArray("parents").spliterator(), false).map(JsonElement::getAsString).map(NodeParent::parse).collect(Collectors.toList());

            List<String> meta = new ArrayList<>();
            if(node.has("meta"))
                meta = StreamSupport.stream(node.getAsJsonArray("meta").spliterator(), false).map(JsonElement::getAsString).collect(Collectors.toList());

            BookNode nodeObject = new BookNode(key, pages, icons, meta, parents, layer, name, desc, x, y);
            ((NodeLayer) layer).getNodes().put(key, nodeObject);
            pages.forEach(page -> page.node = nodeObject.key());
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
        if(json.has("layers")){
            JsonArray layers = json.getAsJsonArray("layers");
            layerQueue.put(rl, layers);
        }
        if(json.has("nodes")){
            JsonArray nodes = json.getAsJsonArray("nodes");
            nodeQueue.put(rl, nodes);
        }
    }

    private static List<Icon> idsToIcons(JsonArray itemIds, ResourceLocation rl){
        List<Icon> ret = new ArrayList<>();
        for(JsonElement element : itemIds){
            ret.add(Icon.fromString(element.getAsString()));
        }
        if(ret.isEmpty())
            LOG.error("A node has 0 icons in " + rl + "!");
        return ret;
    }

    private static List<BookPage> jsonToSections(JsonArray sections, ResourceLocation file){
        List<BookPage> ret = new ArrayList<>();
        for(JsonElement sectionElement : sections) {
            if (!sectionElement.isJsonObject()) {
                LOG.error("Non-object found in sections array in " + file + "!"); continue;}
            // expecting type, content
            JsonObject page = sectionElement.getAsJsonObject();
            ResourceLocation type = new ResourceLocation(page.get("type").getAsString());
            String content = page.get("content").getAsString();
            BookPage es = BookPage.makeSection(type, content);
            if (es != null) {
                if (page.has("requirements"))
                    if (page.get("requirements").isJsonArray()) {
                        for (Requirement requirement : jsonToRequirements(page.get("requirements").getAsJsonArray(), file))
                            if (requirement != null)
                                es.addRequirement(requirement);
                    } else
                        LOG.error("Non-array named \"requirements\" found in " + file + "!");
                es.addOwnRequirements();
                ret.add(es);
            } else if (BookPage.getFactory(type) == null)
                LOG.error("Invalid Section type \"" + type + "\" referenced in " + file + "!");
            else
                LOG.error("Invalid Section content \"" + content + "\" for type \"" + type + "\" used in file " + file + "!");
        }
        return ret;
    }

    private static List<Requirement> jsonToRequirements(JsonArray requirements, ResourceLocation file){
        List<Requirement> ret = new ArrayList<>();
        for(JsonElement requirementElement : requirements){
            if(!requirementElement.isJsonPrimitive()){continue;}

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
        return ret;
    }

    private static boolean parseIsSafe(String type, JsonObject json, ResourceLocation file, String... required){
        boolean safeParse = true;
        for(int i = 0; i<required.length; i++) {
            String str = required[i];
            if (!json.has(str)) {
                LOG.error("Required key \"" + str + "\" missing in object type \"" + type + "\" in file " + file + "!");
                safeParse = false;
            }
        }
        return safeParse;
    }

    @ParametersAreNonnullByDefault
    @Override protected void apply(Map<ResourceLocation, JsonElement> jobject, ResourceManager manager, ProfilerFiller prof) {
        LOG.info("BookLoader running !");

        bookQueue.clear();
        tabQueue.clear();
        layerQueue.clear();
        nodeQueue.clear();

        jobject.forEach((location, object1) -> {
            if(object1.isJsonObject())
                applyJson(object1.getAsJsonObject(), location);
        });

        bookQueue.forEach(BookLoader::applyBooksArray);
        tabQueue.forEach(BookLoader::applyTabsArray);
        layerQueue.forEach(BookLoader::applyLayersArray);
        nodeQueue.forEach(BookLoader::applyNodesArray);

        Books.initBooks();
    }
}
