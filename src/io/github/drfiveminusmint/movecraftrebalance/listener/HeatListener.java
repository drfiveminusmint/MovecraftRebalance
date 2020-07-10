package io.github.drfiveminusmint.movecraftrebalance.listener;

import org.bukkit.event.Listener;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseEvent;
import io.github.drfiveminusmint.movecraftrebalance.MovecraftRebalance;
import io.github.drfiveminusmint.movecraftrebalance.config.Settings;
import io.github.drfiveminusmint.movecraftrebalance.manager.MCRCraftData;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.utils.MathUtils;

public class HeatListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockDispense(BlockDispenseEvent e) {
		if(e.getItem().getType() != Material.TNT) {
			return;
		}
		MovecraftRebalance.getInstance().getLogger().log(Level.INFO, "Dispensed TNT Detected!");
		Location l = e.getBlock().getLocation();
		for (Craft c : CraftManager.getInstance().getCraftsInWorld(e.getBlock().getWorld())) {
			if (!MathUtils.locIsNearCraftFast(c, MathUtils.bukkit2MovecraftLoc(l))) {
				continue;
			}
			if(MathUtils.locationNearHitBox(c.getHitBox(), l, 1.0)) {
				MCRCraftData data = MovecraftRebalance.getInstance().getCraftTracker().getDataByCraft(c);
				if (data == null) {
					return;
				}
				data.addHeat(1f * Settings.heatPerTNT);
			}
		}
	}
}
