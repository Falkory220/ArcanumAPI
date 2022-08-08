package com.falkory.arcanumapi.book.content.requirements;

import com.falkory.arcanumapi.book.Requirement;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import static com.falkory.arcanumapi.ArcanumCommon.AmId;

public class ItemTagRequirement extends Requirement {

    protected TagKey<Item> tag;
    protected ResourceLocation tagName;

    public static final ResourceLocation TYPE = AmId("item_tag");

    public ItemTagRequirement(ResourceLocation tagName){
        this(TagKey.create(Registry.ITEM_REGISTRY, tagName), tagName);
    }

    public ItemTagRequirement(TagKey<Item> tag, ResourceLocation tagName){
        this.tag = tag;
    }

    public boolean satisfied(Player player){
        return player.getInventory().clearOrCountMatchingItems(x -> x.is(tag), 0, player.inventoryMenu.getCraftSlots()) >= (getAmount() == 0 ? 1 : getAmount());//.func_234564_a_(x -> x.getItem().isIn(tag), 0, player.container.func_234641_j_()) >= (getAmount() == 0 ? 1 : getAmount());
    }

    public void take(Player player){
        player.getInventory().clearOrCountMatchingItems(x -> x.is(tag), getAmount(), player.inventoryMenu.getCraftSlots());
    }

    public ResourceLocation type(){
        return TYPE;
    }

    public CompoundTag data(){
        CompoundTag compound = new CompoundTag();
        compound.putString("itemTag", tagName.toString());
        return compound;
    }

    public TagKey<Item> getTag(){
        return tag;
    }

    public ResourceLocation getTagName(){
        return tagName;
    }
}