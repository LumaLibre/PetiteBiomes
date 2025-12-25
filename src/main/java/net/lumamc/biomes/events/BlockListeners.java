package net.lumamc.biomes.events;

import com.google.common.base.Preconditions;
import net.lumamc.biomes.model.CachedLittleBiomes;
import net.lumamc.biomes.model.PlacedLittleBiome;
import net.lumamc.biomes.model.SimpleBlockLocation;
import net.lumamc.biomes.model.WorldTiedChunkLocation;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataType;

public class BlockListeners implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        WorldTiedChunkLocation worldTiedChunkLocation = WorldTiedChunkLocation.of(chunk);
        if (!CachedLittleBiomes.INSTANCE.isChunkCached(worldTiedChunkLocation)) {
            return;
        }

        String serializedAnchor = chunk.getPersistentDataContainer().get(PlacedLittleBiome.BLOCK_ANCHOR_KEY, PersistentDataType.STRING);
        Preconditions.checkNotNull(serializedAnchor, "Expected to find anchor data for little biome in chunk (%d, %d) in world %s".formatted(
                chunk.getX(), chunk.getZ(), chunk.getWorld().getName()
        ));

        SimpleBlockLocation anchorLocation = SimpleBlockLocation.fromSerialized(serializedAnchor, chunk.getWorld());

        if (anchorLocation.matchesBlock(block)) {

        }
    }
}
