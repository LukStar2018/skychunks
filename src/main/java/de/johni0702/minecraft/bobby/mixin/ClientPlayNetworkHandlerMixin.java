package de.johni0702.minecraft.bobby.mixin;

import de.johni0702.minecraft.bobby.SkyblockManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {

    // Wird aufgerufen, wenn der Server eine Chat-/Systemnachricht an den Client sendet
    @Inject(method = "handleSystemChat", at = @At("HEAD"), cancellable = true)
    private void onSystemChat(ClientboundSystemChatPacket packet, CallbackInfo ci) {
        Component content = packet.content();
        if (content != null) {
            String text = content.getString();

            // Prüfen, ob es die JSON-Antwort von Hypixels /locraw ist
            if (text.startsWith("{") && text.contains("gametype")) {
                // An deinen SkyblockManager übergeben
                SkyblockManager.handleLocrawResponse(text);

                // Verhindert, dass die JSON-Nachricht im Chat des Spielers angezeigt wird!
                ci.cancel();
            }
        }
    }

    // Ergänzung im Mixin: Sobald die Verbindung steht / Respawn passiert
    @Inject(method = "handleRespawn", at = @At("TAIL"))
    private void onWorldChange(CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && client.getCurrentServer() != null) {
            if (client.getCurrentServer().ip.contains("hypixel.net")) {
                // Sendet /locraw automatisch im Hintergrund an den Server
                client.player.connection.sendCommand("locraw");
            }
        }
    }
}