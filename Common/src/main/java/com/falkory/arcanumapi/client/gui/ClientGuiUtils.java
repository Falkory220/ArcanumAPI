package com.falkory.arcanumapi.client.gui;

//modified from net.arcanamod.client.gui.ClientUiUtil;
import com.falkory.arcanumapi.book.Icon;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static net.minecraft.client.gui.GuiComponent.blit;

public class ClientGuiUtils {
    /* positions push from top left, nya
     * texture coordinates for which portion of the texture to read, edges wrap
     * width and height are total pixels allowed to draw in
     * scale is,, interesting,
     * */
    public static void drawDualScaledSprite(PoseStack stack, int x, int y, float texX, float texY, int width, int height, int xscl, int yscl, ResourceLocation texture) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
        blit(stack, x, y, Math.nextDown(texX), Math.nextDown(texY), width, height, xscl, yscl);
    }

    public static void drawDualScaledSprite(PoseStack stack, int x, int y, int z, float texX, float texY, int width, int height, int xscl, int yscl, ResourceLocation texture) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);
        blit(stack, x, y, z, Math.nextDown(texX), Math.nextDown(texY), width, height, xscl, yscl);
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

    /**
     * Needs a texture pre-bound.
     * Draws the current bound texture only from a specified UV+width and stretches it to fit the xy+width space specified.
     * @param x draw box start position distance from the left.
     * @param y draw box start position distance from the top.
     * @param width draw width, movement rightward from x.
     * @param height draw height, movement downward from y.
     * @param z GL depth test value. Bump if you're not drawing in front of something you need to
     * @param u texture read box start position, distance from the left.
     * @param v texture read box start position, distance from the top.
     * @param du texture read box width, distance read rightward from u.
     * @param dv texture read box height, distance read downward from v.
     * */
    public static void drawStreched(PoseStack stack, int x, int y, int width, int height, int z, int u, int v, int du, int dv) {
        final float uScale = 1f/ 0x100; //sets our scale
        final float vScale = 1f/ 0x100;

        Tesselator tessa = Tesselator.getInstance();
        BufferBuilder nya = tessa.getBuilder();
        nya.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f pose = stack.last().pose();

        nya.vertex(pose, x        , y + height, z).uv( u      *uScale, ((v + dv)*vScale)).endVertex();
        nya.vertex(pose, x + width, y + height, z).uv((u + du)*uScale, ((v + dv)*vScale)).endVertex();
        nya.vertex(pose, x + width, y         , z).uv((u + du)*uScale, ( v      *vScale)).endVertex();
        nya.vertex(pose, x        , y         , z).uv( u      *uScale, ( v      *vScale)).endVertex();
        tessa.end();
    }

    public static void renderIcon(PoseStack stack, Icon icon, int x, int y, int z){
        // first, check if its an item
        if(icon.getStack() != null && !icon.getStack().isEmpty()){
            // this, uhh, doesn't work
            // rendered items take up multiple blit levels, so we adjust for it
            Minecraft.getInstance().getItemRenderer().blitOffset = z - 96;
            Minecraft.getInstance().getItemRenderer().renderGuiItem(icon.getStack(), x, y);//renderAndDecorateItem(icon.getStack(), x, y);
        }else{
            // otherwise, check for a texture
            RenderSystem.setShaderTexture(0, icon.getResourceLocation());
            RenderSystem.enableDepthTest();
            blit(stack, x, y, z, 0, 0, 16, 16, 16, 16);
        }
    }
}
