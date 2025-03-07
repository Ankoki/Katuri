package us.byeol.katuri.misc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Misc {

    /**
     * Stringifies a location into a readable format.
     *
     * @param location the location to stringify.
     * @return The readable location in the format of 'X, Y, Z'.
     */
    public static String stringify(Location location) {
        return location.getX() + ", " + location.getY() + ", " + location.getZ();
    }

    /**
     * Checks if the given item is a Katuri Wand.
     *
     * @param item the item to check.
     * @return true if it is a katuri wand, else false.
     */
    public static boolean isWand(ItemStack item) {
        return item != null && item.getType() == Material.STICK && item.getItemMeta().getCustomModelData() == 420;
    }

    /**
     * Gets the size of an area between two locations.
     *
     * @param locationOne the first location.
     * @param locationTwo the second location.
     * @return the amount of blocks between them.
     */
    public static int getAreaSize(Location locationOne, Location locationTwo) {
        int i = 0;
        int[] lowest = Misc.getLowestCoords(locationOne, locationTwo);
        int[] highest = Misc.getHighestCoords(locationOne, locationTwo);
        for (int x = lowest[0]; x <= highest[0]; x++)
            for (int y = lowest[1]; y <= highest[1]; y++)
                for (int z = lowest[2]; z <= highest[2]; z++)
                    i++;
        return i;
    }

    /**
     * Gets all the blocks between two locations. Will go off the world of the first location.
     *
     * @param locationOne the first location.
     * @param locationTwo the second location.
     * @return all blocks between each.
     */
    public static List<Block> getBlocks(Location locationOne, Location locationTwo) {
        World world = locationOne.getWorld();
        List<Block> blocks = new ArrayList<>();
        int[] lowest = Misc.getLowestCoords(locationOne, locationTwo);
        int[] highest = Misc.getHighestCoords(locationOne, locationTwo);
        for (int x = lowest[0]; x <= highest[0]; x++)
            for (int y = lowest[1]; y <= highest[1]; y++)
                for (int z = lowest[2]; z <= highest[2]; z++)
                    blocks.add(world.getBlockAt(x, y, z));
        return blocks;
    }

    /**
     * Gets the lowest coordinates, laid out as int[x, y, z].
     *
     * @param locationOne the first location.
     * @param locationTwo the second location.
     * @return the lowest coordinates.
     */
    public static int[] getLowestCoords(Location locationOne, Location locationTwo) {
        int[] lowest = new int[3];
        lowest[0] = Math.min(locationOne.getBlockX(), locationTwo.getBlockX());
        lowest[1] = Math.min(locationOne.getBlockY(), locationTwo.getBlockY());
        lowest[2] = Math.min(locationOne.getBlockZ(), locationTwo.getBlockZ());
        return lowest;
    }

    /**
     * Gets the highest coordinates, laid out as int[x, y, z].
     *
     * @param locationOne the first location.
     * @param locationTwo the second location.
     * @return the highest coordinates.
     */
    public static int[] getHighestCoords(Location locationOne, Location locationTwo) {
        int[] highest = new int[3];
        highest[0] = Math.max(locationOne.getBlockX(), locationTwo.getBlockX());
        highest[1] = Math.max(locationOne.getBlockY(), locationTwo.getBlockY());
        highest[2] = Math.max(locationOne.getBlockZ(), locationTwo.getBlockZ());
        return highest;
    }

}
