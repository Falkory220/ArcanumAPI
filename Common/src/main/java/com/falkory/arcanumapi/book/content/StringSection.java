package com.falkory.arcanumapi.book.content;

//modified from net.arcanamod.systems.research.impls.StringSection

import com.falkory.arcanumapi.book.BookPage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

import static com.falkory.arcanumapi.ArcanumCommon.AmId;

public class StringSection extends BookPage {

    public static final ResourceLocation TYPE = AmId("string");

    String content;

    public StringSection(String content){
        this.content = content;
    }

    public ResourceLocation getType(){
        return TYPE;
    }

    public CompoundTag getData(){
        CompoundTag tag = new CompoundTag();
        tag.putString("text", getText());
        return tag;
    }

    public String getText(){
        return content;
    }

    public boolean equals(Object o){
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        StringSection section = (StringSection)o;
        return content.equals(section.content);
    }

    public int hashCode(){
        return Objects.hash(content);
    }
}
