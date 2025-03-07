package us.byeol.katuri.listeners;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import us.byeol.katuri.Katuri;
import us.byeol.katuri.misc.Constants;
import us.byeol.katuri.misc.Misc;

import java.util.Map;
import java.util.WeakHashMap;

public class WandListeners implements Listener {

    private final Map<Player, Pair<Location, Location>> positionMap = new WeakHashMap<>();

    /**
     * Gets the position map of this handler.
     *
     * @return the position map.
     */
    public Map<Player, Pair<Location, Location>> getPositionMap() {
        return this.positionMap;
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR)
            return;
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !Katuri.getInstance().getWand().getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName()))
            return;
        Block block = event.getClickedBlock();
        if (block == null)
            return;
        event.setCancelled(true);
        Pair<Location, Location> pair = positionMap.getOrDefault(player, Pair.of(null, null));
        Location location = block.getLocation();
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            pair.first(location);
            player.sendMessage("§e§b[!] §7You have set position 1 to " + Misc.stringify(location));
        } else {
            pair.second(location);
            player.sendMessage("§e§b[!] §7You have set position 2 to " + Misc.stringify(location));
        }
        if (pair.first() != null && pair.second() != null) {
            int size = Misc.getAreaSize(pair.first(), pair.second());
            if (size > Constants.MAX_BLOCKS)
                player.sendMessage("§e§b[!] §cThis selection is too big. You may select a maximum of §e" + Constants.MAX_BLOCKS + "§7 blocks. [You currently have §e" + size + "§7 selected]");
        }
        positionMap.put(player, pair);
    }

    @EventHandler
    private void onCrouch(PlayerToggleSneakEvent event) {
        if (!event.getPlayer().isSneaking())
            return;
        Katuri.getInstance().getWandGUI().openTo(event.getPlayer());
    }

}