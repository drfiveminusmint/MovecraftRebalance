package io.github.drfiveminusmint.movecraftrebalance.util;
import org.bukkit.Location;

import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.utils.BitmapHitBox;

public class CraftRelativeLocation {
	private int x, y ,z;
	private Craft craft;
	
	//Creates a location from *relative* coordinates
	public CraftRelativeLocation(Craft c, int relx, int rely, int relz) {
		this.x = relx;
		this.y = rely;
		this.z = relz;
		this.craft = c;
	}
	
	//Creates a new location from absolute coordinates
	public static CraftRelativeLocation fromAbsolute(Craft c, int absx, int absy, int absz) {
		BitmapHitBox hitbox = c.getHitBox();
		CraftRelativeLocation result = new CraftRelativeLocation(c, absx-hitbox.getMinX(), absy-hitbox.getMinY(), absz-hitbox.getMinZ());
		return result;
	}
	
	public static CraftRelativeLocation fromAbsolute (Craft c, Location l) {
		return CraftRelativeLocation.fromAbsolute(c, l.getBlockX(),l.getBlockY(), l.getBlockZ());
	}
	
	public static CraftRelativeLocation fromAbsolute (Craft c, MovecraftLocation l) {
		return CraftRelativeLocation.fromAbsolute(c, l.getX(), l.getY(), l.getZ());
	}
	
	//Gets our location in absolute Bukkit terms
	public Location toAbsolute() {
		BitmapHitBox hitbox = craft.getHitBox();
		Location offset = new Location(craft.getW(), hitbox.getMinX(),hitbox.getMinY(),hitbox.getMinZ());
		Location result = new Location(craft.getW(), x, y, z).add(offset);
		return result;
	}
	
	//Standard translation function
	public CraftRelativeLocation translate(int dx, int dy, int dz) {
		return new CraftRelativeLocation(craft, x + dx, y + dy, z + dz);
	}
	
	//Getters and setters, nothing to see here
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getZ() {
		return z;
	}
	public Craft getCraft() {
		return craft;
	}
	public void setX(int i) {
		x = i;
	}
	public void setY(int i) {
		y = i;
	}
	public void setZ(int i) {
		z = i;
	}
	public void setCraft(Craft c) {
		craft = c;
	}
}
