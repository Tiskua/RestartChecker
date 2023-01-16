package me.tiskua.restart.command;

import org.bukkit.command.*;

import java.util.*;


public class TabComplete implements TabCompleter {
	
	List<String> arguments = new ArrayList<>();
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(arguments.isEmpty()) {
			arguments.add("startChecking");
			arguments.add("stopChecking");
			arguments.add("info");
			arguments.add("plugins");
			arguments.add("reload");
			arguments.add("enable");
			arguments.add("disable");
		}
		
		List<String> result = new ArrayList<>();
		if(args.length == 1) {
			for(String a : arguments)
				if(a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);

			return result;	
		}
		return null;
	}
}
