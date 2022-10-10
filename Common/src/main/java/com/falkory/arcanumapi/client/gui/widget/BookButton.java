package com.falkory.arcanumapi.client.gui.widget;

import com.falkory.arcanumapi.book.BookMain;
import com.falkory.arcanumapi.book.Icon;
import com.falkory.arcanumapi.book.IconsHaver;
import com.falkory.arcanumapi.client.gui.ClientGuiUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import java.util.List;

/**A simple button that has a 16x16 icon and knows which book it's in. */
public abstract class BookButton extends AbstractWidget implements IconsHaver {
    protected final BookMain book;
    private final List<Icon> icons;

    protected BookButton(BookMain book, List<Icon> icons, int x, int y, int width, int height, Component component) {
        super(x, y, width, height, component);
        this.book = book;
        this.icons = icons;
    }

    public List<Icon> getIcons(){
        return icons;
    }

    /**@see net.minecraft.client.gui.components.AbstractButton#onClick(double, double) AbstractButton.onClick(double x, double y)*/
    @Override public void onClick(double x, double y) {
        super.onClick(x, y);
        onPress();
    }

    /**@see net.minecraft.client.gui.components.AbstractButton#onPress() AbstractButton.onPress()*/
    public abstract void onPress();

    @Override public void renderButton(PoseStack stack, int $$1, int $$2, float $$3) {
        if(!visible) return;

        renderBg(stack, Minecraft.getInstance(), $$1, $$2);
        ClientGuiUtils.renderIcon(stack, getIcon(), x + height-18, y + ((height-16)/2), getBlitOffset());
        //RenderSystem.setShaderColor(1f,1f,1f,1f);
        //RenderSystem.setShaderTexture(0, getIcon());
        //blit(stack, x + height-18, y + ((height-16)/2), 1, 0, 0, 16, 16, 16, 16);
    }

    @Override public void updateNarration(NarrationElementOutput narrationElementOutput) {}

    @Override public void playDownSound(SoundManager $$0) {
        $$0.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }

    @Override public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (super.keyPressed($$0, $$1, $$2)) return true;
        if (!this.active || !this.visible) return false;
        if ($$0 == 257 || $$0 == 32 || $$0 == 335){ // 257:enter, 32:space, 335: enter but different
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onPress();
            return true;
        }
        return false;
    }
}
