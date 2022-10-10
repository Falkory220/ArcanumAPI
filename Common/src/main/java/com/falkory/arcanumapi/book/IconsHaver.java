package com.falkory.arcanumapi.book;

import net.minecraft.client.Minecraft;

import java.util.List;

public interface IconsHaver {
    List<Icon> getIcons();

    //todo iterate further through keys
    default Icon getIcon(){
        List<Icon> icons = getIcons();
        if(icons.size() == 1) return icons.get(0);
        if(icons.size() > 1) return icons.get( (int)(((System.currentTimeMillis()/600) % icons.size()) + icons.size()) % icons.size());
        return Icon.fromString("minecraft:bedrock");
    }
}
