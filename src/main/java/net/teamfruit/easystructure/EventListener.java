package net.teamfruit.easystructure;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

public class EventListener implements Listener {
    private final EasyStructure plugin;

    public EventListener(EasyStructure plugin) {
        this.plugin = plugin;

        new BukkitRunnable() {
            @Override
            public void run() {
                onEffect();
            }
        }.runTaskTimer(this.plugin, 3, 3);
    }

    private void onEffect() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            final ItemStack itemMain = player.getInventory().getItemInMainHand();
            if (itemMain.getType() != Material.BLAZE_ROD)
                continue;

            RayTraceResult rayResult = player.rayTraceBlocks(32, FluidCollisionMode.NEVER);
            Block hitBlock = rayResult.getHitBlock();
            BlockFace hitFace = rayResult.getHitBlockFace();

            if (hitBlock == null)
                continue;

            final int color = plugin.getConfig().getInt(Config.SETTING_PARTICLE_COLOR, 0xffffff);
            final int color_r = (color << 16) & 0xff;
            final int color_g = (color << 8) & 0xff;
            final int color_b = (color << 0) & 0xff;
            final int range = plugin.getConfig().getInt(Config.SETTING_PARTICLE_RANGE);
            /*
            if (range > 0) {
                for (final Player other : Bukkit.getOnlinePlayers())
                    if (other.getLocation().distance(player.getLocation()) <= range)
                        for (final Location block : blocks)
                            this.nativemc.spawnParticles(other, block, color_r / 255f, color_g / 255f, color_b / 255f);
            } else
                for (final Location block : blocks)
                    this.nativemc.spawnParticles(player, block, color_r / 255f, color_g / 255f, color_b / 255f);
            */
            player.spawnParticle(Particle.REDSTONE, hitBlock.getLocation(), 1, color_r / 255f, color_g / 255f, color_b / 255f);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerUse(final PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND)
            return;

        final Player player = event.getPlayer();

        if (!player.hasPermission("es.use"))
            return;

        final ItemStack itemMain = player.getInventory().getItemInMainHand();
        if (itemMain.getType() != Material.BLAZE_ROD)
            return;

        RayTraceResult rayResult = player.rayTraceBlocks(32, FluidCollisionMode.NEVER);
        Block hitBlock = rayResult.getHitBlock();
        BlockFace hitFace = rayResult.getHitBlockFace();

        if (hitBlock == null)
            return;

        if (ActionResult.sendResultMessage(onPlayerUse(player, itemMain, event.getAction(), hitBlock, hitFace), player))
            event.setCancelled(true);
    }

    private ActionResult onPlayerUse(final Player player, ItemStack itemMain, final Action action, final Block target, final BlockFace face) {
        return ActionResult.success();
    }
}