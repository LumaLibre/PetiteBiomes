package net.lumamc.biomes.events;

import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import net.lumamc.biomes.model.CachedLittleBiomes;
import net.lumamc.biomes.model.PlacedLittleBiome;
import net.lumamc.biomes.model.WorldTiedChunkLocation;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.concurrent.CompletableFuture;

public class ChunkListeners implements Listener {

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        CompletableFuture.runAsync(() -> {
            if (!this.isLittleBiomeChunk(chunk)) {
                return;
            }

            WorldTiedChunkLocation worldTiedChunkLocation = WorldTiedChunkLocation.of(chunk);
            CachedLittleBiomes.INSTANCE.cacheChunk(worldTiedChunkLocation);
        });
    }

    @EventHandler
    public void onChunkUnloadEvent(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();

        CompletableFuture.runAsync(() -> {
            if (!this.isLittleBiomeChunk(chunk)) {
                return;
            }

            WorldTiedChunkLocation worldTiedChunkLocation = WorldTiedChunkLocation.of(chunk);
            CachedLittleBiomes.INSTANCE.uncacheChunk(worldTiedChunkLocation);
        });
    }


    public boolean isLittleBiomeChunk(Chunk chunk) {
        PersistentDataContainer chunkDataContainer = chunk.getPersistentDataContainer();
        return chunkDataContainer.has(PlacedLittleBiome.CHUNK_LITTLE_BIOME_KEY);
    }
}
