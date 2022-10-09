package com.falkory.arcanumapi.client.gui;

//modified from net.arcanamod.client.gui.ResearchBookScreen

import com.falkory.arcanumapi.book.BookMain;
import com.falkory.arcanumapi.client.gui.widget.menu.BookTabList;
import com.falkory.arcanumapi.client.gui.widget.menu.LayerWindow;
import com.falkory.arcanumapi.client.gui.widget.menu.LayerWindowFramed;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

public class BookMainScreen extends BookScreen {
    ItemStack sender;


    public BookMainScreen(BookMain book, Screen parentScreen, ItemStack item) {
        super(book, parentScreen);
        this.sender = item;
    }

    @Override public void render(PoseStack stack, int $$1, int $$2, float tickDelta) {
        if(debug) drawDebug(stack);
        super.render(stack, $$1, $$2, tickDelta);
    }


    //todo move to more elaborate debug scheme. super screen holds all debug render and gets passed the strings from subscreens?
    protected void drawDebug(PoseStack stack){
        //drawString(stack, this.font, "zoom:"+((Float)zoom), 0, 0, 1);
        //drawString(stack, this.font, "x:"+xPan+"   y:"+yPan,0,10,1);
        //drawString(stack, this.font, "xMax:"+maxPanX+"   yMax:"+maxPanY,0,20,1);
    }


    //TODO: zoom in @ mouse position
    @Override public boolean mouseScrolled(double mouseX, double mouseY, double scroll) { //I hereby propose a new zoom schema ! 1 is as far as it can zoom out, as in 1:1. any zoom is how much it's amplified from max distance

        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    private void initTabButtons(){
        LayerWindow layerWindow = new LayerWindowFramed(getBook());
        BookTabList tabList = new BookTabList(layerWindow);
        addSubscreen(layerWindow);
        addSubscreen(tabList);
        tabList.setPos(0, layerWindow.y);

    }

    @Override
    protected void init() {
        super.init();
        initTabButtons();
    }
}
