package com.falkory.arcanumapi.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class ClientGuiUtils {
    /* positions push from top left, nya
     * texture coordinates for which portion of the texture to read, edges wrap
     * width and height are total pixels allowed to draw in
     * scale is,, interesting,
     * */
    public static void drawDualScaledSprite(PoseStack stack, int x, int y, float texX, float texY, int width, int height, int xscl, int yscl, ResourceLocation texture) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
        GuiComponent.blit(stack, x, y, Math.nextDown(texX), Math.nextDown(texY), width, height, xscl, yscl);
    }
    public static void drawSprite(PoseStack stack, int x, int y, float texX, float texY, int width, int height, ResourceLocation texture){
        drawDualScaledSprite(stack, x, y,texX, texY, width, height, 256, 256, texture);
    }

    public static void drawScaledSprite(PoseStack stack, int x, int y, float texX, float texY, int width, int height, float scale, ResourceLocation texture){
        drawDualScaledSprite(stack, (int)(x*scale), (int)(y*scale), texX, texY, (int)(width*scale), (int)(height*scale), (int)(256*scale), (int)(256*scale), texture);
    }

    public static void drawInplaceScaledSprite(PoseStack stack, int x, int y, float texX, float texY, int width, int height, float scale, ResourceLocation texture){
        drawDualScaledSprite(stack, x, y, texX, texY, (int)(width*scale), (int)(height*scale), (int)(256*scale), (int)(256*scale), texture);
    }
}
