package com.falkory.arcanumapi.client.gui.widgets;

import com.falkory.arcanumapi.book.BookMain;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

/**A simple button that has a 16x16 icon and knows which book it's in. */
public abstract class AbstractBookButton extends AbstractWidget {
    protected final BookMain book;
    //todo make into an Icon object
    private final ResourceLocation icon;

    public AbstractBookButton(BookMain book, ResourceLocation icon, int x, int y, int width, int height, Component component) {
        super(x, y, width, height, component);
        this.book = book;
        this.icon = icon;
    }

    /**hopefully {@link AbstractBookButton#icon} will be an actual {@link com.falkory.arcanumapi.book.Icon Icon} by the next push. Best to use this for now.*/
    protected ResourceLocation getIcon(){
        return icon;
    }

    @Override public void onClick(double x, double y) {
        super.onClick(x, y);
        onPress();
    }

    /**@see net.minecraft.client.gui.components.AbstractButton#onPress()*/
    public abstract void onPress();

    @Override public void renderButton(PoseStack stack, int $$1, int $$2, float $$3) {
        if(visible){
            renderBg(stack, Minecraft.getInstance(), $$1, $$2);
            RenderSystem.setShaderColor(1f,1f,1f,1f);
            RenderSystem.setShaderTexture(0, icon);
            blit(stack, x + height-18, y + ((height-16)/2), 1, 0, 0, 16, 16, 16, 16);
        }
    }

    @Override public void updateNarration(NarrationElementOutput narrationElementOutput) {}

    @Override public void playDownSound(SoundManager $$0) {
        $$0.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }

    @Override public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.active && this.visible) {
            if ($$0 != 257 && $$0 != 32 && $$0 != 335) {
                return false;
            } else {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onPress();
                return true;
            }
        } else {
            return false;
        }
    }
}
