package com.falkory.arcanumapi.book.layers;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class TabLayer {
    // this whole thing is one biiig snip (thank you Luna)

    ////////// static stuff
    
    private static Map<ResourceLocation, Supplier<TabLayer>> factories = new LinkedHashMap<>();
    private static Map<ResourceLocation, Function<CompoundTag, TabLayer>> deserializers = new LinkedHashMap<>();

    public static TabLayer makeLayer(ResourceLocation type, JsonObject content, ResourceLocation file, float speed, float vanishZoom){
        if(getBlank(type) != null){
            TabLayer layer = getBlank(type).get();
            layer.setSpeed(speed);
            layer.setVanishZoom(vanishZoom);
            layer.load(content, file);
            return layer;
        }else{ 
            return null;
        }
    }

    public static TabLayer deserialize(CompoundTag passData){
        ResourceLocation type = new ResourceLocation(passData.getString("type"));
        CompoundTag data = passData.getCompound("data");
        float speed = passData.getFloat("speed");
        float vanishZoom = passData.getFloat("vanishZoom");
        if(deserializers.get(type) != null){
            TabLayer layer = deserializers.get(type).apply(data);
            layer.setSpeed(speed).setVanishZoom(vanishZoom);
            return layer;
        }
        return null;
    }

    public static Supplier<TabLayer> getBlank(ResourceLocation type){
        return factories.get(type);
    }

    public static void init(){
        factories.put(ImageLayer.TYPE, ImageLayer::new);
        deserializers.put(ImageLayer.TYPE, nbt -> new ImageLayer(nbt.getString("image")));
    }

    ///////// instance stuff


    protected float speed = 0.5f, vanishZoom = -1;

    public float speed(){
        return speed;
    }

    public float vanishZoom(){
        return vanishZoom;
    }

    public TabLayer setSpeed(float speed){
        this.speed = speed;
        return this;
    }

    public TabLayer setVanishZoom(float vanishZoom){
        this.vanishZoom = vanishZoom;
        return this;
    }

    public CompoundTag getPassData(){
        CompoundTag nbt = new CompoundTag();
        nbt.putString("type", type().toString());
        nbt.put("data", data());
        nbt.putFloat("speed", speed());
        nbt.putFloat("vanishZoom", vanishZoom());
        return nbt;
    }

    public abstract ResourceLocation type();

    public abstract CompoundTag data();

    public abstract void load(JsonObject data, ResourceLocation file);

    public abstract void render(PoseStack stack, int x, int y, int width, int height, float xPan, float yPan, float parallax, float xOff, float yOff, float zoom);
}
