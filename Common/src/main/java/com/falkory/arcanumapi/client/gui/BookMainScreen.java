package com.falkory.arcanumapi.client.gui;

import com.falkory.arcanumapi.api.ArcanumAPI;
import com.falkory.arcanumapi.book.BookMain;
import com.falkory.arcanumapi.book.BookTab;
import com.falkory.arcanumapi.book.layers.BookLayer;
import com.falkory.arcanumapi.book.layers.ImageLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

import static com.falkory.arcanumapi.ArcanumCommon.AmId;
import static net.minecraft.util.Mth.abs;
import static net.minecraft.util.Mth.clamp;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;

public class BookMainScreen extends AbstractBookScreen{
    ItemStack sender;

    //this is a funny way to make this persistent across instances alsdkjfhn
    //this is awful probably? todo cry about it later
    public static float xPan = 0;
    public static float yPan = 0;
    public static float targetZoom = 1.0f;
    public static float zoom = targetZoom;

    static final float ZOOM_MULTIPLIER = 2.0f;

    public static float maxPanX;
    public static float maxPanY;

    public static int minBookX;
    public static int minBookY;
    public static int bookWidth;
    public static int bookHeight;


    public BookMainScreen(BookMain book, Screen parentScreen, ItemStack item) {
        super(book, parentScreen);
        this.sender = item;
    }

    @Override public void render(PoseStack stack, int $$1, int $$2, float tickDelta) {

        //this used to be in the arcana config, copied default settings todo make user config option
        int frameWidth = width - 60;
        int frameHeight = height - 20;
        int frameSize = Math.max(frameWidth, frameHeight);

        float diff = targetZoom - zoom;
        if (abs(diff) < 0.05f) {targetZoom = zoom;} else {
            float zoomP = (zoom-1)/3;
            float smoothDelta = Math.min(tickDelta * (1 / 3f), 1) * diff;
            float scalar = smoothDelta*(1/zoom)/zoom; // todo so close to focused zoom I can taste it
            xPan -= xPan*scalar;
            yPan -= yPan*scalar;
            zoom += smoothDelta;
            System.out.println("Scalar: "+scalar+" Zoom: "+zoom);
        }
        int zoomDrawSize = (int)(frameSize*zoom);


        // draw stuff
        // todo frame width config
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
        //render(stack, partialTicks, minBookX, minBookY, bookWidth, bookHeight, zoom)
        getBook().getTabs().get(getBook().tabIndex);

        BookLayer mainbg = new ImageLayer("arcana:textures/research/eldritch_bg.png");
        BookLayer glassoverlay = new ImageLayer("minecraft:textures/block/glass.png", 1.1f);

        Map<Number, BookLayer> testLayers = new HashMap<>();
        testLayers.put(1, glassoverlay);
        testLayers.put(0, mainbg);



        BookTab testTab = new BookTab(testLayers, AmId("test_tab"), AmId("textures/items/arcanum.png"), null, "Test Tab", this.getBook() );
        testTab.render(stack, this, frameSize, tickDelta);

        //mainbg.render(stack, this, frameSize, tickDelta);
        //glassoverlay.render(stack, this, frameSize, tickDelta);

        // scissors off
        GL11.glDisable(GL_SCISSOR_TEST);

        if(ArcanumAPI.LOG.isDebugEnabled()) {drawDebug(stack);}

        super.render(stack, $$1, $$2, tickDelta);
    }
    private void drawDebug(PoseStack stack){
        drawString(stack, this.font, "zoom:"+((Float)zoom), 0, 0, 1);
        drawString(stack, this.font, "x:"+xPan+"   y:"+yPan,0,10,1);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
        if(inWindow(mouseX, mouseY)) {
            if(Float.isNaN(xPan + yPan)){xPan=0;yPan=0;}
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
