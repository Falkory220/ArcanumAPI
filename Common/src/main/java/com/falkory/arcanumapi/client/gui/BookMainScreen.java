package com.falkory.arcanumapi.client.gui;

//modified from net.arcanamod.client.gui.ResearchBookScreen

import com.falkory.arcanumapi.book.BookMain;
import com.falkory.arcanumapi.book.BookTab;
import com.falkory.arcanumapi.client.gui.widgets.AbstractBookButton;
import com.falkory.arcanumapi.client.gui.widgets.BookTabButton;
import com.falkory.arcanumapi.client.gui.widgets.LayeredWidgetHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.Mth.abs;
import static net.minecraft.util.Mth.clamp;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;

public class BookMainScreen extends AbstractBookScreen{
    ItemStack sender;

    //this is a funny way to make this persistent across instances alsdkjfhn
    //this is awful probably? todo cry about it later
    private static float xPan = 0;
    private static float yPan = 0;
    public static float targetZoom = 1.0f;
    public static float zoom = targetZoom;

    static final float ZOOM_MULTIPLIER = 2.0f;

    public static float maxPanX;
    public static float maxPanY;

    public static int minBookX;
    public static int minBookY;
    public static int bookWidth;
    public static int bookHeight;

    private static int frameSize;

    private static boolean dragOnCanvas;

    private final List<AbstractBookButton> tabButtons = new ArrayList<>();

    public BookMainScreen(BookMain book, Screen parentScreen, ItemStack item) {
        super(book, parentScreen);
        this.sender = item;
    }

    @Override public void render(PoseStack stack, int $$1, int $$2, float tickDelta) {
        //should tick this on render to avoid jumping at low framerates, nya
        smoothZoom(tickDelta);

        // draw stuff
        int scale = (int)this.minecraft.getWindow().getGuiScale();
        GL11.glScissor(minBookX * scale, minBookY * scale, bookWidth * scale, bookHeight * scale);
        // scissors on
        GL11.glEnable(GL_SCISSOR_TEST);

        getBook().getCurrentTab().render(stack, this, frameSize, tickDelta);

        // scissors off
        GL11.glDisable(GL_SCISSOR_TEST);

        if(debug) {drawDebug(stack);}

        super.render(stack, $$1, $$2, tickDelta);
    }

    protected void smoothZoom(float tickDelta){
        float diff = targetZoom - zoom;
        if (diff == 0){return;}
        if (abs(diff) < 0.05f) {targetZoom = zoom;} else {
            float smoothDelta = Math.min(tickDelta * (2 / 3f), 1) * diff;
            float scalar = smoothDelta*(1/zoom)/zoom; // todo so close to focused zoom I can taste it
            setXPan(xPan - xPan*scalar);
            setYPan(yPan - yPan*scalar);
            zoom += smoothDelta;
        }
        recalcRender();
    }

    protected void drawDebug(PoseStack stack){
        drawString(stack, this.font, "zoom:"+((Float)zoom), 0, 0, 1);
        drawString(stack, this.font, "x:"+xPan+"   y:"+yPan,0,10,1);
        drawString(stack, this.font, "xMax:"+maxPanX+"   yMax:"+maxPanY,0,20,1);
    }

    public void setXPan(float newPan){
        if (Float.isNaN(newPan)) return;
        xPan = clamp(newPan, -1f, 1f);
    }
    public float getXPan(){return xPan;}
    public void setYPan(float newPan){
        if(Float.isNaN(newPan)) return;
        yPan = clamp(newPan, -1f, 1f);
    }
    public float getYPan(){return yPan;}

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        dragOnCanvas = inWindow(mouseX, mouseY);
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragOnCanvas = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }


    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
        if(dragOnCanvas) {
            if(Float.isNaN(xPan + yPan)){xPan=0;yPan=0;}
            setXPan((float)(xPan - (deltaX * ZOOM_MULTIPLIER) / (zoom * maxPanX)));
            setYPan((float)(yPan - (deltaY * ZOOM_MULTIPLIER) / (zoom * maxPanY)));
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
        } else {
            getBook().incrementTab((int) -scroll);
        }
        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    private boolean inWindow(double x, double y){
        return !(minBookY > y || y > minBookY+ bookHeight || minBookX > x || x > minBookX+ bookWidth);
    }


    private void recalcFrame(){
        int scale = (int)this.minecraft.getWindow().getGuiScale();
        //this used to be in the arcana config, copied default settings todo make user config option
        int frameWidth = width - 60;
        int frameHeight = height - 20;
        int wiggleRoom = 200;

        minBookX = (width - frameWidth + 32) / 2;
        minBookY = (height - frameHeight + 34) / 2;
        bookWidth = frameWidth - 32;
        bookHeight = frameHeight - 34;

        frameSize = Math.max(bookWidth, bookHeight);
    }

    private void recalcRender(){
        int zoomDrawSize = (int)(frameSize*zoom);

        maxPanX = (zoomDrawSize- bookWidth)/2f;
        maxPanY = (zoomDrawSize- bookHeight)/2f;
    }

    private void initTabButtons(){
        tabButtons.clear();
        int scale = 1;//(int)this.minecraft.getWindow().getGuiScale();
        //Tabs are x24 textures
        final int buttonHeight = 24*scale;
        final int buttonWidth = 24*scale;
        final int xSpace = 18*scale;
        final int ySpace = 26*scale;

        //first we figure out how many buttons we need to fit in each column
        final List<BookTab> tabs = getBook().getTabs();
        final int columns = (int) Math.ceil((tabs.size()*ySpace + buttonHeight)/(float)bookHeight);
        if(columns > 2){
            int columnEated = (int) Math.ceil((columns-2)*xSpace);
            bookWidth -= columnEated; minBookX += columnEated;
        }

        LayeredWidgetHolder tabsHolder = new LayeredWidgetHolder();
        addRenderableWidget(tabsHolder);

        int tabOffset = 0;
        int left = 0;
        for (BookTab tab : tabs){
            BookTabButton tabLink = new BookTabButton(getBook(), tab.key(),
              minBookX + (left * -xSpace) - buttonWidth,
              minBookY + (tabOffset),
              buttonWidth + (left * xSpace), Component.empty());
            tabButtons.add(tabLink);
            tabsHolder.addLayeredWidget(tabLink);
            //make our selected tab stay selected
            if(tab.key() == getBook().getTabKey()) tabsHolder.select(tabLink);

            tabOffset += ySpace;
            if(tabOffset + ySpace > bookHeight ) {
                left += 1;
                tabOffset = (int)(buttonHeight/columns)*(left % columns);
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        recalcFrame();
        initTabButtons();
        recalcRender();
    }
}
