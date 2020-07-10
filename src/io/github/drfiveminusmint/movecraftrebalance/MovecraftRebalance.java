package io.github.drfiveminusmint.movecraftrebalance;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import io.github.drfiveminusmint.movecraftrebalance.manager.CraftTracker;
import io.github.drfiveminusmint.movecraftrebalance.command.MCRCommand;
import io.github.drfiveminusmint.movecraftrebalance.config.Settings;
import io.github.drfiveminusmint.movecraftrebalance.listener.HeatListener;
import net.countercraft.movecraft.Movecraft;

public class MovecraftRebalance extends JavaPlugin {
	private static MovecraftRebalance instance;
	private static CraftTracker craftTracker;
	private Movecraft movecraft;
	private Logger logger;
	
	@Override
	public void onEnable() {
		Plugin movecraftPlugin = Bukkit.getServer().getPluginManager().getPlugin("Movecraft");
		if(movecraftPlugin == null || !(movecraftPlugin instanceof Movecraft)) {
			logger.log(Level.SEVERE, "No compatable Movecraft plugin found!");
			this.setEnabled(false);
			return;
		}
		
		this.saveDefaultConfig();
		if (getConfig() == null) {
			logger.log(Level.SEVERE, "No configuration file found!");
			this.setEnabled(false);
			return;
		} else {
			Settings.heatPerTNT = getConfig().getDouble("HeatPerTNT", 20.0);
			Settings.flatCooling = getConfig().getDouble("FlatCooling", 20.0);
			Settings.flatThreshold = getConfig().getDouble("FlatThreshold", 400.0);
			Settings.heatThresholdPerBlock = getConfig().getDouble("HeatThresholdPerBlock", 1.0);
			Settings.coolingPerBlock = getConfig().getDouble("CoolingPerBock", 0.01);
			Settings.surfaceFireThreshold = getConfig().getDouble("surfaceFireThreshold", 1.0);
			Settings.surfaceFireChance = getConfig().getDouble("SurfaceFireChance", 0.2);
			Settings.coolingPerSurfaceFire = getConfig().getDouble("CoolingPerSurfaceFire", 2.0);
			Settings.internalFireThreshold = getConfig().getDouble("InternalFireThreshold", 1.5);
			Settings.internalFireChance = getConfig().getDouble("InternalFireChance", 0.01);
			Settings.coolingPerInternalFire = getConfig().getDouble("CoolingPerInternalFire", 8.0);
			Settings.explosionThreshold = getConfig().getDouble("ExplosionThreshold", 2.0);
			Settings.explosionChance = getConfig().getDouble("ExplosionChance", 0.01);
			Settings.coolingPerExplosion = getConfig().getDouble("CoolingPerExplosion", 20);
		}
		
		getServer().getPluginManager().registerEvents(new HeatListener(), this);
		this.getCommand("mcr").setExecutor(new MCRCommand());
		
		craftTracker = new CraftTracker();
		craftTracker.runTaskTimer(this, 200, 400);
		
		logger.log(Level.INFO, "Successfully enabled Movecraft Rebalance " + this.getDescription().getVersion());
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		instance = this;
		logger = getLogger();
	}
	
	public Movecraft getMovecraftPlugin() {
		return movecraft;
	}
	
	public CraftTracker getCraftTracker() {
		return craftTracker; 
	}
	
	public static MovecraftRebalance getInstance() {
		return instance;
	}
}
