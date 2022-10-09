package com.falkory.arcanumapi.client.gui.widget.menu;

import com.falkory.arcanumapi.api.ArcanumAPI;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

/**
 * A widget that holds a group of widgets that may overlap,
 * handling cases where both might be interacted with. Ensures that users
 * can see buttons layered in the order they're checked for {@link GuiEventListener} methods.<p>
 * As a side effect, (or rather because {@link LayeredWidgetHolder#render(PoseStack, int, int, float) it's the point})
 * our {@link LayeredWidgetHolder#renderables widgets} appear oldest on top.
 * @see GuiEventListener
 * @see Screen
 * */
public class LayeredWidgetHolder extends Subscreen {
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

    /**
     * Updates the {@link LayeredWidgetHolder LayeredWidgetHolder's} focus to a contained {@link Widget widget}.
     * @param widget {@link Widget} to focus. Can be nulled to focus nothing.
     * */
    public void select(@Nullable Widget widget){
        if(widget == getFocused()) {
            ArcanumAPI.LOG.info("tried to select already selected tab");
            return;
        }
        mouseSinceTab = false;
        if(getFocused() != null) {
            // we always unfocus current if possible before selecting new
            if(getFocused().changeFocus(true)) getFocused().changeFocus(true);
        }
        if(!renderables.contains(widget) || widget == null) {setFocused(null); return;}

        if(!(widget instanceof GuiEventListener)) return;
        setFocused((GuiEventListener) widget);
        if(!((GuiEventListener) widget).changeFocus(true)) ((GuiEventListener) widget).changeFocus(true);
    }

    /**
     * The {@link Widget} method. <p>
     * Calls {@link LayeredWidgetHolder#renderables renderables'} renders in reverse creation order, so they're drawn in order of input event priority.<p>
     * Additionally, updates {@link LayeredWidgetHolder#focused} by mouse position since hover is unreliable when widgets overlap.
     * */
    @Override public void render(PoseStack poseStack, int mx, int my, float v) {
        ListIterator<Widget> iterator = this.renderables.listIterator(this.renderables.size());
        while(iterator.hasPrevious()) iterator.previous().render(poseStack, mx, my, v);
    }

    //mojang whyyy
    @Override public boolean changeFocus(boolean $$0) {
        return super.changeFocus($$0);
    }

    //Screen
    /**
     * Intercepts widget clearing to make sure we clear our local {@link LayeredWidgetHolder#renderables} list when needed.
     * @see LayeredWidgetHolder#addLayeredWidget(GuiEventListener)
     * */
    @Override protected void clearWidgets() {
        this.renderables.clear();
        super.clearWidgets();
    }

    @Override public void mouseMoved(double mx, double my) {
        Optional<GuiEventListener> nya = getChildAt(mx, my);
        if(nya.isPresent()){
            if (!(nya.get()==getFocused())) {
                select((Widget)nya.get());
            }
            mouseSinceTab = true;
        }
        super.mouseMoved(mx, my);
    }
}
