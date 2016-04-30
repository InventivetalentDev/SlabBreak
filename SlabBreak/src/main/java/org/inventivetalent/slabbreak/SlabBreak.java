package org.inventivetalent.slabbreak;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.mcstats.MetricsLite;

public class SlabBreak extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);

		try {
			MetricsLite metrics = new MetricsLite(this);
			if (metrics.start()) {
				getLogger().info("Metrics started");
			}
		} catch (Exception e) {
		}
	}

	@EventHandler
	public void on(BlockBreakEvent event) {
		if (event.isCancelled()) { return; }
		if (!event.getPlayer().hasPermission("slabbreak.use")) { return; }
		if (isDoubleSlab(event.getBlock())) {
			event.setCancelled(true);

			Vector direction = event.getPlayer().getLocation().getDirection();
			Vector blockVector = null;
			for (double d = 1; d < 16; d += .06) {
				Vector multiplied = direction.clone().multiply(d).add(new Vector(0, event.getPlayer().getEyeHeight(), 0)).add(event.getPlayer().getLocation().toVector());
				Location multipliedLocation = multiplied.toLocation(event.getPlayer().getWorld());
				if (multipliedLocation.getBlock().getType() == event.getBlock().getType()) {
					blockVector = multiplied;
					break;
				}
			}

			if (blockVector != null) {//We know the clicked height
				double blockY = blockVector.getY() - ((int) blockVector.getY());
				if (blockY > .5) {//Upper half
					event.getBlock().setTypeIdAndData(event.getBlock().getTypeId() + 1, event.getBlock().getData(), true);
					if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) { event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(event.getBlock().getType(), 1, event.getBlock().getData())); }
				} else {//Lower half
					event.getBlock().setTypeIdAndData(event.getBlock().getTypeId() + 1, (byte) (event.getBlock().getData() + 8), true);
					if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) { event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(event.getBlock().getType(), 1, (byte) (event.getBlock().getData() & 7))); }
				}
			} else {
				event.getBlock().setTypeIdAndData(event.getBlock().getTypeId() + 1, event.getBlock().getData(), true);
				if (event.getPlayer().getGameMode() == GameMode.SURVIVAL) { event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(event.getBlock().getType(), 1, event.getBlock().getData())); }
			}
		}
	}

	boolean isDoubleSlab(Block block) {
		if (block == null) { return false; }
		switch (block.getType()) {
			case DOUBLE_STEP:
			case WOOD_DOUBLE_STEP:
			case PURPUR_DOUBLE_SLAB:
			case DOUBLE_STONE_SLAB2:
				return true;
		}
		return false;
	}
}
