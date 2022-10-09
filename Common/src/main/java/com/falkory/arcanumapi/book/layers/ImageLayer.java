package com.falkory.arcanumapi.book.layers;

//modified from net.arcanamod.systems.impls.ImageLayer

import com.falkory.arcanumapi.client.gui.widget.menu.LayerWindow;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.falkory.arcanumapi.ArcanumCommon.AmId;
import static com.falkory.arcanumapi.client.gui.ClientGuiUtils.drawDualScaledSprite;

public class ImageLayer extends BookLayer {

    public static ResourceLocation TYPE = AmId("image");

    private ResourceLocation image;
    private float tiles = 1;

    private static final Logger LOGGER = LogManager.getLogger();

    public ImageLayer(){}

    public ImageLayer(String image){
        this(image, 1.0f);
    }
    public ImageLayer(String image, float speed){
        this.image = new ResourceLocation(image);
        this.speed = speed;
    }

    public ResourceLocation type(){
        return TYPE;
    }

    public CompoundTag data(){
        CompoundTag data = new CompoundTag();
        data.putString("image", image.toString());
        return data;
    }

    public void load(JsonObject data, ResourceLocation file){
        JsonPrimitive imagePrim = data.getAsJsonPrimitive("image");
        if(imagePrim == null){
            LOGGER.error("Field \"image\" for an image background layer was not defined, in " + file + "!");
            return;}
        if(!imagePrim.isString()){
            LOGGER.error("Field \"image\" for an image background layer was not a string, in " + file + "!");
            return;}

        JsonPrimitive tilePrim = data.getAsJsonPrimitive("tileamt");
        if(tilePrim != null){
            if(!tilePrim.isNumber()){
                this.tiles = tilePrim.getAsFloat();
            }else{
                LOGGER.error("Field \"tileamt\" for image tiling coefficient was not a number, in " + file + "!");
            }
        }

        ResourceLocation base = new ResourceLocation(imagePrim.getAsString());
        image = new ResourceLocation(base.getNamespace(), "textures/" + base.getPath() + ".png");
    }

    @Override public void render(PoseStack stack, LayerWindow parent, float drawSize, float tickDelta){
        float zoomSize = drawSize*LayerWindow.zoom;
        float scaledSize = zoomSize*speed;
        drawDualScaledSprite(stack, parent.x, parent.y,
          (((2f * LayerWindow.maxPanX * parent.getXPan() /(zoomSize- parent.width))+1)*(scaledSize- parent.width))/2f,
          (((2f * LayerWindow.maxPanY * parent.getYPan() /(zoomSize- parent.height))+1)*(scaledSize- parent.height))/2f,
          parent.width, parent.height,
          (int)(scaledSize/tiles), (int)(scaledSize/tiles),
          image);
    }
}
