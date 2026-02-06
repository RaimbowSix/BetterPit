package com.github.raimbowsix.betterpit.render;

import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.modules.Denicker;
import com.github.raimbowsix.betterpit.util.CacheManager;
import com.github.raimbowsix.betterpit.util.WatchlistManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static com.github.raimbowsix.betterpit.BetterPit.isInPit;

public class NametagRenderer {

    @SubscribeEvent
    public void onRenderNametag(RenderLivingEvent.Specials.Pre event) {
        if (!(event.entity instanceof AbstractClientPlayer)) return;
        if (ConfigOneConfig.whileInPit && !isInPit()) return;
        AbstractClientPlayer player = (AbstractClientPlayer) event.entity;
        String nick = player.getName();
        if (!Denicker.lastNickedSet.contains(nick)) return;
        String realName = CacheManager.getFromCache(nick);
        if (realName == null || realName.equalsIgnoreCase(nick)) return;
        if (!ConfigOneConfig.customFont) return;
        event.setCanceled(true);
        renderNametag(player, "§e"+realName + " §f(§b" + nick + "§f)", event.x, event.y, event.z, WatchlistManager.isEnemy(realName));
    }

    private void renderNametag(EntityLivingBase entity, String name, double x, double y, double z, boolean isEnemy) {
        double distanceSq = entity.getDistanceSqToEntity(Minecraft.getMinecraft().thePlayer);
        if (distanceSq > 4096.0D) return;

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        float scale = 0.02666667F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + entity.height + 0.5F, z);
        GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();

        int textWidth = fontRenderer.getStringWidth(name);
        int textHeight = fontRenderer.FONT_HEIGHT;

        int padding = 2;
        int x1 = -textWidth / 2 - padding;
        int y1 = -padding;
        int x2 = textWidth / 2 + padding;
        int y2 = textHeight + padding;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA,
                1,0
        );
        DrawingMethods.drawRect(x1 , y1, x2, y2, 0x80000000);

        int outlineColor = 0xFFFFFFFF;
        if (isEnemy){
            outlineColor = 0xFFFF0000;
        }
        int thickness = 1;

        DrawingMethods.drawRect(x1, y1, x2, y1 + thickness, outlineColor);
        DrawingMethods.drawRect(x1, y2 - thickness, x2, y2, outlineColor);
        DrawingMethods.drawRect(x1, y1, x1 + thickness, y2, outlineColor);
        DrawingMethods.drawRect(x2 - thickness, y1, x2, y2, outlineColor);

        GlStateManager.enableTexture2D();
        fontRenderer.drawStringWithShadow(name, (float) -textWidth / 2, 0, 0xFFFFFF);

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
