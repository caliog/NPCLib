package com.sharesc.caliog.npclib;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;

public class NPCEntityTargetEvent extends EntityTargetEvent {

	public static enum NPCTargetReason {
		CLOSEST_PLAYER, NPC_RIGHTCLICKED, NPC_BOUNCED
	}

	private NPCTargetReason reason;

	public NPCEntityTargetEvent(Entity entity, Entity target,
			NPCTargetReason reason) {
		super(entity, target, TargetReason.CUSTOM);
		this.reason = reason;
	}

	public NPCTargetReason getNPCReason() {
		return reason;
	}

}
