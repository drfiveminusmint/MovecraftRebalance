package io.github.drfiveminusmint.movecraftrebalance.command;

import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.drfiveminusmint.movecraftrebalance.MovecraftRebalance;
import io.github.drfiveminusmint.movecraftrebalance.manager.MCRCraftData;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;

public class MCRCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		
		if(!command.getName().equalsIgnoreCase("mcr")) {
			return false;
		}
		
		if(args.length < 1) {
			sender.sendMessage("Movecraft Rebalance v0.01");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("register")) {
			Player p;
			if(args.length < 2) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("You must supply a player whose craft to register.");
					return true;
				}
				p = (Player) sender;
			} else { 
				p = Bukkit.getPlayer(args[1]);
			}
			if (p == null) {
				sender.sendMessage("Cannot find specified player.");
				return true;
			}
			Craft c = CraftManager.getInstance().getCraftByPlayer(p);
			if (c == null) {
				sender.sendMessage("Could not find any craft piloted by " + p.getName());
			}
			if (!MovecraftRebalance.getInstance().getCraftTracker().addCraft(c)) {
				sender.sendMessage("Craft is already registered!");
			}
			MovecraftRebalance.getInstance().getLogger().log(Level.INFO, "New craft registered.");
			return true;
		}
		if(args[0].equalsIgnoreCase("debug")) {
			for (Craft c : MovecraftRebalance.getInstance().getCraftTracker().getAllCrafts()) {
				if (c == null) continue;
				sender.sendMessage(c.getType().getCraftName()+ " commanded by " + c.getNotificationPlayer().getName());
			}
			return true;
		}
		
		if(args[0].equalsIgnoreCase("addheat")) {
			if(args.length < 3) {
				sender.sendMessage("Insufficient arguments. Use /mcr addheat [player] [strength]");
				return true;
			}
			Player p = Bukkit.getPlayer(args[1]);
			if (p == null) {
				sender.sendMessage("Could not find player " + args[1]);
				return true;
			}
			Craft c = CraftManager.getInstance().getCraftByPlayer(p);
			if (c == null) {
				sender.sendMessage("Could not find a craft piloted by " + args[1]);
				return true;
			}
			Float f;
			try {
				f = Float.parseFloat(args[2]);
			} catch (NumberFormatException e) {
				sender.sendMessage("\"" + args[2] + "\" is not a valid number.");
				return true;
			}
			
			MCRCraftData data = MovecraftRebalance.getInstance().getCraftTracker().getDataByCraft(c);
			if (data == null) {
				sender.sendMessage("Craft is not registered, either use /mcr register, or wait a few seconds for the craftlist to update.");
				return true;
			}
			data.addHeat(f);
			sender.sendMessage("Added heat, target craft now has " + Double.toString(data.getCurrentHeat()) + "/" + Double.toString(data.getHeatThreshold()));
			return true;
		}
		if(args[0].equalsIgnoreCase("checkheat")) {
			if(args.length < 2) {
				sender.sendMessage("Insufficient arguments. Use /mcr checkheat <player>");
				return true;
			}
			Player p = Bukkit.getPlayer(args[1]);
			if (p == null) {
				sender.sendMessage("Could not find player " + args[1]);
				return true;
			}
			Craft c = CraftManager.getInstance().getCraftByPlayer(p);
			if (c == null) {
				sender.sendMessage("Could not find a craft piloted by " + args[1]);
				return true;
			}
			MCRCraftData data = MovecraftRebalance.getInstance().getCraftTracker().getDataByCraft(c);
			if (data == null) {
				sender.sendMessage("Craft is not registered, either use /mcr register, or wait a few seconds for the craftlist to update.");
				return true;
			}
			sender.sendMessage("Target craft has heat of " + Double.toString(data.getCurrentHeat()) + "/" + Double.toString(data.getHeatThreshold()));
			return true;
		}
		return false;
	}

}
