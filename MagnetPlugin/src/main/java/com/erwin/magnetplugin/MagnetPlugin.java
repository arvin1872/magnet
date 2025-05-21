package com.erwin.magnetplugin;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class MagnetPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("MagnetPlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MagnetPlugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("magnet")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("این دستور فقط برای بازیکنان است!");
                return true;
            }

            Player player = (Player) sender;

            // ساخت آیتم آهنربا (مثلا آهن زره اسب)
            ItemStack magnet = new ItemStack(Material.IRON_HORSE_ARMOR);
            ItemMeta meta = magnet.getItemMeta();
            meta.setDisplayName("§aآهن‌ربای جادویی");
            magnet.setItemMeta(meta);

            player.getInventory().addItem(magnet);
            player.sendMessage("آهن‌ربا به شما داده شد! آیتم‌های نزدیک را می‌کشد.");

            // اجرای تاسک کشیدن آیتم‌ها
            startMagnetTask(player);

            return true;
        }
        return false;
    }

    private void startMagnetTask(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                // بررسی اینکه بازیکن هنوز آهنربا دارد (مثلا در دست اصلی)
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                if (mainHand == null || mainHand.getType() != Material.IRON_HORSE_ARMOR) {
                    cancel();
                    player.sendMessage("آهن‌ربا را از دست دادید! وظیفه کشیدن آیتم‌ها متوقف شد.");
                    return;
                }

                double radius = 6.0;
                List<Item> nearbyItems = player.getNearbyEntities(radius, radius, radius).stream()
                        .filter(e -> e instanceof Item)
                        .map(e -> (Item) e)
                        .toList();

                for (Item item : nearbyItems) {
                    Vector direction = player.getLocation().toVector().subtract(item.getLocation().toVector()).normalize();
                    item.setVelocity(direction.multiply(0.7));
                }
            }
        }.runTaskTimer(this, 0L, 10L);
    }
}
