package com.falkory.arcanumapi.client.gui.widgets;

//modified from net.arcanamod.client.gui.ResearchBookScreen.CategoryButton

import com.falkory.arcanumapi.book.BookMain;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static com.falkory.arcanumapi.ArcanumCommon.AmId;
/**
 * Book buttons that link to a tab in their book.
 *
 * */
public class BookTabButton extends AbstractBookButton {
    ResourceLocation link;
    private static ResourceLocation tab_bg = AmId("textures/arcanumthemes/default/tab.png");
    private int hoverBump = 0;

    public BookTabButton(BookMain book, ResourceLocation link, int x, int y, int width, Component message) {
        super(book, book.getTab(link).icon(), x, y, width, 24, message);
        this.link = link;
    }

    @Override protected void renderBg(PoseStack stack, Minecraft $$1, int $$2, int $$3) {
        if(book.getTabKey() == link) hoverBump = 10;
        else if(hoverBump < 5 && isFocused()) hoverBump++;
        else if (hoverBump > 0 && !isFocused()) hoverBump--;

        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0, tab_bg);
        blit(stack, x-hoverBump, y, 1, 0, 0, Math.min(width+hoverBump, 48), 24, 72, 24);
        for(int drawWidth = 24; (drawWidth += 24) < width+hoverBump;)
          blit(stack, x+drawWidth-hoverBump, y, 1, 48, 0, Math.max(0, width-drawWidth+hoverBump), 24, 80, 80);

    }
    @Override public void renderButton(PoseStack stack, int $$1, int $$2, float $$3) {
        if(visible){
            renderBg(stack, Minecraft.getInstance(), $$1, $$2);
            RenderSystem.setShaderColor(1f,1f,1f,1f);
            RenderSystem.setShaderTexture(0, getIcon());
            blit(stack, x + height-18-hoverBump, y + ((height-16)/2), 1, 0, 0, 16, 16, 16, 16);
        }
    }


    @Override public void onPress() {
        book.setTabKey(link);
    }
}
