package dev.lumas.biomes.commands.subcommand;

import com.google.common.base.Preconditions;
import dev.lumas.biomes.commands.Subcommand;
import dev.lumas.biomes.model.CachedLittleBiomes;
import dev.lumas.biomes.model.KeyedData;
import dev.lumas.biomes.model.SimpleBlockLocation;
import dev.lumas.biomes.model.WorldTiedChunkLocation;
import dev.lumas.biomes.util.Executors;
import dev.lumas.biomes.util.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class NearestAnchorCommand implements Subcommand {
    @Override
    public boolean execute(CommandSender sender, String label, List<String> args) {
        int radius = !args.isEmpty() ? Integer.parseInt(args.getFirst()) : 200;

        Player player = (Player) sender;
        WorldTiedChunkLocation nearest = CachedLittleBiomes.INSTANCE.getCachedChunks().stream()
                .filter(worldTiedChunkLocation -> {
                    if (!Objects.equals(worldTiedChunkLocation.world(), player.getWorld())) return false;
                    double dx = (worldTiedChunkLocation.chunkX() << 4 | 8) + 0.5 - player.getLocation().getX();
                    double dz = (worldTiedChunkLocation.chunkZ() << 4 | 8) + 0.5 - player.getLocation().getZ();
                    return dx * dx + dz * dz < radius * radius;
                })
                .sorted((a, b) -> {
                    double ax = (a.chunkX() << 4 | 8) + 0.5 - player.getLocation().getX();
                    double az = (a.chunkZ() << 4 | 8) + 0.5 - player.getLocation().getZ();
                    double bx = (b.chunkX() << 4 | 8) + 0.5 - player.getLocation().getX();
                    double bz = (b.chunkZ() << 4 | 8) + 0.5 - player.getLocation().getZ();
                    return Double.compare(ax * ax + az * az, bx * bx + bz * bz);
                })
                .limit(1)
                .findFirst()
                .orElse(null);

        if (nearest != null) {
            nearest.toBukkitChunk().thenAccept(chunk -> {
                Executors.sync(chunk, () -> {
                    String serializedAnchorLocation = Preconditions.checkNotNull(KeyedData.ANCHOR_BLOCK.get(chunk), "Expected to find anchor data for little biome in chunk (%d, %d) in world %s".formatted(
                            chunk.getX(), chunk.getZ(), chunk.getWorld().getName()
                    ));

                    SimpleBlockLocation anchorLocation = SimpleBlockLocation.fromSerialized(serializedAnchorLocation, chunk.getWorld());

                    TextUtil.msg(sender, "Nearest anchor found at: <green><click:copy_to_clipboard:'%d %d %d'><hover:show_text:'Click to copy coordinates'>[%d, %d, %d]</hover></click></green>".formatted(
                            anchorLocation.x(), anchorLocation.y(), anchorLocation.z(),
                            anchorLocation.x(), anchorLocation.y(), anchorLocation.z()
                    ));
                });
            });
        } else {
            TextUtil.msg(sender, "No anchors found within a radius of %d blocks.".formatted(radius));
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, List<String> args) {
        return List.of("<radius>");
    }

    @Override
    public Options options() {
        return Options.builder()
                .label("nearestanchor")
                .permission("littlebiomes.command.nearestanchor")
                .playerOnly(true)
                .usage("/<command> nearestanchor <radius>")
                .build();
    }
}
