package com.falkory.arcanumapi.book;

import com.falkory.arcanumapi.book.content.Pin;
import com.falkory.arcanumapi.book.content.StringSection;
import com.falkory.arcanumapi.book.content.CraftingSection;
import com.falkory.arcanumapi.util.Identifiable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.falkory.arcanumapi.ArcanumCommon.AmId;
import static com.falkory.arcanumapi.util.StreamUtils.streamAndApply;

public abstract class BookPage implements Identifiable {

    // static stuff
    // when addon support is to be added: change this from strings to ResourceLocations so mods can register more
    private static Map<ResourceLocation, Function<String, BookPage>> factories = new LinkedHashMap<>();
    private static Map<ResourceLocation, Function<CompoundTag, BookPage>> deserializers = new LinkedHashMap<>();
    
    public static Function<String, BookPage> getFactory(ResourceLocation type){
        return factories.get(type);
    }

    public static BookPage makeSection(ResourceLocation type, String content){
        if(getFactory(type) != null)
            return getFactory(type).apply(content);
        else
            return null;
    }

    public static BookPage deserialize(CompoundTag passData){
        ResourceLocation type = AmId(passData.getString("type"));
        CompoundTag data = passData.getCompound("data");
        List<Requirement> requirements = streamAndApply(passData.getList("requirements", 10), CompoundTag.class, Requirement::deserialize).toList();
        if(deserializers.get(type) != null){
            BookPage page = deserializers.get(type).apply(data);
            requirements.forEach(page::addRequirement);
            // recieving on client
            page.node = new ResourceLocation(passData.getString("entry"));
            return page;
        }
        return null;
    }

    public static void init(){
        factories.put(StringSection.TYPE, StringSection::new);
        deserializers.put(StringSection.TYPE, nbt -> new StringSection(nbt.getString("text")));
        factories.put(CraftingSection.TYPE, CraftingSection::new);
        deserializers.put(CraftingSection.TYPE, nbt -> new CraftingSection(nbt.getString("recipe")));
        /*
         * NYI
         *
        factories.put(SmeltingSection.TYPE, SmeltingSection::new);
        deserializers.put(SmeltingSection.TYPE, nbt -> new SmeltingSection(nbt.getString("recipe")));
        factories.put(AlchemySection.TYPE, AlchemySection::new);
        deserializers.put(AlchemySection.TYPE, nbt -> new AlchemySection(nbt.getString("recipe")));
        factories.put(ArcaneCraftingSection.TYPE, ArcaneCraftingSection::new);
        deserializers.put(ArcaneCraftingSection.TYPE, nbt -> new ArcaneCraftingSection(nbt.getString("recipe")));
        factories.put(ImageSection.TYPE, ImageSection::new);
        deserializers.put(ImageSection.TYPE, nbt -> new ImageSection(nbt.getString("image")));
        factories.put(AspectCombosSection.TYPE, __ -> new AspectCombosSection());
        deserializers.put(AspectCombosSection.TYPE, __ -> new AspectCombosSection());
        */
    }

    // instance stuff

    protected List<Requirement> requirements = new ArrayList<>();
    protected ResourceLocation node;
    protected ResourceLocation key;

    public void addRequirement(Requirement requirement){
        requirements.add(requirement);
    }

    public List<Requirement> getRequirements(){
        return Collections.unmodifiableList(requirements);
    }

    public CompoundTag getPassData(){
        CompoundTag nbt = new CompoundTag();
        nbt.putString("type", getType().toString());
        nbt.put("data", getData());
        nbt.putString("entry", getNode().toString());

        ListTag list = new ListTag();
        getRequirements().forEach((requirement) -> list.add(requirement.getPassData()));
        nbt.put("requirements", list);

        return nbt;
    }

    public ResourceLocation getNode(){
        return node;
    }

    public abstract ResourceLocation getType();

    public abstract CompoundTag getData();

    public void addOwnRequirements(){
    }

    /**
     * Returns a stream containing this entry's pins.
     *
     * @param index
     * 		The index in the entry of this section.
     * @param world
     * 		The world the player is in.
     * @param entry
     * 		The entry this is in.
     * @return This entry's pins.
     */
    public Stream<Pin> getPins(int index, Level world, BookNode entry){
        return Stream.empty();
    }
    
    @Override
    public ResourceLocation key() {
        return key;
    }
}
