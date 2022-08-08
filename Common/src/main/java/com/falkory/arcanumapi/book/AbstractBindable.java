package com.falkory.arcanumapi.book;

import com.falkory.arcanumapi.util.Identifiable;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface AbstractBindable<T extends Identifiable> {
    void setHeld(Map<ResourceLocation, T> map);
}
