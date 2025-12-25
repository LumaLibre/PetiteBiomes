package net.lumamc.biomes.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public record SimpleBlockLocation(/* transient */ World world, int x, int y, int z) {

    public static SimpleBlockLocation of(World world, int x, int y, int z) {
        return new SimpleBlockLocation(world, x, y, z);
    }

    public Location toLocation() {
        return new Location(world, x, y, z);
    }

    public boolean matchesBlock(Block block) {
        return block.getWorld().getName().equals(world.getName())
                && block.getX() == x
                && block.getY() == y
                && block.getZ() == z;
    }

    public static SimpleBlockLocation fromSerialized(String serialized, World world) {
        String[] parts = serialized.split(",");
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        int z = Integer.parseInt(parts[3]);
        return new SimpleBlockLocation(world, x, y, z);
    }

    public String serialize() {
        return String.format("%d,%d,%d", x, y, z);
    }
}
