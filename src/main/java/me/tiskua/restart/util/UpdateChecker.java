package me.tiskua.restart.util;

import me.tiskua.restart.main.*;
import org.bukkit.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.*;

public class UpdateChecker {

	private final Main main;
	private final int resourceID;
	
	public UpdateChecker(Main main, int resourceID) {
		this.main = main;
		this.resourceID = resourceID;
	}
	
	public void getLatestVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.main, () -> {
            try (InputStream inputstream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceID).openStream();
            Scanner s = new Scanner(inputstream)) {
            if (s.hasNext()) consumer.accept(s.next());
        } catch (IOException e) {
                main.getLogger().info("Update checker is broken, can't find an update!" + e.getMessage());
            }
        });
    }
}
