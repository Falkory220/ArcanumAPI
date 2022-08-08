package com.falkory.arcanumapi.book.layers;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.falkory.arcanumapi.ArcanumCommon.AmId;

public class ImageLayer extends TabLayer{
    // this whole thing is one biiig snip (thank you Luna)

    public static final ResourceLocation TYPE = AmId("image");

    public ResourceLocation image;

    private static final Logger LOGGER = LogManager.getLogger();

    public ImageLayer(){}

    public ImageLayer(String image){
        this.image = new ResourceLocation(image);
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
        if(imagePrim != null)
            if(imagePrim.isString()){
                ResourceLocation base = new ResourceLocation(imagePrim.getAsString());
                image = new ResourceLocation(base.getNamespace(), "textures/" + base.getPath() + ".png");
            }else
                LOGGER.error("Field \"image\" for an image background layer was not a string, in " + file + "!");
        else
            LOGGER.error("Field \"image\" for an image background layer was not defined, in " + file + "!");
    }

    public void render(PoseStack stack, int x, int y, int width, int height, float xPan, float yPan, float parallax, float xOff, float yOff, float zoom){

    }
}
