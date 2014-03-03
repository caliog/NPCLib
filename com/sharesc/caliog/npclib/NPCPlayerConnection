package com.sharesc.caliog.npclib;

import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.CraftServer;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;

public class NPCPlayerConnection extends PlayerConnection {

    public NPCPlayerConnection(NPCManager npcManager, EntityPlayer entityplayer) {
	super(npcManager.getServer().getMCServer(), npcManager.getNPCNetworkManager(), entityplayer);
    }

    @Override
    public CraftPlayer getPlayer() {
	return new CraftPlayer((CraftServer) Bukkit.getServer(), player); // Fake player prevents spout NPEs
    }

}
