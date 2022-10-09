package com.falkory.arcanumapi.client.gui.widget.menu;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

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
    public int x;
    public int y;

    protected Subscreen(Component component) {
        super(component);
    }

    /***/
    public void setPos(int x, int y){
        this.x = x;
        this.y = y;
        whenMoved(x, y);
    }

    /**
     * Should be called when the object's position is changed, so it knows it moved.
     **/
    protected void whenMoved(int x, int y){}

    @Override public boolean mouseClicked(double mx, double my, int button) {
        if(isMouseOver(mx, my)) {
            super.mouseClicked(mx, my, button);
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    // super method assumes the widget is at 0,0
    @Override public Optional<GuiEventListener> getChildAt(double $$0, double $$1) {
        //return super.getChildAt($$0-x, $$1-y);
        return super.getChildAt($$0, $$1);
    }

    @Override public boolean isMouseOver(double mx, double my){
        return !(y > my || my > y + height || x > mx || mx > x + width);
    }

    //narratable entry stuff

    @Override public @NotNull NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override public void updateNarration(@NotNull NarrationElementOutput narrationElementOutput) {}
}
