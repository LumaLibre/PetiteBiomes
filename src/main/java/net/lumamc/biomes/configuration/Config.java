package net.lumamc.biomes.configuration;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Accessors(fluent = true)
public class Config extends OkaeriConfig {

    private Set<Material> checkAnchorMaterials = Set.of(Material.ANVIL);

    private Map<String, OkaeriLittleBiome> littleBiomes = Map.of("reference", new OkaeriLittleBiome()); // TODO: defaults


}
