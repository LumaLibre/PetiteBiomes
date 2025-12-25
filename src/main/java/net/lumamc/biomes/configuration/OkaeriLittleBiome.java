package net.lumamc.biomes.configuration;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.outspending.biomesapi.BiomeSettings;
import me.outspending.biomesapi.biome.BiomeHandler;
import me.outspending.biomesapi.biome.CustomBiome;
import me.outspending.biomesapi.packet.data.BlockReplacement;
import me.outspending.biomesapi.packet.data.PhonyCustomBiome;
import me.outspending.biomesapi.registry.BiomeResourceKey;
import me.outspending.biomesapi.renderer.AmbientParticle;
import me.outspending.biomesapi.renderer.ParticleRenderer;
import net.lumamc.biomes.PetiteBiomes;
import net.lumamc.biomes.model.CachedLittleBiomes;
import net.lumamc.biomes.model.WorldTiedChunkLocation;
import net.lumamc.biomes.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

@Getter
@Accessors(fluent = true)
public class OkaeriLittleBiome extends OkaeriConfig {

    private static final String PETITE_BIOME_NAMESPACE = "petite_biomes";
    public static final NamespacedKey ITEMSTACK_LITTLE_BIOME_KEY = new NamespacedKey(PetiteBiomes.instance(), "little_biome");


    private String name;
    private Material anchorMaterial;

    private String biomeName;
    private String fogColor;
    private String waterColor;
    private String waterFogColor;
    private String skyColor;
    private String foliageColor;
    private String grassColor;
    private Map<AmbientParticle, Float> ambientParticles;
    private Map<Material, Material> blockReplacements;
    private int chunkRadius;


    public BiomeResourceKey biomeResourceKey() {
        return BiomeResourceKey.of(PETITE_BIOME_NAMESPACE, this.biomeName);
    }

    public boolean register() {
        if (BiomeHandler.isBiome(this.biomeResourceKey())) {
            return false;
        }


        CustomBiome customBiome = CustomBiome.builder()
                .resourceKey(this.biomeResourceKey())
                .settings(BiomeSettings.defaultSettings())
                .fogColor(fogColor)
                .foliageColor(foliageColor)
                .skyColor(skyColor)
                .waterColor(waterColor)
                .waterFogColor(waterFogColor)
                .grassColor(grassColor)
                .particleRenderer(new ParticleRenderer(ambientParticles))
                .blockReplacements(
                        blockReplacements.entrySet().stream()
                                .map(entry -> BlockReplacement.of(entry.getKey(), entry.getValue()))
                                .toArray(BlockReplacement[]::new)
                )
                .build();

        customBiome.register();
        PetiteBiomes.debug("Registered custom biome: " + this.biomeResourceKey().toString());
        return true;
    }

    public void addToPacketHandler() {
        // TODO: No way to check if PacketHandler already contains biome?? I'm an idiot???
        PhonyCustomBiome phonyCustomBiome = PhonyCustomBiome.builder()
                .setCustomBiome(BiomeHandler.getBiome(this.biomeResourceKey()))
                .setConditional((player, chunkLocation) -> {
                    WorldTiedChunkLocation worldTiedChunkLocation = WorldTiedChunkLocation.of(player.getWorld(), chunkLocation);
                    PetiteBiomes.debug("Checking biome condition for player " + player.getName() + " at " + worldTiedChunkLocation);
                    return CachedLittleBiomes.INSTANCE.isChunkCached(worldTiedChunkLocation);
                })
                .build();

        PetiteBiomes.packetHandler().appendBiome(phonyCustomBiome);
        PetiteBiomes.debug("Added biome to packet handler: " + this.biomeResourceKey().toString());
    }


    public ItemStack anchorItem() {
        ItemStack itemStack = new ItemStack(this.anchorMaterial);
        itemStack.editMeta(meta -> {
            meta.displayName(TextUtil.minimessage(this.name));
            meta.addEnchant(Enchantment.LURE, 5, true);
            meta.getPersistentDataContainer().set(ITEMSTACK_LITTLE_BIOME_KEY, PersistentDataType.STRING, this.name);
        });
        return itemStack;
    }
}
