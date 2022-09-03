package com.falkory.arcanumapi.book.layers;

import com.falkory.arcanumapi.client.gui.BookMainScreen;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BookLayer {
    // this whole thing is one biiig snip (thank you Luna)

    ////////// static stuff

    private static Map<ResourceLocation, Supplier<BookLayer>> factories = new LinkedHashMap<>();
    private static Map<ResourceLocation, Function<CompoundTag, BookLayer>> deserializers = new LinkedHashMap<>();

    public static BookLayer makeLayer(ResourceLocation type, JsonObject content, ResourceLocation file, float speed, float vanishZoom){
        if(getBlank(type) != null){
            BookLayer layer = getBlank(type).get();
            layer.setSpeed(speed);
            layer.setVanishZoom(vanishZoom);
            layer.load(content, file);
            return layer;
        }else{ 
            return null;
        }
    }

    public static BookLayer deserialize(CompoundTag passData){
        ResourceLocation type = new ResourceLocation(passData.getString("type"));
        CompoundTag data = passData.getCompound("data");
        float speed = passData.getFloat("speed");
        float vanishZoom = passData.getFloat("vanishZoom");
        if(deserializers.get(type) != null){
            BookLayer layer = deserializers.get(type).apply(data);
            layer.setSpeed(speed).setVanishZoom(vanishZoom);
            return layer;
        }
        return null;
    }

    public static Supplier<BookLayer> getBlank(ResourceLocation type){
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

    public BookLayer setSpeed(float speed){
        this.speed = speed;
        return this;
    }

    public BookLayer setVanishZoom(float vanishZoom){
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

    /** @return Any subclass-specific data that needs to be recorded by the serializer */
    public abstract CompoundTag data();

    /** Applies subclass-specific deserialization.
     * @param data The json object passed by the main {@link BookLayer} deserializer
     * */
    public abstract void load(JsonObject data, ResourceLocation file);
    /** Called by the book screen. All draws will be in a GL scissor,
     * so no worries about drawing over the frame.
     * @param frameMax Maximum width/height of the scissors.
     * @param spd A combination scale/scroll multiplication factor. <br>
     * 1.0 is at true scale when zoomed out, 2.0 is double size. Values less than 1.0 will cause issues. */
    //todo look into the zoom < 1.0f render issues, it shouldn't break *that* hard
    public abstract void render(PoseStack stack, BookMainScreen parent, float frameMax, float spd);
}
