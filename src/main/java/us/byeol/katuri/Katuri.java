package us.byeol.katuri;

import com.ankoki.roku.bukkit.BukkitImpl;
import com.ankoki.roku.bukkit.guis.GUI;
import com.ankoki.roku.bukkit.misc.ItemUtils;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import us.byeol.katuri.listeners.WandListeners;
import us.byeol.katuri.misc.Constants;
import us.byeol.katuri.misc.Misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Katuri extends JavaPlugin {

    private static Katuri instance;

    /**
     * Gets the current instance of Katuri.
     *
     * @return the current instance.
     */
    public static Katuri getInstance() {
        return instance;
    }

    private WandListeners wandHandler;
    private ItemStack wand;
    private GUI wandGUI;

    @Override
    public void onEnable() {
        instance = this;
        BukkitImpl.setupRoku(this);
        this.declareRecipe();
        this.wandHandler = new WandListeners();
        this.getServer().getPluginManager().registerEvents(wandHandler, this);
    }

    /**
     * Declares the recipe for the Katuri wand.
     */
    private void declareRecipe() {
        NamespacedKey key = new NamespacedKey(this, "KaturiWand");
        wand = ItemStack.of(Material.STICK);
        wand.editMeta(meta -> {
            meta.setDisplayName("§e§bKaturi Wand");
            meta.setLore(List.of("§7§oThis is a wand to",
                    "§7§oenchance the survival experience.",
                    "§7§o",
                    "§7§oLeft click to set position 1.",
                    "§7§oRight click to set position 2.",
                    "§7§oCrouch to open the wand menu."));
            meta.setUnbreakable(true);
            meta.setEnchantmentGlintOverride(true);
            meta.setCustomModelData(420);
        });
        ShapedRecipe recipe = new ShapedRecipe(key, wand);
        recipe.shape(" G ",
                " S ",
                " S ");
        recipe.setIngredient('G', Material.GOLD_NUGGET);
        recipe.setIngredient('S', Material.STICK);
        this.getServer().addRecipe(recipe);
        this.declareGUIs();
    }

    /**
     * Declares all the Roku GUIs to be used.
     */
    private void declareGUIs() {
        this.wandGUI = new GUI("§e§bKaturi", 27)
                .setShape("xxxxxxxxx",
                        "xxx x xxx",
                        "xxxxxxxxx")
                .setShapeItem('x', ItemUtils.getBlank(Material.LIGHT_BLUE_STAINED_GLASS_PANE))
                .setSlot(12, ItemUtils.from("§e§oBreak", Material.NETHER_WART, "§7§oClick to open the", "§7§obreaking menu. You should put the tools", "§7§oyou would like to break this area", "§7§owith in the following GUI, and", "§7§othen close it. The process will begin", "§7§oautomatically."))
                .setSlot(14, ItemUtils.from("§e§oBuild", Material.PUFFERFISH, "§7§oClick to open the", "§7§obuilding menu. You should put the blocks", "§7§oyou would like to place in this", "§7§oarea in the following GUI, and", "§7§othen close it. The process will begin", "§7§oautomatically."))
                .setOwnClickable(false)
                .addClickEvent(event -> {
                    event.setCancelled(true);
                    Player player = (Player) event.getWhoClicked();
                    GUI breakMenu = new GUI("§c§oKaturi", 64)
                            .setOwnClickable(true)
                            .setCloseEvent(closeEvent -> {
                                Pair<Location, Location> pair = wandHandler.getPositionMap().getOrDefault(player, Pair.of(null, null));
                                if (pair.first() == null) {
                                    player.sendMessage("§e§b[!] §cYou don't have a first position set.");
                                    return;
                                } else if (pair.second() == null) {
                                    player.sendMessage("§e§b[!] §cYou don't have a second position set.");
                                    return;
                                }
                                List<Block> selection = Misc.getBlocks(pair.first(), pair.second());
                                if (selection.size() > Constants.MAX_BLOCKS) {
                                    player.sendMessage("§e§b[!] §cThis selection is too big. You may select a maximum of §e" + Constants.MAX_BLOCKS + "§7 blocks. [You currently have §e" + selection.size() + "§7 selected]");
                                    return;
                                }
                                Inventory inventory = closeEvent.getInventory();
                                List<ItemStack> results = new ArrayList<>();
                                for (Block block : selection) {
                                    for (int i = 0; i < inventory.getSize(); i++) {
                                        ItemStack item = inventory.getItem(i);
                                        if (item == null || item.getType() == Material.AIR)
                                            continue;
                                        Collection<ItemStack> drops = block.getDrops(item);
                                        if (drops.isEmpty())
                                            continue;
                                        if (block.breakNaturally(item, true, true))
                                            results.addAll(drops);
                                    }
                                }
                                GUI resultGui = new GUI("§e§oKaturi", 64)
                                        .setOwnClickable(true)
                                        .setCloseEvent(closeEvent2 -> {
                                            for (ItemStack item : closeEvent2.getInventory())
                                                player.getLocation().getWorld().dropItem(player.getLocation(), item);
                                        })
                                        .addItem(results.toArray(new ItemStack[0]));
                                GUI.registerGUI(resultGui);
                                player.sendMessage("§e§b[!] §7Your operation is complete, please collect the broken blocks and remaining tools.");
                                player.closeInventory(); // JIC
                                resultGui.openTo(player);
                            });
                    GUI.registerGUI(breakMenu);
                    player.closeInventory();
                    breakMenu.openTo(player);
                }, 12)
                .addClickEvent(event -> {
                    event.setCancelled(true);
                    Player player = (Player) event.getWhoClicked();
                    GUI buildMenu = new GUI("§a§oKaturi", 64)
                            .setOwnClickable(true)
                            .setCloseEvent(closeEvent -> {
                                Pair<Location, Location> pair = wandHandler.getPositionMap().getOrDefault(player, Pair.of(null, null));
                                if (pair.first() == null) {
                                    player.sendMessage("§e§b[!] §cYou don't have a first position set.");
                                    return;
                                } else if (pair.second() == null) {
                                    player.sendMessage("§e§b[!] §cYou don't have a second position set.");
                                    return;
                                }
                                List<Block> selection = Misc.getBlocks(pair.first(), pair.second());
                                if (selection.size() > Constants.MAX_BLOCKS) {
                                    player.sendMessage("§e§b[!] §cThis selection is too big. You may select a maximum of §e" + Constants.MAX_BLOCKS + "§7 blocks. [You currently have §e" + selection.size() + "§7 selected]");
                                    return;
                                }
                                Inventory inventory = closeEvent.getInventory();
                                List<ItemStack> used = new ArrayList<>();
                                for (int i = 0; i < inventory.getSize(); i++) {
                                    ItemStack item = inventory.getItem(i);
                                    if (item == null || !item.getType().isBlock())
                                        continue;
                                    List<Block> done = new ArrayList<>();
                                    while (item.getAmount() != 0) {
                                        for (Block block : selection) {
                                            block.setType(item.getType());
                                            item.setAmount(item.getAmount() - 1);
                                            ItemStack clone = item.clone();
                                            clone.setAmount(1);
                                            used.add(clone);
                                            done.add(block);
                                        }
                                    }
                                    selection.removeAll(done);
                                }
                                inventory.removeItem(used.toArray(new ItemStack[0]));
                                GUI resultGui = new GUI("§e§oKaturi", 64)
                                        .setOwnClickable(true)
                                        .setCloseEvent(closeEvent2 -> {
                                            for (ItemStack item : closeEvent2.getInventory())
                                                player.getLocation().getWorld().dropItem(player.getLocation(), item);
                                        })
                                        .addItem(inventory.getContents());
                                GUI.registerGUI(resultGui);
                                player.sendMessage("§e§b[!] §7Your operation is complete, please collect the remaining blocks.");
                                player.closeInventory(); // JIC
                                resultGui.openTo(player);
                            });
                    GUI.registerGUI(buildMenu);
                    player.closeInventory();
                    buildMenu.openTo(player);
                }, 14)
                .addClickEvent(event -> event.setCancelled(true), 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26)
                .setDragEvent(event -> event.setCancelled(true));
        GUI.registerGUI(this.wandGUI);
    }

    /**
     * Gets the Katuri wand, useful for comparison.
     *
     * @return the wand.
     */
    public ItemStack getWand() {
        return this.wand;
    }

    /**
     * Gets the wand's GUI.
     *
     * @return the wand gui.
     */
    public GUI getWandGUI() {
        return this.wandGUI;
    }

}