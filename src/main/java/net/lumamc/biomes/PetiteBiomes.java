package net.lumamc.biomes;


import com.jeff_media.customblockdata.CustomBlockData;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.outspending.biomesapi.packet.PacketHandler;
import net.lumamc.biomes.configuration.Config;
import org.bukkit.plugin.java.JavaPlugin;

@Accessors(fluent = true)
public final class PetiteBiomes extends JavaPlugin {

    @Getter
    private static PetiteBiomes instance;
    @Getter
    private static PacketHandler packetHandler;
    @Getter
    private static Config okaeriConfig;

    @Override
    public void onLoad() {
        instance = this;
        packetHandler = PacketHandler.of(this, PacketHandler.Priority.HIGH);
    }


    @Override
    public void onEnable() {
        CustomBlockData.registerListener(this);
        packetHandler.register();
    }

    @Override
    public void onDisable() {
        packetHandler.unregister();
    }

    public static void debug(String message) {
        instance.getLogger().info("[DEBUG] " + message);
    }
}