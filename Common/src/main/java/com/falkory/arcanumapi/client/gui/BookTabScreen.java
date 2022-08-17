package com.falkory.arcanumapi.client.gui;

import com.falkory.arcanumapi.book.BookMain;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

import static com.falkory.arcanumapi.client.gui.ClientGuiUtils.*;
import static net.minecraft.util.Mth.clamp;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;

public class BookTabScreen extends AbstractBookScreen{
    ItemStack sender;

    //this is a funny way to make this persistent across instances alsdkjfhn
    static float xPan = 0;
    static float yPan = 0;
    static float targetZoom = 1.0f;
    static float zoom = targetZoom;
    static boolean showZoom = false;

    static final float ZOOM_MULTIPLIER = 2.0f;

    public BookTabScreen(BookMain book, Screen parentScreen, ItemStack item) {
        super(book, parentScreen);
        this.sender = item;
    }

    @Override public void render(PoseStack stack, int $$1, int $$2, float $$3) {
        //this used to be in the arcana config, copied default settings todo make user config option
        int frameWidth = width - 60;
        int frameHeight = height - 20;
        int minDrawSize = Math.max(frameWidth, frameHeight);
        int zoomDrawSize = (int)(minDrawSize*zoom);


        // draw stuff
        // 224x196 viewing area
        int scale = (int)this.minecraft.getWindow().getGuiScale();
        int x = (width - frameWidth + 32) / 2, y = (height - frameHeight + 34) / 2;
        int visibleWidth = frameWidth - 32, visibleHeight = frameHeight - 34;
        GL11.glScissor(x * scale, y * scale, visibleWidth * scale, visibleHeight * scale);
        // scissors on
        GL11.glEnable(GL_SCISSOR_TEST);
        float scrollAbleX = ((minDrawSize-visibleWidth)/2f);
        float scrollAbleY = ((minDrawSize-visibleHeight)/2f);
        xPan = clamp(xPan, -scrollAbleX, scrollAbleX);
        yPan = clamp(yPan, -scrollAbleY, scrollAbleY);

        //renderResearchBackground(stack);
        //renderEntries(stack, partialTicks);
        ResourceLocation bg = new ResourceLocation("arcana","textures/research/eldritch_bg.png");
        drawDualScaledSprite(stack, x, y, -xPan+scrollAbleX, -yPan+scrollAbleY, width, height, minDrawSize, minDrawSize, bg);

        // scissors off
        GL11.glDisable(GL_SCISSOR_TEST);

        super.render(stack, $$1, $$2, $$3);
    }


    public float getXOffset(){
        return ((width / 2f) * (1 / zoom)) + (xPan / 2f);
    }
    public float getYOffset(){
        return ((height / 2f) * (1 / zoom)) - (yPan / 2f);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
        xPan += (deltaX * ZOOM_MULTIPLIER) / zoom;
        yPan += (deltaY * ZOOM_MULTIPLIER) / zoom;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll){
        float amount = 1.2f;
        if((scroll < 0 && zoom > 0.5) || (scroll > 0 && zoom < 1))
            zoom *= scroll > 0 ? amount : 1 / amount;
        if(zoom > 1f)
            zoom = 1f;
        return super.mouseScrolled(mouseX, mouseY, scroll);
    }
}
