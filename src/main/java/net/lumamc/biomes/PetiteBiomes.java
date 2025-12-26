package net.lumamc.biomes;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.standard.StandardSerdes;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.outspending.biomesapi.packet.PacketHandler;
import net.lumamc.biomes.configuration.Config;
import net.lumamc.biomes.events.BlockListeners;
import net.lumamc.biomes.events.ChunkListeners;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

@Accessors(fluent = true)
public final class PetiteBiomes extends JavaPlugin {

    public static final String PETITE_BIOME_NAMESPACE = "petitebiomes";

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
        okaeriConfig = loadConfig(Config.class, "config.yml");
    }


    @Override
    public void onEnable() {
        packetHandler.register();

        okaeriConfig.littleBiomes().values().forEach(okaeriLittleBiome -> {
            okaeriLittleBiome.register();
            okaeriLittleBiome.addToPacketHandler();
        });

        getServer().getPluginManager().registerEvents(new BlockListeners(), this);
        getServer().getPluginManager().registerEvents(new ChunkListeners(), this);
    }

    @Override
    public void onDisable() {
        packetHandler.unregister();
    }



    public <T extends OkaeriConfig> T loadConfig(Class<T> configClass, String fileName) {
        Path bindFile = this.getDataPath().resolve(fileName);
        return ConfigManager.create(configClass, it -> {
            it.withConfigurer(new YamlBukkitConfigurer(), new StandardSerdes());
            it.withRemoveOrphans(false);
            it.withBindFile(bindFile);

            it.saveDefaults();
            it.load(false);
        });
    }


    public static void debug(String message) {
        instance.getLogger().info("[DEBUG] " + message);
    }
}