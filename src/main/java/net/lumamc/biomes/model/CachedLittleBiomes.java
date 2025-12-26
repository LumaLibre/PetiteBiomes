package net.lumamc.biomes.model;

import net.lumamc.biomes.PetiteBiomes;

import java.util.HashSet;
import java.util.Set;

public final class CachedLittleBiomes {

    public static CachedLittleBiomes INSTANCE = new CachedLittleBiomes();

    private final Set<WorldTiedChunkLocation> cachedChunkLocations = new HashSet<>();

    public boolean isChunkCached(WorldTiedChunkLocation location) {
        return cachedChunkLocations.contains(location);
    }

    public boolean isWithinRadiusOfCachedChunk(WorldTiedChunkLocation chunk) {
        int radius = PetiteBiomes.okaeriConfig().anchorBiomeRadius();

        for (WorldTiedChunkLocation cachedLocation : cachedChunkLocations) {
            if (!cachedLocation.world().equals(chunk.world())) {
                continue;
            }

            int dx = cachedLocation.chunkX() - chunk.chunkX();
            int dz = cachedLocation.chunkZ() - chunk.chunkZ();

            if (dx * dx + dz * dz <= radius * radius) {
                return true;
            }
        }
        return false;
    }

    public void cacheChunk(WorldTiedChunkLocation location) {
        cachedChunkLocations.add(location);
        PetiteBiomes.debug("Cached new chunk, size: %d".formatted(cachedChunkLocations.size()));
    }

    public void uncacheChunk(WorldTiedChunkLocation location) {
        cachedChunkLocations.remove(location);
        PetiteBiomes.debug("Uncached chunk, size: %d".formatted(cachedChunkLocations.size()));
    }
}
