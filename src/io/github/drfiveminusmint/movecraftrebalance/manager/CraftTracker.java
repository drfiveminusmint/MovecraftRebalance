package io.github.drfiveminusmint.movecraftrebalance.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.drfiveminusmint.movecraftrebalance.MovecraftRebalance;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;

public class CraftTracker extends BukkitRunnable {
	private Map<Craft, MCRCraftData> trackedCrafts = new HashMap<Craft, MCRCraftData>();
	
	
	@Override
	public void run() {
		for (World w : Bukkit.getServer().getWorlds()) {
			for (Craft c : CraftManager.getInstance().getCraftsInWorld(w)) {
				if (!trackedCrafts.containsKey(c)) {
					addCraft(c);
				}
			}
		}
		purgeNullCrafts();
		for (Craft c : trackedCrafts.keySet()) {
			MCRCraftData data = trackedCrafts.get(c);
			data.detectVents();
			data.processHeat();
		}
	}
	
	public MCRCraftData getDataByCraft(Craft c) {
		return trackedCrafts.get(c);
	}
	
	public boolean addCraft(Craft c) {
		if (isTrackingCraft(c)) {
			return false;
		}
		trackedCrafts.put(c, new MCRCraftData(c));
		return true;
	}
	
	public boolean isTrackingCraft(Craft c) {
		return (trackedCrafts.containsKey(c));
	}
	public List<Craft> getAllCrafts() {
		purgeNullCrafts();
		List<Craft> l = new ArrayList<Craft>();
		for (Craft c : trackedCrafts.keySet()) {
			l.add(c);
		}
		return l;
	}
	
	private void purgeNullCrafts() {
		List<Craft> craftsToRemove = new ArrayList<Craft>();
		for (Craft c : trackedCrafts.keySet()) {
			if (c == null || c.getNotificationPlayer() == null || CraftManager.getInstance().getCraftByPlayer(c.getNotificationPlayer()) == null) {
				craftsToRemove.add(c);
			}
		}
		for (Craft removeCraft : craftsToRemove) {
			trackedCrafts.remove(removeCraft);
		}
	}
}
