package com.falkory.arcanumapi.book.content.requirements;

import com.falkory.arcanumapi.book.Requirement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static com.falkory.arcanumapi.ArcanumCommon.AmId;

public class XpRequirement extends Requirement {

    public static final ResourceLocation TYPE = AmId("xp");

    public boolean satisfied(Player player){
        return player.experienceLevel >= getAmount();
    }

    public void take(Player player){
        player.experienceLevel -= getAmount();
    }

    public ResourceLocation type(){
        return TYPE;
    }

    public CompoundTag data(){
        return new CompoundTag();
    }
}