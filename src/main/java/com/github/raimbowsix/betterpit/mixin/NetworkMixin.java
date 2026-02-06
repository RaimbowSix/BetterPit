package com.github.raimbowsix.betterpit.mixin;

import com.github.raimbowsix.betterpit.BetterPit;
import com.github.raimbowsix.betterpit.config.ConfigOneConfig;
import com.github.raimbowsix.betterpit.modules.AutoPantSwap;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class NetworkMixin {
    @Shadow private INetHandler packetListener;

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void onChannelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
    };
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        if (ConfigOneConfig.invPackets&&(packet instanceof C0DPacketCloseWindow || packet instanceof C0EPacketClickWindow || packet instanceof C16PacketClientStatus)){
            BetterPit.sendMessage(packet.toString());
            System.out.println(packet);
        }
        if (packet instanceof C01PacketChatMessage){
            String message = ((C01PacketChatMessage) packet).getMessage();
            if (message.startsWith("/spawn")){
                AutoPantSwap.alreadyDidPod=false;
            }
        }
    }
}