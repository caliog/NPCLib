package org.caliog.npclib;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.v1_8_R3.DedicatedPlayerList;
import net.minecraft.server.v1_8_R3.DedicatedServer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PropertyManager;
import net.minecraft.server.v1_8_R3.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.console.ConsoleReader;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

/**
 * Server hacks for Bukkit
 * 
 * @author Kekec852
 */
public class BServer {

    private static BServer ins;
    private MinecraftServer mcServer;
    private CraftServer cServer;
    private final Server server;
    private final HashMap<String, BWorld> worlds = new HashMap<>();

    private BServer() {
	server = Bukkit.getServer();
	try {
	    cServer = (CraftServer) server;
	    mcServer = cServer.getServer();
	} catch (final Exception ex) {
	    Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
	}
    }

    public void disablePlugins() {
	cServer.disablePlugins();
    }

    public void dispatchCommand(CommandSender sender, String msg) {
	cServer.dispatchCommand(sender, msg);
    }

    public DedicatedPlayerList getHandle() {
	return cServer.getHandle();
    }

    public ConsoleReader getReader() {
	return cServer.getReader();
    }

    public void loadPlugins() {
	cServer.loadPlugins();
    }

    public void stop() {
	mcServer.safeShutdown();
    }

    public void sendConsoleCommand(String cmd) {
	if (mcServer.isRunning()) {
	    ((DedicatedServer) mcServer).issueCommand(cmd, mcServer);
	}
    }

    public Logger getLogger() {
	return cServer.getLogger();
    }

    public List<WorldServer> getWorldServers() {
	return mcServer.worlds;
    }

    public int getSpawnProtationRadius() {
	return mcServer.server.getSpawnRadius();
    }

    public PropertyManager getPropertyManager() {
	return mcServer.getPropertyManager();
    }

    public Server getServer() {
	return server;
    }

    public BWorld getWorld(String worldName) {
	if (worlds.containsKey(worldName)) {
	    return worlds.get(worldName);
	}
	final BWorld w = new BWorld(this, worldName);
	worlds.put(worldName, w);
	return w;
    }

    public static BServer getInstance() {
	if (ins == null) {
	    ins = new BServer();
	}
	return ins;
    }

    public MinecraftServer getMCServer() {
	return mcServer;
    }

}
