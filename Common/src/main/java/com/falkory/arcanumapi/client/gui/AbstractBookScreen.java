package com.falkory.arcanumapi.client.gui;

import com.falkory.arcanumapi.book.BookMain;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.Screen;

import javax.annotation.ParametersAreNonnullByDefault;


public abstract class AbstractBookScreen extends Screen {
    private final BookMain book;
    private final Screen parentScreen;
    //List<PinButton> pinButtons;

    protected AbstractBookScreen(BookMain book, Screen parentScreen) {
        super(NarratorChatListener.NO_TITLE);
        this.book = book;
        this.parentScreen = parentScreen;
        this.height = Minecraft.getInstance().getWindow().getHeight();
        this.width = Minecraft.getInstance().getWindow().getWidth();
    }

    public BookMain getBook(){
        return book;
    }

    @ParametersAreNonnullByDefault
    @Override public void render(PoseStack stack, int $$1, int $$2, float $$3) {
        super.render(stack, $$1, $$2, $$3);
    }

    @Override public boolean isPauseScreen() {
        return false;
    }

}
