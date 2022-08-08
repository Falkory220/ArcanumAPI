package com.falkory.arcanumapi.book;

import com.falkory.arcanumapi.book.content.Pin;
import com.falkory.arcanumapi.util.Identifiable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.falkory.arcanumapi.util.StreamUtils.streamAndApply;

public class BookNode implements Identifiable {

    private ResourceLocation key;
    private List<BookPage> pages;
    private List<String> meta;
    private List<NodeParent> parents;
    private List<Icon> icons;
    private int x, y;

    private String name, desc; //do these really,, need to ?
    private BookTab tab;

    //a barebones one for making placeholders. should never be used in production in fact- todo remove
    public BookNode(ResourceLocation key){
        this.key = key;
    }

    //missing a whole bunch of bits because uhhh, they don't exist as types yet oops
    public BookNode(ResourceLocation key, List<BookPage> pages, List<Icon> icons, List<String> meta, List<NodeParent> parents, BookTab tab, String name, String desc, int x, int y){
        this.key = key;
        this.pages = pages;
        this.tab = tab;
        this.meta = meta;
        this.name = name;
        this.desc = desc;
        this.x = x;
        this.y = y;
    }

    @Override
    public ResourceLocation key() {
        return key;
    }

    public List<NodeParent> parents() {
        return parents;
    }

    public List<BookPage> pages(){
        return Collections.unmodifiableList(pages);
    }

    public List<Icon> icons(){
        return icons;
    }

    public List<String> meta(){
        return meta;
    }

    public BookTab tab(){
        return tab;
    }

    public String name(){
        return name;
    }

    public String description(){
        return desc;
    }

    public int x(){
        return x;
    }

    public int y(){
        return y;
    }

    public CompoundTag serialize(ResourceLocation tag){
        CompoundTag nbt = new CompoundTag();
        // key
        nbt.putString("id", tag.toString());
        // name, desc
        nbt.putString("name", name());
        nbt.putString("desc", description());
        // x, y
        nbt.putInt("x", x());
        nbt.putInt("y", y());
        // pages
        ListTag list = new ListTag();
        pages().forEach((section) -> list.add(section.getPassData()));
        nbt.put("pages", list);
        // icons
        ListTag icons = new ListTag();
        icons().forEach((icon) -> icons.add(StringTag.valueOf(icon.toString())));
        nbt.put("icons", icons);
        // parents
        ListTag parents = new ListTag();
        parents().forEach((parent) -> parents.add(StringTag.valueOf(parent.asString())));
        nbt.put("parents", parents);
        // meta
        ListTag meta = new ListTag();
        meta().forEach((met) -> meta.add(StringTag.valueOf(met)));
        nbt.put("meta", meta);
        return nbt;
    }

    public static BookNode deserialize(CompoundTag nbt, BookTab in){
        ResourceLocation key = new ResourceLocation(nbt.getString("id"));
        String name = nbt.getString("name");
        String desc = nbt.getString("desc");
        int x = nbt.getInt("x");
        int y = nbt.getInt("y");
        List<BookPage> pages = streamAndApply(nbt.getList("pages", 10), CompoundTag.class, BookPage::deserialize).collect(Collectors.toList());
        List<NodeParent> betterParents = streamAndApply(nbt.getList("parents", 8), StringTag.class, StringTag::getAsString).map(NodeParent::parse).collect(Collectors.toList());
        List<Icon> icons = streamAndApply(nbt.getList("icons", 8), StringTag.class, StringTag::getAsString).map(Icon::fromString).collect(Collectors.toList());
        List<String> meta = streamAndApply(nbt.getList("meta", 8), StringTag.class, StringTag::getAsString).collect(Collectors.toList());
        return new BookNode(key, pages, icons, meta, betterParents, in, name, desc, x, y);
    }

    public boolean equals(Object o){
        if(this == o)
            return true;
        if(!(o instanceof BookNode))
            return false;
        BookNode entry = (BookNode)o;
        return key.equals(entry.key);
    }

    public int hashCode(){
        return Objects.hash(key);
    }

    /**
     * Returns a stream containing all of the pins of contained pages.
     *
     * @param world
     * 		The world the player is in.
     * @return A stream containing the pins of contained pages.
     */
    public Stream<Pin> getAllPins(Level world){
        return pages().stream().flatMap(section -> section.getPins(pages.indexOf(section), world, this));
    }
}
