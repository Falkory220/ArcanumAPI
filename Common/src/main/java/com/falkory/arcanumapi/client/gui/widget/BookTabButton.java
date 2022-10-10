package com.falkory.arcanumapi.client.gui.widget;

//modified from net.arcanamod.client.gui.ResearchBookScreen.CategoryButton

import com.falkory.arcanumapi.api.ArcanumAPI;
import com.falkory.arcanumapi.book.BookMain;
import com.falkory.arcanumapi.client.gui.ClientGuiUtils;
import com.falkory.arcanumapi.client.gui.widget.menu.BookTabList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

/**
 * A button that links to a {@link com.falkory.arcanumapi.book.BookTab BookTab} in its {@link BookMain}.
 * @see BookButton
 * @see BookTabList
 * @see com.falkory.arcanumapi.book.BookTab BookTab
 * */
public class BookTabButton extends BookButton {
    protected ResourceLocation link;
    protected ResourceLocation buttonBg;
    protected static final String TAB_LOC = "textures/arcanumthemes/";
    protected static final String TAB_SUFFIX = "/tab.png";
    private int hoverBump = 0;

    /**
     * @param book The {@link BookMain} this button links to a tab from.
     * @param link The {@link com.falkory.arcanumapi.book.BookTab#key key} of the {@link com.falkory.arcanumapi.book.BookTab BookTab} the button should open.
     * @param x The button's distance from the left of the screen.
     * @param y The button's distance from the top of the screen.
     * @param width The button's width.
     * @param message Narration message. Appears to be unused currently.*/
    public BookTabButton(BookMain book, ResourceLocation link, int x, int y, int width, Component message) {
        super(book, book.getTab(link).getIcons(), x, y, width, 24, message);
        this.link = link;
        buttonBg = new ResourceLocation(book.key().getNamespace(), TAB_LOC+book.key().getPath()+TAB_SUFFIX);
    }

    public ResourceLocation getLink() {
        return link;
    }

    @Override protected void renderBg(@NotNull PoseStack stack, @NotNull Minecraft $$1, int $$2, int $$3) {
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0, buttonBg);
        for(int drawWidth = 24; (drawWidth += 24) < width+hoverBump;) {
            //draw tail first - just in case draw order comes up
            blit(stack, x + drawWidth - hoverBump, y, getBlitOffset()*2, 48, 0, Math.max(0, width - drawWidth + hoverBump), 24, 72, 24);
        }
        //draw tab head
        blit(stack, x-hoverBump, y, getBlitOffset()*2, 0, 0, Math.min(width+hoverBump, 48), 24, 72, 24);
        super.renderBg(stack, $$1, $$2, $$3);
    }
    @Override public void renderButton(PoseStack stack, int $$1, int $$2, float $$3) {
        if(!visible) return;

        if(book.getTabKey() == link) {hoverBump = 10;}
        else if(hoverBump < 5 && isFocused()) {hoverBump++;}
        else if(hoverBump > 0 && !isFocused()) {hoverBump--;}

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        renderBg(stack, Minecraft.getInstance(), $$1, $$2);
        //renders our icon. *2 gives us enough room for a bed to render happily with no (visible) clipping.
        ClientGuiUtils.renderIcon(stack, getIcon(), x+height-18-hoverBump, y + ((height-16)/2), getBlitOffset()*2);
    }

    @Override public void onPress() {
        book.setTabKey(link);
    }

    @Override protected void onFocusedChanged(boolean $$0) {
        ArcanumAPI.LOG.debug("Tab button "+link+" selected: "+$$0);
        super.onFocusedChanged($$0);
    }

    @Override public void updateNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.POSITION, this.book.getTab(link).name());
        super.updateNarration(narrationElementOutput);
    }
}
