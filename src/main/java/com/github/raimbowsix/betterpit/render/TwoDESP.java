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

public class TwoDESP {

    private static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!ConfigOneConfig.twoDESP) return;
        if (ConfigOneConfig.whileInPit && !isInPit()) return;

        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player == mc.thePlayer) continue;

            double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks - mc.getRenderManager().viewerPosX;
            double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks - mc.getRenderManager().viewerPosY;
            double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks - mc.getRenderManager().viewerPosZ;


            double height = (player.getEntityBoundingBox().maxY - player.getEntityBoundingBox().minY) + 0.1;
            double width = (player.width / 2) + 0.05;

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);

            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

            double zOffset = 0.000;
            GL11.glLineWidth(2.0f);
            //box drawing
            GL11.glColor4f(1f, 1f, 1f, 1f);
            if (WatchlistManager.isEnemy(player.getName())){
                GL11.glColor4f(1f, 0f, 0f, 1f);
                zOffset = 0.002;

            }
            if (Denicker.isNicked(player.getGameProfile().getId())){
                GL11.glColor4f(0f, 1f, 1f, 1f);
                zOffset = 0.001;
            }

            GL11.glBegin(GL11.GL_LINE_LOOP);
            GL11.glVertex3d(-width, 0, zOffset);
            GL11.glVertex3d(width, 0, zOffset);
            GL11.glVertex3d(width, height, zOffset);
            GL11.glVertex3d(-width, height, zOffset);
            GL11.glEnd();

            //health bar
            if (ConfigOneConfig.healthBar) {
                double barWidth = 0.1;
                double barX = width + barWidth + 0.03;
                float health = player.getHealth();
                float maxHealth = player.getMaxHealth();
                float healthPct = Math.max(0f, Math.min(health / maxHealth, 1f));
                double filledHeight = height * healthPct;

                float red = 1.0f - healthPct;
                float green = healthPct;
                float blue = 0.0f;

                GL11.glColor4f(0f,0f,0f,0.6f);

                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex3d(barX, 0, 0);
                GL11.glVertex3d(barX + barWidth, 0, 0);
                GL11.glVertex3d(barX + barWidth, height, 0);
                GL11.glVertex3d(barX, height, 0);
                GL11.glEnd();

                GL11.glColor4f(red, green, blue, 1f);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex3d(barX, 0, 0.002);
                GL11.glVertex3d(barX + barWidth, 0, 0.002);
                GL11.glVertex3d(barX + barWidth, filledHeight, 0.002);
                GL11.glVertex3d(barX, filledHeight, 0.002);
                GL11.glEnd();

                GL11.glColor4f(0, 0, 0, 1f);
                GL11.glBegin(GL11.GL_LINE_LOOP);
                GL11.glVertex3d(barX, 0, zOffset);
                GL11.glVertex3d(barX + barWidth, 0, zOffset);
                GL11.glVertex3d(barX + barWidth, height, zOffset);
                GL11.glVertex3d(barX, height, zOffset);
                GL11.glEnd();

            }

            GlStateManager.enableDepth();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GlStateManager.popMatrix();
        }
    }
}