package io.github.drfiveminusmint.movecraftrebalance.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

import io.github.drfiveminusmint.movecraftrebalance.MovecraftRebalance;
import io.github.drfiveminusmint.movecraftrebalance.config.Settings;
import io.github.drfiveminusmint.movecraftrebalance.util.CraftRelativeLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.utils.BitmapHitBox;
import net.countercraft.movecraft.MovecraftLocation;

public class MCRCraftData {
		private List<CraftRelativeLocation> ventLocations = new ArrayList<CraftRelativeLocation>();
		private Craft craft;
		private double heatThreshold;
		private double currentHeat;
		private double coolingMult;
		
		public MCRCraftData(Craft c) {
			if (c == null) {
				MovecraftRebalance.getInstance().getLogger().log(Level.SEVERE, "Attemped to generate craft data for a null craft!");
				return;
			}
			this.craft = c;
			this.coolingMult = Settings.flatCooling + Settings.coolingPerBlock * craft.getOrigBlockCount();
			this.heatThreshold = Settings.flatThreshold + Settings.heatThresholdPerBlock * craft.getOrigBlockCount();
			this.detectVents();
			this.currentHeat = 0;
		}
		
		public void detectVents() {
			ventLocations = new ArrayList<CraftRelativeLocation>();
			BitmapHitBox hitbox = craft.getHitBox();
			for (MovecraftLocation loc : hitbox) {	
				if(hitbox.contains(loc.translate(0, 1, 0))) {
					continue;
				}
				MovecraftLocation aboveLocation = loc.translate(0, 1, 0);
				if(aboveLocation.toBukkit(craft.getW()).getBlock().getType().equals(Material.AIR)) {
					if (aboveLocation.translate(0, -1, 0).toBukkit(craft.getW()).getBlock().getType().equals(Material.AIR)) {
						continue;
					}
					boolean ceilingFound = false;
					for (int x = 2; x+aboveLocation.getX() < hitbox.getMaxY(); x++) {
						if (hitbox.contains(aboveLocation.translate(0, x, 0))) {
							ceilingFound = true;
							break;
						}
						
					}
					if (!ceilingFound) {
						CraftRelativeLocation newLocation = CraftRelativeLocation.fromAbsolute(craft, aboveLocation);
						ventLocations.add(newLocation);
					}
				}
			}
		}
		
		public int igniteInterior (double strength) {
			Random rand = new Random();
			int ignitions = 0;
			for (MovecraftLocation l : craft.getHitBox()) {
				if (rand.nextDouble() > strength) {
					continue;
				}
				if (l.toBukkit(craft.getW()).getBlock().getType().isBurnable()) {
					l.toBukkit(craft.getW()).getBlock().setType(Material.FIRE);
					ignitions++;
				}
			}
			return ignitions;
		}
		
		public int explodeInterior (double strength) {
			Random rand = new Random();
			int ignitions = 0;
			for (MovecraftLocation l : craft.getHitBox()) {
				if (rand.nextDouble() > strength) {
					continue;
				}
				if (l.toBukkit(craft.getW()).getBlock().getType() != Material.AIR) {
					craft.getW().spawnEntity(l.toBukkit(craft.getW()), EntityType.PRIMED_TNT);
					ignitions++;
				}
			}
			return ignitions;
		}
		
		public int igniteSurface (double strength) {
			Random rand = new Random();
			int ignitions = 0;
			for (CraftRelativeLocation l : ventLocations) {
				if (rand.nextDouble() > strength) {
					continue;
				}
				Block b = l.toAbsolute().getBlock();
				if (b.getType() == Material.AIR) {
					b.setType(Material.FIRE);
					ignitions++;
				}
			}
			return ignitions;
		}
		
		public void processHeat() {
			Random random = new Random();
			if (currentHeat <= 0) {
				currentHeat = 0;
				return;
			}
			//TODO: configuration for each catastrophe
			if (currentHeat >= heatThreshold * Settings.explosionThreshold && random.nextDouble() < 0.5) {
				currentHeat -= explodeInterior(Settings.explosionChance) * Settings.coolingPerExplosion;
			} else if (currentHeat >= heatThreshold * Settings.internalFireThreshold && random.nextDouble() < 0.5) {
				currentHeat -= igniteInterior(Settings.internalFireChance) * Settings.coolingPerInternalFire;
			} else if (currentHeat >= heatThreshold * Settings.surfaceFireThreshold) {
				currentHeat -= igniteSurface(Settings.surfaceFireChance) * Settings.coolingPerSurfaceFire;
			}
			currentHeat -= 20 * coolingMult;
		}
		
		public double getCurrentHeat() {
			return currentHeat;
		}
		
		public double getHeatThreshold () {
			return heatThreshold;
		}
		
		public void setCurrentHeat(double d) {
			currentHeat = d;
		}
		
		public void addHeat (double d) {
			currentHeat += d;
		}
}
