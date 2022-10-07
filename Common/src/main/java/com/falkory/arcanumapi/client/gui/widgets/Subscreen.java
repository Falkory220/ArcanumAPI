package com.falkory.arcanumapi.client.gui.widgets;

import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link NarratableEntry Narratable} screen type that keeps track of its own position.
 * Made to be used with a {@link com.falkory.arcanumapi.client.gui.MultiScreen Multiscreen}
 * @see com.falkory.arcanumapi.client.gui.MultiScreen MultiScreen
 * @see Screen
 * @see LayeredWidgetHolder
 * @see BookTabList
 * */
//todo couldn't this just be a custom widget type with screen functionality and a mouse event listener?? would need widget holding custom too,
public abstract class Subscreen extends Screen implements NarratableEntry {
    private int x;
    private int y;

    protected Subscreen(Component component) {
        super(component);
    }

    /***/
    public void setPos(int x, int y){
        this.x = x;
        this.y = y;
        whenMoved(x, y);
    }

    /**@param x The {@link Subscreen}'s new {@link Subscreen#x} position.
     * @see Subscreen#setPos(int x, int y)*/
    public void setX(int x){this.x = x; whenMoved(x, y);}
    /**@param y The {@link Subscreen}'s new {@link Subscreen#y} position.
     * @see Subscreen#setPos(int x, int y)*/
    public void setY(int y){this.y = y; whenMoved(x, y);}
    /**@return The object's current {@link Subscreen#x} position.*/
    public int getX(){return x;}
    /**@return The object's current {@link Subscreen#y} position.*/
    public int getY(){return y;}

    /**
     * Gets called when the object's position is changed.
     **/
    protected void whenMoved(int x, int y){}

    //narratable entry stuff

    @Override public @NotNull NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override public void updateNarration(@NotNull NarrationElementOutput narrationElementOutput) {}
}
