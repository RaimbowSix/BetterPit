package com.github.raimbowsix.betterpit.render;

import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.modules.Denicker;
import com.github.raimbowsix.betterpit.util.WatchlistManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static com.github.raimbowsix.betterpit.BetterPit.isInPit;

public class ThreeDESP {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!ConfigOneConfig.threeDESP) return;
        if (ConfigOneConfig.whileInPit && !isInPit()) return;

        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player == mc.thePlayer) continue;

            double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks - mc.getRenderManager().viewerPosX;
            double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks - mc.getRenderManager().viewerPosY;
            double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks - mc.getRenderManager().viewerPosZ;

            double width = player.width / 2 + 0.05;
            double height = player.height + 0.1;
            double depth = player.width / 2 + 0.05;

            float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * event.partialTicks;

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(-yaw, 0, 1, 0);

            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            GL11.glLineWidth(2.0f);
            GL11.glColor4f(1f, 1f, 1f, 1f);
            if (WatchlistManager.isEnemy(player.getName())){
                GL11.glColor4f(1f, 0f, 0f, 1f);
            }
            if (Denicker.isNicked(player.getGameProfile().getId())){
                GL11.glColor4f(0f, 1f, 1f, 1f);
            }

            GL11.glBegin(GL11.GL_LINES);

            // bottom square
            GL11.glVertex3d(-width, 0, -depth);
            GL11.glVertex3d(width, 0, -depth);

            GL11.glVertex3d(width, 0, -depth);
            GL11.glVertex3d(width, 0, depth);

            GL11.glVertex3d(width, 0, depth);
            GL11.glVertex3d(-width, 0, depth);

            GL11.glVertex3d(-width, 0, depth);
            GL11.glVertex3d(-width, 0, -depth);

            // top square
            GL11.glVertex3d(-width, height, -depth);
            GL11.glVertex3d(width, height, -depth);

            GL11.glVertex3d(width, height, -depth);
            GL11.glVertex3d(width, height, depth);

            GL11.glVertex3d(width, height, depth);
            GL11.glVertex3d(-width, height, depth);

            GL11.glVertex3d(-width, height, depth);
            GL11.glVertex3d(-width, height, -depth);

            // vertical lines
            GL11.glVertex3d(-width, 0, -depth);
            GL11.glVertex3d(-width, height, -depth);

            GL11.glVertex3d(width, 0, -depth);
            GL11.glVertex3d(width, height, -depth);

            GL11.glVertex3d(width, 0, depth);
            GL11.glVertex3d(width, height, depth);

            GL11.glVertex3d(-width, 0, depth);
            GL11.glVertex3d(-width, height, depth);

            GL11.glEnd();

            GlStateManager.enableDepth();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}
