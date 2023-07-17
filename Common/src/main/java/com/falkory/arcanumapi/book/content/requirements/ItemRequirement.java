package com.falkory.arcanumapi.book.content.requirements;

import com.falkory.arcanumapi.book.Requirement;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static com.falkory.arcanumapi.ArcanumCommon.AmId;

public class ItemRequirement extends Requirement {

    // perhaps support NBT in the future? will be required for enchantments in the future at least.
    protected Item item;
    protected ItemStack stack;

    public static final ResourceLocation TYPE = AmId("item");

    public ItemRequirement(Item item){
        this.item = item;
    }

    public boolean satisfied(Player player){
        return player.getInventory().clearOrCountMatchingItems(x -> x.getItem() == item, 0, player.inventoryMenu.getCraftSlots()) >= (getAmount() == 0 ? 1 : getAmount());
    }

    public void take(Player player){
        player.getInventory().clearOrCountMatchingItems(x -> x.getItem() == item, getAmount(), player.inventoryMenu.getCraftSlots());
    }

    public ResourceLocation type(){
        return TYPE;
    }

    public CompoundTag data(){
        CompoundTag compound = new CompoundTag();
        compound.putString("itemType", String.valueOf(BuiltInRegistries.ITEM.getKey(item)));
        return compound;
    }

    public Item getItem(){
        return item;
    }

    public ItemStack getStack(){
        if(stack == null)
            return stack = new ItemStack(getItem());
        return stack;
    }
}
