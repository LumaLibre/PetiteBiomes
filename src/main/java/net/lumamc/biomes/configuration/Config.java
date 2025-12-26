package net.lumamc.biomes.configuration;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Material;

import java.util.Map;
import java.util.Set;

@Getter
@Accessors(fluent = true)
public class Config extends OkaeriConfig {

    // TODO: Unused
    private Set<Material> checkAnchorMaterials = Set.of(Material.ANVIL);

    private int anchorBiomeRadius = 4;

    private Map<String, OkaeriLittleBiome> littleBiomes = Map.of("example", new OkaeriLittleBiome()); // TODO: defaults


}
