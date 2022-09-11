package com.falkory.arcanumapi.book.layers;

import com.falkory.arcanumapi.client.gui.BookMainScreen;
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
    // this whole thing was one biiig snip (thank you Luna)

    public static ResourceLocation TYPE = AmId("image");

    private ResourceLocation image;

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

        ResourceLocation base = new ResourceLocation(imagePrim.getAsString());
        image = new ResourceLocation(base.getNamespace(), "textures/" + base.getPath() + ".png");
    }

    @Override public void render(PoseStack stack, BookMainScreen parent, float drawSize, float tickDelta){
        float zoomSize = drawSize*BookMainScreen.zoom;
        float scaledSize = zoomSize*speed;
        drawDualScaledSprite(stack, BookMainScreen.minBookX, BookMainScreen.minBookY,
          (((2f * BookMainScreen.maxPanX * BookMainScreen.xPan /(zoomSize- BookMainScreen.bookWidth))+1)*(scaledSize- BookMainScreen.bookWidth))/2f,
          (((2f * BookMainScreen.maxPanY * BookMainScreen.yPan /(zoomSize- BookMainScreen.bookHeight))+1)*(scaledSize- BookMainScreen.bookHeight))/2f,
          parent.width, parent.height,
          (int)(scaledSize), (int)(scaledSize),
          image);
    }
}
