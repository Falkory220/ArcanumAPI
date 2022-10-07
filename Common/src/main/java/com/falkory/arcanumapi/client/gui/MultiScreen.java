package com.falkory.arcanumapi.client.gui;

import com.falkory.arcanumapi.client.gui.widgets.Subscreen;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
/**
 * A {@link Screen} type that can hold other screens which it passes screen-exclusive input events to.
 *
 * */
//Feel like I read about this being a bad idea somewhere. still sounds fun though!
public abstract class MultiScreen extends Screen {
    protected List<Subscreen> subscreens = Lists.newLinkedList();

    protected MultiScreen(Component component) {
        super(component);
    }

    /** */
    public Subscreen addSubscreen(Subscreen scrn){
        subscreens.add(scrn);
        return addRenderableWidget(scrn);
    }

    @Override protected void clearWidgets() {
        subscreens.clear();
        super.clearWidgets();
    }


    // multi-delegation ! todo , add more events and test these. got sleepy so only did these as needed for now. 🐈

    /**Probably omitted from {@link net.minecraft.client.gui.components.events.ContainerEventHandler ContainerEventHandler} for valid reasons*/
    @Override public void mouseMoved(double mx, double my) {
        for (Subscreen a : subscreens) a.mouseMoved(mx, my);
        super.mouseMoved(mx, my);
    }

    @Override public void afterMouseAction() {
        subscreens.forEach(Subscreen::afterMouseAction);
        super.afterMouseAction();
    }

    @Override public void afterMouseMove() {
        subscreens.forEach(Subscreen::afterMouseAction);
        super.afterMouseMove();
    }
}
