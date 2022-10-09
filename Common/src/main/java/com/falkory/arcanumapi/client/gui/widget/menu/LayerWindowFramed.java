package com.falkory.arcanumapi.client.gui.widget.menu;

import com.falkory.arcanumapi.book.BookMain;
import com.falkory.arcanumapi.client.gui.ClientGuiUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class LayerWindowFramed extends LayerWindow{
    private final ResourceLocation frameTexture;
    protected static final String SUFFIX_FRAME = "/frame.png";
    protected static final String PREFIX_FRAME = "textures/arcanumthemes/";
    protected int bump;

    public LayerWindowFramed(BookMain book) {
        super(book);
        frameTexture = new ResourceLocation(getBook().key().getNamespace(), PREFIX_FRAME + getBook().key().getPath() + SUFFIX_FRAME);
    }

    @Override public void render(PoseStack stack, int $$1, int $$2, float tickDelta) {
        super.render(stack, $$1, $$2, tickDelta);
        RenderSystem.enableBlend();
        if(minecraft == null) return; //should it be here? probably, honestly. had an issue with the other one.
        final int textureSize = 256;
        final int frameSize = 69;
        final int frameLength = 2;
        final int boxWidth = frameSize*2 + frameLength;
        bump = 5;


        int cornerL = x-bump;
        int cornerR = x+width-frameSize+bump;
        int cornerU = y-bump;
        int cornerD = y+height-frameSize+bump;

        RenderSystem.setShaderTexture(0, frameTexture);
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        setBlitOffset(2);
        // draw edges, order: U, D, L, R
        ClientGuiUtils.drawStreched(stack, cornerL+frameSize, cornerU, width-(frameSize-bump)*2, frameSize, getBlitOffset(), frameSize, 0,                     frameLength, frameSize);
        ClientGuiUtils.drawStreched(stack, cornerL+frameSize, cornerD, width-(frameSize-bump)*2, frameSize, getBlitOffset(), frameSize, frameSize+frameLength, frameLength, frameSize);
        ClientGuiUtils.drawStreched(stack, cornerL, cornerU+frameSize, frameSize, height-(frameSize-bump)*2, getBlitOffset(), 0, frameSize,                     frameSize, frameLength);
        ClientGuiUtils.drawStreched(stack, cornerR, cornerU+frameSize, frameSize, height-(frameSize-bump)*2, getBlitOffset(), frameSize+frameLength, frameSize, frameSize, frameLength);

        // draw corners, order: LU, RU, LD, RD
        // blit function takes (pos x, pos y, render z, texture u, texture v, draw width, draw height, texture size, texture size)
        blit(stack, cornerL, cornerU, getBlitOffset(), 0, 0,                                         frameSize, frameSize, textureSize, textureSize);
        blit(stack, cornerR, cornerU, getBlitOffset(), frameSize+frameLength, 0,                     frameSize, frameSize, textureSize, textureSize);
        blit(stack, cornerL, cornerD, getBlitOffset(), 0, frameSize+frameLength,                     frameSize, frameSize, textureSize, textureSize);
        blit(stack, cornerR, cornerD, getBlitOffset(), frameSize+frameLength, frameSize+frameLength, frameSize, frameSize, textureSize, textureSize);

        // draw decor, order: U, D, L, R
        // blit function takes (pos x, pos y, render z, texture u, texture v, draw width, draw height, texture size, texture size)
        final int decoWidth = 58;
        blit(stack, x+(width -boxWidth)/2, cornerU, getBlitOffset(), 0, boxWidth, boxWidth, decoWidth, textureSize, textureSize);
        blit(stack, x+(width -boxWidth)/2, cornerD+(frameSize-decoWidth), getBlitOffset(), 0, boxWidth+decoWidth, boxWidth, decoWidth, textureSize, textureSize);
        blit(stack, cornerL, y+(height-boxWidth)/2, getBlitOffset(), boxWidth, 0, decoWidth, boxWidth, textureSize, textureSize);
        blit(stack, cornerR+(frameSize-(decoWidth)), y+(height-boxWidth)/2, getBlitOffset(), boxWidth+decoWidth, 0, decoWidth, boxWidth, textureSize, textureSize);
    }

}
