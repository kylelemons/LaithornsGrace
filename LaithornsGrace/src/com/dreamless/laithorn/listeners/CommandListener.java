package com.dreamless.laithorn.listeners;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dreamless.laithorn.LanguageReader;
import com.dreamless.laithorn.PlayerMessager;
import com.dreamless.laithorn.WellLocationHandler;
import com.dreamless.laithorn.api.FragmentRarity;
import com.dreamless.laithorn.api.ItemCrafting;
import com.dreamless.laithorn.player.CacheHandler;
import com.dreamless.laithorn.player.PlayerData;

public class CommandListener implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch (command.getName()) {
		case "givefragment":
			return cmdFragment(sender, args);
		case "fragments":
			return cmdPlayerInv(sender);
		case "clearwell":
			return cmdClearWell(sender);
		case "setwell":
			return cmdSetWell(sender);
		case "laithornlevels":
			return cmdPlayerInfo(sender, args);
		case "smithinglevel":
			return cmdSmithingInfo(sender);
		case "attunementlevel":
			return cmdAttunementInfo(sender);
		}
		return false;

	}

	private boolean cmdFragment(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			String level = "DULL";
			String type = "RAW";

			if (args.length >= 2) {
				level = args[0].toUpperCase();
				type = args[1].toUpperCase();
			} else if (args.length == 1) {
				level = args[0].toUpperCase();
				type = "RAW";
			} else {
				return false;
			}

			ArrayList<String> additionalFlags = new ArrayList<String>();
			for (int i = 2; i < args.length; i++) {
				additionalFlags.add(args[i].toUpperCase());
			}

			((Player) sender).getInventory()
					.addItem(ItemCrafting.fragmentItem(FragmentRarity.valueOf(level), type, additionalFlags));
			return true;
		}
		return false;
	}

	private boolean cmdPlayerInv(CommandSender sender) {
		if (sender instanceof Player) {
			((Player) sender).playSound(((Player) sender).getLocation(), Sound.ENTITY_PLAYER_SPLASH, 0.25f, 0.25f);
			((Player) sender).openInventory(CacheHandler.getPlayer((Player) sender).getInventory());
			return true;
		}
		return false;
	}

	private boolean cmdPlayerInfo(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) { // No console commands please
			PlayerMessager.msg(sender, LanguageReader.getText("Error_PlayerOnly"));
			return false;
		}

		PlayerData data = CacheHandler.getPlayer((Player) sender);

		if (args.length == 0) {
			PlayerMessager.msg(sender, data.toString());
		} else {
			switch (args[0]) {
			case "attunement":
			case "attune":
				return cmdAttunementInfo(sender);
			case "smithing":
			case "smith":
			case "crafting":
			case "craft":
				return cmdSmithingInfo(sender);
			default:
				PlayerMessager.msg(sender, data.toString());
				break;
			}
		}
		return true;
	}

	private boolean cmdAttunementInfo(CommandSender sender) {
		PlayerData data = CacheHandler.getPlayer((Player) sender);
		PlayerMessager.msg(sender,
				"Attunement level: " + data.getAttunementLevel() + " Attunement EXP: " + data.getAttunementEXP());
		return true;
	}
	
	private boolean cmdSmithingInfo(CommandSender sender) {
		PlayerData data = CacheHandler.getPlayer((Player) sender);
		PlayerMessager.msg(sender,
				"Smithing level: " + data.getSmithingLevel() + " Smithing EXP: " + data.getSmithingEXP());
		return true;
	}

	private boolean cmdClearWell(CommandSender sender) {
		WellLocationHandler.clearSpawn();
		PlayerMessager.msg(sender, LanguageReader.getText("CMD_Well_Cleared"));
		return true;
	}

	private boolean cmdSetWell(CommandSender sender) {
		if (!(sender instanceof Player)) { // No console commands please
			PlayerMessager.msg(sender, LanguageReader.getText("Error_PlayerOnly"));
			return false;
		}

		Player player = (Player) sender;

		Location currentLocation = player.getLocation();
		Location targetLocationetLocation = new Location(currentLocation.getWorld(), currentLocation.getBlockX(),
				currentLocation.getBlockY(), currentLocation.getBlockZ());

		if (WellLocationHandler.addCorner(targetLocationetLocation)) {
			PlayerMessager.msg(sender, LanguageReader.getText("CMD_Well_Success"));
		} else {
			PlayerMessager.msg(sender, LanguageReader.getText("CMD_Well_Failure"));
		}
		return true;
	}
}
