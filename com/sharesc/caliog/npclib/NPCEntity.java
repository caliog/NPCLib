package com.sharesc.caliog.npclib;

import net.minecraft.server.v1_5_R3.Entity;
import net.minecraft.server.v1_5_R3.EntityHuman;
import net.minecraft.server.v1_5_R3.EntityPlayer;
import net.minecraft.server.v1_5_R3.EnumGamemode;
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
		EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(),
				entity.getBukkitEntity(),
				NpcEntityTargetEvent.NpcTargetReason.NPC_RIGHTCLICKED);
		CraftServer server = ((WorldServer) world).getServer();
		server.getPluginManager().callEvent(event);

		return super.a_(entity);
	}

	public void b_(EntityHuman entity) {
		if ((lastBounceId != entity.id || System.currentTimeMillis()
				- lastBounceTick > 1000)
				&& entity.getBukkitEntity().getLocation()
						.distanceSquared(getBukkitEntity().getLocation()) <= 1) {
			EntityTargetEvent event = new NpcEntityTargetEvent(
					getBukkitEntity(), entity.getBukkitEntity(),
					NpcEntityTargetEvent.NpcTargetReason.NPC_BOUNCED);
			CraftServer server = ((WorldServer) world).getServer();
			server.getPluginManager().callEvent(event);

			lastBounceTick = System.currentTimeMillis();
			lastBounceId = entity.id;
		}

		if (lastTargetId == -1 || lastTargetId != entity.id) {
			EntityTargetEvent event = new NpcEntityTargetEvent(
					getBukkitEntity(), entity.getBukkitEntity(),
					NpcEntityTargetEvent.NpcTargetReason.CLOSEST_PLAYER);
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
			EntityTargetEvent event = new NpcEntityTargetEvent(
					getBukkitEntity(), entity.getBukkitEntity(),
					NpcEntityTargetEvent.NpcTargetReason.NPC_BOUNCED);
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

}
