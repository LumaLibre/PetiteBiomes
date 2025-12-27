package net.lumamc.biomes;

import com.google.common.base.Preconditions;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.standard.StandardSerdes;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.outspending.biomesapi.packet.PacketHandler;
import net.lumamc.biomes.commands.CommandManager;
import net.lumamc.biomes.configuration.Config;
import net.lumamc.biomes.events.BlockListeners;
import net.lumamc.biomes.events.ChunkListeners;
import net.lumamc.biomes.events.BadRegistryPrevention;
import net.lumamc.biomes.model.CachedLittleBiomes;
import net.lumamc.biomes.model.KeyedData;
import net.lumamc.biomes.model.SimpleBlockLocation;
import net.lumamc.biomes.model.WorldTiedChunkLocation;
import net.lumamc.biomes.util.Executors;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Accessors(fluent = true)
public final class LittleBiomes extends JavaPlugin {

    public static final String LITTLE_BIOME_NAMESPACE = "littlebiomes";

    @Getter
    private static LittleBiomes instance;
    @Getter
    private static PacketHandler packetHandler;
    @Getter
    private static Config okaeriConfig;

    @Override
    public void onLoad() {
        instance = this;
        packetHandler = PacketHandler.of(this, PacketHandler.Manipulator.PROTOCOLLIB, PacketHandler.Priority.HIGH);
        okaeriConfig = loadConfig(Config.class, "config.yml");
    }


    @Override
    public void onEnable() {
        packetHandler.register();

        okaeriConfig.littleBiomes().forEach(okaeriLittleBiome -> {
            okaeriLittleBiome.register();
            okaeriLittleBiome.addToPacketHandler();
        });

        getServer().getPluginManager().registerEvents(new BlockListeners(), this);
        getServer().getPluginManager().registerEvents(new ChunkListeners(), this);
        getServer().getPluginManager().registerEvents(new BadRegistryPrevention(), this);
        getCommand("littlebiomes").setExecutor(new CommandManager());

        this.anchorParticlesTask();
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
            it.load(true);
        });
    }



    // TODO: temporarily here
    public void anchorParticlesTask() {
        Executors.runRepeatingAsync(1, TimeUnit.SECONDS, task -> {
            for (WorldTiedChunkLocation worldTiedChunkLocation : CachedLittleBiomes.INSTANCE.getCachedChunks()) {
                Chunk chunk = worldTiedChunkLocation.toBukkitChunk();
                if (!chunk.isLoaded()) {
                    Executors.sync(() -> {
                        CachedLittleBiomes.INSTANCE.uncacheChunk(worldTiedChunkLocation);
                        debug("Uncached chunk at %s in world %s because it was unloaded?".formatted(
                                worldTiedChunkLocation.chunkX() + "," + worldTiedChunkLocation.chunkZ(),
                                worldTiedChunkLocation.world().getName()
                        ));
                    });
                    continue;
                }

                String serializedAnchorLocation = Preconditions.checkNotNull(KeyedData.ANCHOR_BLOCK.get(chunk), "Expected to find anchor block data for chunk (%d, %d) in world %s".formatted(
                        chunk.getX(), chunk.getZ(), chunk.getWorld().getName()
                ));

                SimpleBlockLocation anchorLocation = SimpleBlockLocation.fromSerialized(serializedAnchorLocation, chunk.getWorld());
                Location location = anchorLocation.toLocation().toCenterLocation();
                Particle particle = okaeriConfig.anchorParticle();
                if (particle != null) {
                    location.getWorld().spawnParticle(particle, location, 3, 0.3, 0.3, 0.3, 0.01);
                }
            }
        });
    }


    public static void debug(String message) {
        if (okaeriConfig.debug()) {
            instance.getLogger().info("[DEBUG] " + message);
        }
    }
}