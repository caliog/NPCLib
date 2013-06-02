package com.sharesc.caliog.npclib;

import net.minecraft.server.v1_5_R3.Entity;
import net.minecraft.server.v1_5_R3.EntityHuman;
import net.minecraft.server.v1_5_R3.EntityPlayer;
import net.minecraft.server.v1_5_R3.EnumGamemode;
import net.minecraft.server.v1_5_R3.Packet17EntityLocationAction;
import net.minecraft.server.v1_5_R3.PlayerInteractManager;
import net.minecraft.server.v1_5_R3.WorldServer;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftEntity;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * 
 * @author martin
 */
public class NPCEntity extends EntityPlayer {

  private int lastTargetId;
  private long lastBounceTick;
  private int lastBounceId;
  private boolean isSleeping;

	public NPCEntity(NPCManager npcManager, BWorld world, String s,
			PlayerInteractManager playerInteractManager) {
		super(npcManager.getServer().getMCServer(), world.getWorldServer(), s,
				playerInteractManager);

		playerInteractManager.b(EnumGamemode.SURVIVAL);

		playerConnection = new NPCNetHandler(npcManager, this);
		lastTargetId = -1;
		lastBounceId = -1;
		lastBounceTick = 0;

		fauxSleeping = true;
	}

	public void setBukkitEntity(org.bukkit.entity.Entity entity) {
		// TODO
		bukkitEntity = (CraftEntity) entity;
	}

	@Override
	public boolean a_(EntityHuman entity) {
		EntityTargetEvent event = new NPCEntityTargetEvent(getBukkitEntity(),
				entity.getBukkitEntity(),
				NPCEntityTargetEvent.NPCTargetReason.NPC_RIGHTCLICKED);
		CraftServer server = ((WorldServer) world).getServer();
		server.getPluginManager().callEvent(event);

		return super.a_(entity);
	}

	public void b_(EntityHuman entity) {
		if ((lastBounceId != entity.id || System.currentTimeMillis()
				- lastBounceTick > 1000)
				&& entity.getBukkitEntity().getLocation()
						.distanceSquared(getBukkitEntity().getLocation()) <= 1) {
			EntityTargetEvent event = new NPCEntityTargetEvent(
					getBukkitEntity(), entity.getBukkitEntity(),
					NPCEntityTargetEvent.NPCTargetReason.NPC_BOUNCED);
			CraftServer server = ((WorldServer) world).getServer();
			server.getPluginManager().callEvent(event);

			lastBounceTick = System.currentTimeMillis();
			lastBounceId = entity.id;
		}

		if (lastTargetId == -1 || lastTargetId != entity.id) {
			EntityTargetEvent event = new NPCEntityTargetEvent(
					getBukkitEntity(), entity.getBukkitEntity(),
					NPCEntityTargetEvent.NPCTargetReason.CLOSEST_PLAYER);
			CraftServer server = ((WorldServer) world).getServer();
			server.getPluginManager().callEvent(event);
			lastTargetId = entity.id;
		}

		super.b_(entity);// TODO c_ renamed to b_ ?! TEST
	}

	@Override
	public void c(Entity entity) {
		if (lastBounceId != entity.id
				|| System.currentTimeMillis() - lastBounceTick > 1000) {
			EntityTargetEvent event = new NPCEntityTargetEvent(
					getBukkitEntity(), entity.getBukkitEntity(),
					NPCEntityTargetEvent.NPCTargetReason.NPC_BOUNCED);
			CraftServer server = ((WorldServer) world).getServer();
			server.getPluginManager().callEvent(event);

			lastBounceTick = System.currentTimeMillis();
		}

		lastBounceId = entity.id;

		super.c(entity);
	}

	@Override
	public void move(double arg0, double arg1, double arg2) {
		setPosition(arg0, arg1, arg2);
	}
	
	public void setSleeping(boolean sleep) {
		  if (sleep) {
		    this.sleeping = true;
		    this.isSleeping = true;
		    Packet17EntityLocationAction packet17entitylocationaction = new Packet17EntityLocationAction(this, 0, 22, 162, 187);

		    o().getTracker().a(this, packet17entitylocationaction);
		    this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
		    this.playerConnection.sendPacket(packet17entitylocationaction);
		    a(1.0F, 2.0F);
		  }
		  else {
		    this.playerConnection.player.a(false, true, true);
		    this.sleeping = false;
		    this.isSleeping = false;
		  }
	}
	
	public boolean isSleeping(){
		return isSleeping;
	}

}
