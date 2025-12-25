package net.lumamc.biomes.model;

import java.util.HashSet;
import java.util.Set;

public final class CachedLittleBiomes {

    public static CachedLittleBiomes INSTANCE = new CachedLittleBiomes();

    private final Set<WorldTiedChunkLocation> cachedChunkLocations = new HashSet<>();

    public boolean isChunkCached(WorldTiedChunkLocation location) {
        return cachedChunkLocations.contains(location);
    }

    public void cacheChunk(WorldTiedChunkLocation location) {
        cachedChunkLocations.add(location);
    }

    public void uncacheChunk(WorldTiedChunkLocation location) {
        cachedChunkLocations.remove(location);
    }
}
