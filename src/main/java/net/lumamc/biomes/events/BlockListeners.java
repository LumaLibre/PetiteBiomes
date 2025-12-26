package net.lumamc.biomes.events;

import com.google.common.base.Preconditions;
import me.outspending.biomesapi.registry.BiomeResourceKey;
import net.lumamc.biomes.model.CachedLittleBiomes;
import net.lumamc.biomes.model.KeyedData;
import net.lumamc.biomes.model.PlacedLittleBiome;
import net.lumamc.biomes.model.SimpleBlockLocation;
import net.lumamc.biomes.model.WorldTiedChunkLocation;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import static net.lumamc.biomes.PetiteBiomes.PETITE_BIOME_NAMESPACE;

public class BlockListeners implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        WorldTiedChunkLocation worldTiedChunkLocation = WorldTiedChunkLocation.of(chunk);
        if (!CachedLittleBiomes.INSTANCE.isChunkCached(worldTiedChunkLocation)) {
            return; // Chunk does not have an anchor in it
        }

        String serializedAnchor = KeyedData.ANCHOR_BLOCK.get(chunk);
        Preconditions.checkNotNull(serializedAnchor, "Expected to find anchor data for little biome in chunk (%d, %d) in world %s".formatted(
                chunk.getX(), chunk.getZ(), chunk.getWorld().getName()
        ));

        SimpleBlockLocation anchorLocation = SimpleBlockLocation.fromSerialized(serializedAnchor, chunk.getWorld());

        if (!anchorLocation.matchesBlock(block)) {
            return; // Only proceed if the broken block is the anchor
        }

        PlacedLittleBiome placedLittleBiome = PlacedLittleBiome.fromChunk(chunk);
        placedLittleBiome.onRemove(block);
        CachedLittleBiomes.INSTANCE.uncacheChunk(worldTiedChunkLocation);

        event.setDropItems(false);
        block.getWorld().dropItemNaturally(block.getLocation(), placedLittleBiome.anchorItemStack());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemStack = event.getItemInHand();

        if (!KeyedData.ANCHOR_BLOCK.matches(itemStack)) {
            return; // Not a little biome anchor
        }

        Block block = event.getBlockPlaced();
        SimpleBlockLocation simpleBlockLocation = SimpleBlockLocation.of(block.getWorld(), block.getX(), block.getY(), block.getZ());

        String biomeName = Preconditions.checkNotNull(KeyedData.ANCHOR_BLOCK.get(itemStack), "Expected biome name in anchor item stack.");
        BiomeResourceKey biomeResourceKey = BiomeResourceKey.of(PETITE_BIOME_NAMESPACE, biomeName);


        PlacedLittleBiome placedLittleBiome = new PlacedLittleBiome(simpleBlockLocation, biomeResourceKey);
        placedLittleBiome.onPlace(block);
        WorldTiedChunkLocation worldTiedChunkLocation = WorldTiedChunkLocation.of(block.getChunk());
        CachedLittleBiomes.INSTANCE.cacheChunk(worldTiedChunkLocation);
    }
}
