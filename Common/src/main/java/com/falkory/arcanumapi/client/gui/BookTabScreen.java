package com.falkory.arcanumapi.client.gui;

import com.falkory.arcanumapi.api.ArcanumAPI;
import com.falkory.arcanumapi.book.BookMain;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

import static com.falkory.arcanumapi.client.gui.ClientGuiUtils.*;
import static net.minecraft.util.Mth.abs;
import static net.minecraft.util.Mth.clamp;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;

public class BookTabScreen extends AbstractBookScreen{
    ItemStack sender;

    //this is a funny way to make this persistent across instances alsdkjfhn
    static float xPan = 0;
    static float yPan = 0;
    static float targetZoom = 1.0f;
    static float zoom = targetZoom;

    static final float ZOOM_MULTIPLIER = 2.0f;

    private static float maxPanX;
    private static float maxPanY;

    private static int minBookX;
    private static int minBookY;
    private static int bookWidth;
    private static int bookHeight;


    public BookTabScreen(BookMain book, Screen parentScreen, ItemStack item) {
        super(book, parentScreen);
        this.sender = item;
    }

    @Override public void render(PoseStack stack, int $$1, int $$2, float tickDelta) {
        float diff = targetZoom - zoom;
        if (abs(diff) < 0.1f) {zoom = targetZoom;} else {
            zoom += Math.min(tickDelta * (2 / 3f), 1) * diff;
        }
        //this used to be in the arcana config, copied default settings todo make user config option
        int frameWidth = width - 60;
        int frameHeight = height - 20;
        int frameSize = Math.max(frameWidth, frameHeight);

        int zoomDrawSize = (int)(frameSize*zoom);


        // draw stuff
        // 224x196 viewing area
        int scale = (int)this.minecraft.getWindow().getGuiScale();
        minBookX = (width - frameWidth + 32) / 2;
        minBookY = (height - frameHeight + 34) / 2;
        bookWidth = frameWidth - 32;
        bookHeight = frameHeight - 34;

        GL11.glScissor(minBookX * scale, minBookY * scale, bookWidth * scale, bookHeight * scale);
        // scissors on
        GL11.glEnable(GL_SCISSOR_TEST);
        maxPanX = (zoomDrawSize- bookWidth)/2f;
        maxPanY = (zoomDrawSize- bookHeight)/2f;

        //renderResearchBackground(stack);
        //renderEntries(stack, partialTicks);
        ResourceLocation bg = new ResourceLocation("arcana","textures/research/eldritch_bg.png");
        drawBackground(stack, minBookX, minBookY, frameSize, bookWidth, bookHeight, 1.0f, bg);
        drawBackground(stack, minBookX, minBookY, frameSize, bookWidth, bookHeight, 1.1f, new ResourceLocation("textures/block/glass.png"));

        // scissors off
        GL11.glDisable(GL_SCISSOR_TEST);

        if(ArcanumAPI.LOG.isDebugEnabled()) {drawDebug(stack);}

        super.render(stack, $$1, $$2, tickDelta);
    }
    private void drawDebug(PoseStack stack){
        drawString(stack, this.font, "zoom:"+((Float)zoom), 0, 0, 1);
        drawString(stack, this.font, "x:"+xPan+"   y:"+yPan,0,10,1);
    }

    //todo this should be in ImageLayer,,, later
    public void drawBackground(PoseStack stack, int x, int y, float drawSize, float drawWidth, float drawHeight, float spd, ResourceLocation texture){
        float zoomSize = drawSize*zoom;
        float scaledSize = zoomSize*spd;
        drawDualScaledSprite(stack, x, y,
          (((2f * maxPanX * xPan/(zoomSize-drawWidth ))+1)*(scaledSize-drawWidth ))/2f,
          (((2f * maxPanY * yPan/(zoomSize-drawHeight))+1)*(scaledSize-drawHeight))/2f,
          width, height,
          (int)(scaledSize), (int)(scaledSize),
          texture);
    }

    public float getXOffset(){
        return ((width / 2f) * (1 / zoom)) + (xPan / 2f);
    }
    public float getYOffset(){
        return ((height / 2f) * (1 / zoom)) - (yPan / 2f);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
        if(inWindow(mouseX, mouseY)) {
            xPan -= (deltaX * ZOOM_MULTIPLIER) / (zoom * maxPanX);
            yPan -= (deltaY * ZOOM_MULTIPLIER) / (zoom * maxPanY);
            xPan = clamp(xPan, -1f, 1f);
            yPan = clamp(yPan, -1f, 1f);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    //TODO: zoom in @ mouse position
    @Override public boolean mouseScrolled(double mouseX, double mouseY, double scroll) { //I hereby propose a new zoom schema ! 1 is as far as it can zoom out, as in 1:1. any zoom is how much it's amplified from max distance
        //if outside window, don't !
        if (inWindow(mouseX, mouseY)) {
            float amount = 1.2f;
            if ((scroll < 0 && targetZoom > 1) || (scroll > 0 && targetZoom < 4))
                targetZoom *= scroll > 0 ? amount : 1 / amount;
            targetZoom = clamp(targetZoom, 1f, 4f);
        }
        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    private boolean inWindow(double x, double y){
        return !(minBookY > y || y > minBookY+ bookHeight || minBookX > x || x > minBookX+ bookWidth);
    }
}
