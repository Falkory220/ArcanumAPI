package com.falkory.arcanumapi.client.gui;

import com.falkory.arcanumapi.api.ArcanumAPI;
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
        float scrollableX = (zoomDrawSize-visibleWidth)/2f;
        float scrollableY = (zoomDrawSize-visibleHeight)/2f;
        xPan = clamp(xPan, -scrollableX, scrollableX);
        yPan = clamp(yPan, -scrollableY, scrollableY);

        //temp
        float spd = 1.5f;

        //renderResearchBackground(stack);
        //renderEntries(stack, partialTicks);
        ResourceLocation bg = new ResourceLocation("arcana","textures/research/eldritch_bg.png");
        drawDualScaledSprite(stack, x, y, xPan+scrollableX, yPan+scrollableY, width, height, zoomDrawSize, zoomDrawSize, bg);
        //drawDualScaledSprite(stack, x, y, xPan+((minDrawSize*(zoom)-visibleWidth)/2), yPan+((minDrawSize*(zoom)-visibleHeight)/2), width, height, (int)(minDrawSize*(zoom)), (int)(minDrawSize*(zoom)), new ResourceLocation("minecraft","textures/block/glass.png"));
        drawDualScaledSprite(stack, x, y,
          ((xPan/scrollableX)*(zoomDrawSize*spd-visibleWidth )/2)+((minDrawSize*zoom*spd) -visibleWidth )/2f,
          ((yPan/scrollableY)*(zoomDrawSize*spd-visibleHeight)/2)+((minDrawSize*zoom*spd) -visibleHeight)/2f,
          width, height,
          (int)(zoomDrawSize*spd),
          (int)(zoomDrawSize*spd),
          new ResourceLocation("minecraft","textures/block/glass.png"));
        // scissors off
        GL11.glDisable(GL_SCISSOR_TEST);

        if(ArcanumAPI.LOG.isDebugEnabled()) {drawDebug(stack);}

        super.render(stack, $$1, $$2, $$3);
    }
    private void drawDebug(PoseStack stack){
        drawString(stack, this.font, "zoom:"+((Float)zoom), 0, 0, 1);
        drawString(stack, this.font, "x:"+xPan+"   y:"+yPan,0,10,1);
    }

    public float getXOffset(){
        return ((width / 2f) * (1 / zoom)) + (xPan / 2f);
    }
    public float getYOffset(){
        return ((height / 2f) * (1 / zoom)) - (yPan / 2f);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
        xPan -= (deltaX * ZOOM_MULTIPLIER) / zoom;
        yPan -= (deltaY * ZOOM_MULTIPLIER) / zoom;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll){ //I hereby propose a new zoom schema ! 1 is as far as it can zoom out, as in 1:1. any zoom is how much it's amplified from max distance
        float amount = 1.2f;
        if((scroll < 0 && zoom > 1) || (scroll > 0 && zoom < 4))
            zoom *= scroll > 0 ? amount : 1 / amount;
        if(zoom > 4f)
            zoom = 4f;
        return super.mouseScrolled(mouseX, mouseY, scroll);
    }
}
