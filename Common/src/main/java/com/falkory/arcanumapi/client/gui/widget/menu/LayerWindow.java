package com.falkory.arcanumapi.client.gui.widget.menu;

import com.falkory.arcanumapi.book.BookMain;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;

import static net.minecraft.util.Mth.abs;
import static net.minecraft.util.Mth.clamp;

public class LayerWindow extends Subscreen{
    BookMain book;


    //this is a funny way to make this persistent across instances alsdkjfhn
    //this is awful probably? todo cry about it later
    private static float xPan = 0;
    private static float yPan = 0;
    public static float targetZoom = 1.1f;
    public static float zoom = targetZoom;

    static final float ZOOM_MULTIPLIER = 2.0f;

    public static float maxPanX;
    public static float maxPanY;

    private static int frameSize;

    private static boolean dragOnCanvas;

    public LayerWindow(BookMain book) {
        super(Component.empty());
        this.book = book;
    }

    public BookMain getBook() {
        return book;
    }

    @Override public void render(PoseStack stack, int $$1, int $$2, float tickDelta) {
        //tick this on render to avoid jumping at low framerates, nya
        smoothZoom(tickDelta);

        // draw stuff
        int scale = 4;
        if(this.minecraft != null) scale = (int)this.minecraft.getWindow().getGuiScale();


        /* // Scissors unneeded? might change once we get to nodes, but keeping them off for now
         * GL11.glScissor(x * scale, y * scale, width * scale, height * scale);
         * GL11.glEnable(GL_SCISSOR_TEST);
         */
        RenderSystem.enableBlend();
        book.getCurrentTab().render(stack, this, frameSize, tickDelta);

        // scissors off
        // GL11.glDisable(GL_SCISSOR_TEST);

        super.render(stack, $$1, $$2, tickDelta);
    }

    protected void smoothZoom(float tickDelta){
        float diff = targetZoom - zoom;
        if (diff == 0) return;
        if (abs(diff) < 0.05f) {targetZoom = zoom;} else {
            float smoothDelta = Math.min(tickDelta * (2 / 3f), 1) * diff;
            float scalar = smoothDelta*(1/zoom)/zoom; // todo so close to focused zoom I can taste it
            setXPan(xPan - xPan*scalar);
            setYPan(yPan - yPan*scalar);
            zoom += smoothDelta;
        }
        recalcRender();
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

    @Override public boolean mouseClicked(double mx, double my, int button) {
        dragOnCanvas = isMouseOver(mx, my);
        return super.mouseClicked(mx, my, button);
    }
    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragOnCanvas = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }


    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
        if(!dragOnCanvas) return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        if(Float.isNaN(xPan + yPan)){xPan=0;yPan=0;}
        setXPan((float)(xPan - (deltaX * ZOOM_MULTIPLIER) / (zoom * maxPanX)));
        setYPan((float)(yPan - (deltaY * ZOOM_MULTIPLIER) / (zoom * maxPanY)));

        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return true;
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double scroll)  {
        if (isMouseOver(mouseX, mouseY)) {
            float amount = 1.2f;
            if ((scroll < 0 && targetZoom > 1) || (scroll > 0 && targetZoom < 4))
                targetZoom *= scroll > 0 ? amount : 1 / amount;
            targetZoom = clamp(targetZoom, 1f, 4f);
        }else{
            //if outside window, don't !
            getBook().incrementTab((int) -scroll);
        }
        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override protected void init() {
        recalcFrame();
        super.init();
        recalcRender(); // super's init could change our size... actually shouldn't that be a setter handling thing?
    }

    private void recalcFrame(){
        int scale = (int)this.minecraft.getWindow().getGuiScale();
        //this used to be in the arcana config, copied default settings todo make user config option
        int frameWidth = minecraft.screen.width - 60;
        int frameHeight = minecraft.screen.height - 20;
        int wiggleRoom = 200;

        x = (minecraft.screen.width - frameWidth + 32) / 2;
        y = (minecraft.screen.height - frameHeight + 34) / 2;
        width = frameWidth - 32;
        height = frameHeight - 34;

        frameSize = Math.max(width, height);
    }

    private void recalcRender(){
        int zoomDrawSize = (int)(frameSize*zoom);

        maxPanX = (zoomDrawSize- width)/2f;
        maxPanY = (zoomDrawSize- height)/2f;
    }
}
