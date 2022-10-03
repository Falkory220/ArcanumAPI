package com.falkory.arcanumapi.client.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ListIterator;

/**
 * A widget that holds a group of widgets that may overlap,
 * handling cases where both might be interacted with. Ensures that users
 * can see buttons layered in the order they're checked for {@link GuiEventListener} methods.<p>
 * As a side effect, (or rather because {@link LayeredWidgetHolder#render(PoseStack, int, int, float) it's the point})
 * our {@link LayeredWidgetHolder#renderables widgets} appear oldest on top.
 * @see GuiEventListener
 * @see Screen
 * */
public class LayeredWidgetHolder extends Screen implements NarratableEntry{
    /**@see Screen#renderables*/
    protected final List<Widget> renderables = Lists.newArrayList();
    private boolean mouseSinceTab = false;

    public LayeredWidgetHolder() {
        super(Component.empty());
    }

    /**
     * Adds a widget to our local {@link LayeredWidgetHolder#renderables} list on its way in, since {@link Screen#renderables} is private. <p>
     * Currently restricted to GuiEventListeners
     * */
    public <T extends GuiEventListener & Widget & NarratableEntry> T addLayeredWidget(T widget){
        this.renderables.add(widget);
        return addRenderableWidget(widget);
    }

    /**@see LayeredWidgetHolder#clearWidgets()*/
    public void clear(){
        clearWidgets();
    }

    /**
     * Updates the {@link LayeredWidgetHolder LayeredWidgetHolder's} focus to a contained {@link Widget widget}.
     * @param widget {@link Widget} to focus. Can be nulled to focus nothing.
     * */
    public void select(@Nullable Widget widget){
        if(getFocused() != null) {
            // we always unfocus
            if(getFocused().changeFocus(true)) getFocused().changeFocus(true);
        }
        if(!renderables.contains(widget) || widget == null) {setFocused(null); return;}

        if(!(widget instanceof GuiEventListener)) return;
        setFocused((GuiEventListener) widget);
        if(!((GuiEventListener) widget).changeFocus(true))
            ((GuiEventListener) widget).changeFocus(true);
    }

    /**
     * The {@link Widget} method. <p>
     * Calls {@link LayeredWidgetHolder#renderables renderables'} renders in reverse creation order, so they're drawn in order of input event priority.<p>
     * Additionally, updates {@link LayeredWidgetHolder#focused} by mouse position since hover is unreliable when widgets overlap.
     * */
    @Override public void render(PoseStack poseStack, int mx, int my, float v) {
        ListIterator<Widget> iterator = this.renderables.listIterator(this.renderables.size());
        while(iterator.hasPrevious()) iterator.previous().render(poseStack, mx, my, v);

        // have only one hovered object in the holder
        //TODO: mouse events won't trigger unless it's the active screen. Widgetification is at stake
        //if(!mouseSinceTab) return; // keeping us tab friendly, nya
        getChildAt(mx, my).ifPresentOrElse(
          c -> {if (!c.equals(getFocused())) select((Widget)c);},
          ()-> {if(getFocused() != null) select(null);}
        );
        //mouseSinceTab = false;
    }

    //Screen
    /**
     * Intercepts widget clearing to make sure we clear our local {@link this.renderables} list when needed.
     * @see LayeredWidgetHolder#addLayeredWidget(GuiEventListener)
     * */
    @Override protected void clearWidgets() {
        this.renderables.clear();
        super.clearWidgets();
    }

    @Override public void afterMouseMove() {
        mouseSinceTab = true;
        super.afterMouseMove();
    }

    //NarratableEntry

    @Override public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override public void updateNarration(NarrationElementOutput narrationElementOutput) {}
}
