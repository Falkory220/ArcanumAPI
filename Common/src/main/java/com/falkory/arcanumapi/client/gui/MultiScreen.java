package com.falkory.arcanumapi.client.gui;

import com.falkory.arcanumapi.client.gui.widget.menu.LayeredWidgetHolder;
import com.falkory.arcanumapi.client.gui.widget.menu.Subscreen;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
/**
 * A {@link Screen} type that can hold other screens which it passes screen-exclusive input events to.
 *
 * */
//Feel like I read about this being a bad idea somewhere. still sounds fun though!
public abstract class MultiScreen extends LayeredWidgetHolder {
    protected List<Subscreen> subscreens = Lists.newLinkedList();

    /** */
    public Subscreen addSubscreen(Subscreen scrn){
        scrn.init(minecraft, width, height);
        subscreens.add(scrn);
        return addLayeredWidget(scrn);
    }

    @Override protected void clearWidgets() {
        subscreens.clear();
        super.clearWidgets();
    }

    @Override protected void removeWidget(GuiEventListener $$0) {
        if($$0 instanceof Subscreen) this.subscreens.remove($$0);
        super.removeWidget($$0);
    }

    // multi-delegation ! todo , add more events and test these. got sleepy so only did these as needed for now. 🐈


    @Override public boolean mouseClicked(double mx, double my, int button) {
        for(Subscreen subscreen : subscreens) {
            if(subscreen.mouseClicked(mx, my, button)) {
                setFocused(subscreen);
                super.mouseClicked(mx, my, button);
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    /**Probably omitted from {@link net.minecraft.client.gui.components.events.ContainerEventHandler ContainerEventHandler} for valid reasons*/
    @Override public void mouseMoved(double mx, double my) {
        for (Subscreen a : subscreens) a.mouseMoved(mx, my);
    }

    @Override public void afterMouseAction() {
        subscreens.forEach(Subscreen::afterMouseAction);
        super.afterMouseAction();
    }

    @Override public void afterMouseMove() {
        subscreens.forEach(Subscreen::afterMouseAction);
        super.afterMouseMove();
    }

    @Override protected void init() {
        for(Subscreen s : subscreens) s.init(minecraft, width, height);
        super.init();
    }
}
