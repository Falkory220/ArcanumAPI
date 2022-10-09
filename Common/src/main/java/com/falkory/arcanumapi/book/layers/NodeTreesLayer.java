package com.falkory.arcanumapi.book.layers;

import com.falkory.arcanumapi.book.BookNode;
import com.falkory.arcanumapi.client.gui.widget.menu.LayerWindow;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.falkory.arcanumapi.ArcanumCommon.AmId;

public class NodeTreesLayer extends BookLayer implements NodeLayer {
    public static final ResourceLocation TYPE = AmId("nodetrees");

    //BookLayer things

    @Override public ResourceLocation type() {
        return TYPE;
    }

    @Override public CompoundTag data() {
        return new CompoundTag();
    }

    @Override public void load(JsonObject data, ResourceLocation file) {}

    @Override public void render(PoseStack stack, LayerWindow parent, float drawSize, float spd) {

    }

    //NodeLayer things
    protected Map<ResourceLocation, BookNode> nodes;

    @Override public BookNode node(BookNode node){
        return nodes.get(node.key());
    }

    @Override public List<BookNode> nodes(){
        return new ArrayList<>(nodes.values());
    }

    @Override public BookNode getNode(ResourceLocation key){
        return nodes.get(key);
    }

    @Override public Map<ResourceLocation, BookNode> getNodes() {
        return nodes;
    }
}
