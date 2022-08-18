package com.falkory.arcanumapi.client.gui;

import com.falkory.arcanumapi.book.BookMain;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import static com.falkory.arcanumapi.client.gui.ClientGuiUtils.*;

public abstract class AbstractBookScreen extends Screen {
    private BookMain book;
    private Screen parentScreen;
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

    @Override public void render(PoseStack stack, int $$1, int $$2, float $$3) {
       // drawSprite(stack, 192, 3, 0, 0, 192, 192, new ResourceLocation("textures/gui/book.png"));
        //drawScaledSprite(stack, 0, 128, 0, 0,  192, 192, 0.25f, new ResourceLocation("textures/gui/book.png"));
        //drawString(stack, this.font, "hey lily look at this !!", 192+40, 20, 20);
        //drawString(stack, this.font, "nya", 10, 10, 256);
        super.render(stack, $$1, $$2, $$3);
    }

    @Override public boolean isPauseScreen() {
        return false;
    }

}
