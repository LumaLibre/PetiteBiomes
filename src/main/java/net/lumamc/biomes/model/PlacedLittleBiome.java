package net.lumamc.biomes.model;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.outspending.biomesapi.registry.BiomeResourceKey;
import net.lumamc.biomes.PetiteBiomes;
import net.lumamc.biomes.configuration.OkaeriLittleBiome;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@Getter
@Builder
@Accessors(fluent = true)
public class PlacedLittleBiome {

    public static final NamespacedKey BLOCK_ANCHOR_KEY = new NamespacedKey(PetiteBiomes.instance(), "anchor");
    public static final NamespacedKey CHUNK_LITTLE_BIOME_KEY = new NamespacedKey(PetiteBiomes.instance(), "chunk_little_biome");

    private final SimpleBlockLocation anchor;
    private final BiomeResourceKey biomeKey;
    private final int chunkRadius;


    public PlacedLittleBiome(SimpleBlockLocation anchor, BiomeResourceKey biomeKey, int chunkRadius) {
        this.anchor = anchor;
        this.biomeKey = biomeKey;
        this.chunkRadius = chunkRadius;
    }

    public String name() {
        return biomeKey.key().value();
    }

    public void onPlace(Block block) {
        Chunk chunk = block.getChunk();
        SimpleBlockLocation simpleBlockLocation = SimpleBlockLocation.of(chunk.getWorld(), block.getX(), block.getY(), block.getZ());

        PersistentDataContainer chunkData = chunk.getPersistentDataContainer();

        Preconditions.checkState(!chunkData.has(CHUNK_LITTLE_BIOME_KEY, PersistentDataType.STRING),
                "Chunk at (%d, %d) in world %s already has a little biome assigned.".formatted(
                        chunk.getX(), chunk.getZ(), chunk.getWorld().getName()
                ));

        chunkData.set(BLOCK_ANCHOR_KEY, PersistentDataType.STRING, simpleBlockLocation.serialize());
        chunkData.set(CHUNK_LITTLE_BIOME_KEY, PersistentDataType.STRING, this.biomeKey.toString());

        PetiteBiomes.debug("Placed little biome %s at chunk (%d, %d) in world %s with anchor at %s".formatted(
                this.biomeKey,
                chunk.getX(),
                chunk.getZ(),
                chunk.getWorld().getName(),
                simpleBlockLocation.serialize()
        ));
    }


    public void onRemove(Block block) {
        Chunk chunk = block.getChunk();
        PersistentDataContainer chunkData = chunk.getPersistentDataContainer();

        chunkData.remove(BLOCK_ANCHOR_KEY);
        chunkData.remove(CHUNK_LITTLE_BIOME_KEY);

        PetiteBiomes.debug("Removed little biome from chunk (%d, %d) in world %s".formatted(
                chunk.getX(),
                chunk.getZ(),
                chunk.getWorld().getName()
        ));
    }

    public ItemStack anchorItemStack() {
        OkaeriLittleBiome littleBiomeConfig = PetiteBiomes.okaeriConfig().littleBiomes().get(this.name());
        Preconditions.checkNotNull(littleBiomeConfig, "No little biome config found for biome key: " + this.biomeKey);

        return littleBiomeConfig.anchorItem();
    }
}
