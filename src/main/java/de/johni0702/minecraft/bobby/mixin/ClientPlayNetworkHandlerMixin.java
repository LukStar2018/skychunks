package de.johni0702.minecraft.bobby.mixin;

import de.johni0702.minecraft.bobby.SkyblockManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {

    private static long lastLocrawTime = 0;
    private static final long COOLDOWN_MS = 3000;

    @Inject(method = "handleSystemChat", at = @At("HEAD"), cancellable = true)
    private void onSystemChat(ClientboundSystemChatPacket packet, CallbackInfo ci) {
        Component content = packet.content();
        if (content != null) {
            String text = content.getString();

            // Hypixel /locraw liefert JSON. Wir prüfen lockerer, ob "server" oder "gametype" vorkommt.
            if (text.contains("gametype") || text.contains("\"server\":")) {
                // Übergabe an den Manager
                SkyblockManager.handleLocrawResponse(text);

                // Verhindert, dass der Müll im Chat landet
                ci.cancel();
            }
        }
    }

    @Inject(method = "handleRespawn", at = @At("TAIL"))
    private void onWorldChange(CallbackInfo ci) {
        triggerLocraw();
    }

    private static void triggerLocraw() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            ServerData serverData = client.getCurrentServer();
            if (serverData != null && serverData.ip != null && serverData.ip.toLowerCase().contains("hypixel.net")) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastLocrawTime > COOLDOWN_MS) {
                    lastLocrawTime = currentTime;
                    client.player.connection.sendCommand("locraw");
                }
            }
        }
    }
}