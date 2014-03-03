package com.sharesc.caliog.npclib;

import java.lang.reflect.Field;

import net.minecraft.server.v1_7_R1.EntityHuman;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_7_R1.WorldServer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class HumanNPC extends NPC {
    private net.minecraft.server.v1_7_R1.ItemStack[] previousEquipment = { null, null, null, null, null };

    public HumanNPC(NPCEntity npcEntity) {
	super(npcEntity);
    }

    public void animateArmSwing() {

	((WorldServer) getEntity().world).getTracker().a(getEntity(), new PacketPlayOutAnimation(this.getEntity(), 0));
    }

    public void actAsHurt() {
	((WorldServer) getEntity().world).broadcastEntityEffect(this.getEntity(), (byte) 2);
    }

    public void setItemInHand(Material m) {
	setItemInHand(m, (short) 0);
    }

    public void setItemInHand(Material m, short damage) {
	((HumanEntity) getEntity().getBukkitEntity()).setItemInHand(new ItemStack(m, 1, damage));
    }

    public void setName(String name) {
	try {
	    final Class<?> clazz = EntityHuman.class;
	    final Field nameField = clazz.getDeclaredField("name");
	    nameField.setAccessible(true);
	    nameField.set(getEntity(), name);
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    public String getName() {
	return ((NPCEntity) getEntity()).getName();
    }

    public PlayerInventory getInventory() {
	return ((HumanEntity) getEntity().getBukkitEntity()).getInventory();
    }

    public void putInBed(Location bed) {
	getEntity().setPosition(bed.getX(), bed.getY(), bed.getZ());
	getEntity().a((int) bed.getX(), (int) bed.getY(), (int) bed.getZ());
    }

    public void getOutOfBed() {
	((NPCEntity) getEntity()).a(true, true, true);
    }

    public void setSneaking() {
	getEntity().setSneaking(true);
    }

    public void lookAtPoint(Location point) {
	if (getEntity().getBukkitEntity().getWorld() != point.getWorld()) {
	    return;
	}
	final Location npcLoc = ((LivingEntity) getEntity().getBukkitEntity()).getEyeLocation();
	final double xDiff = point.getX() - npcLoc.getX();
	final double yDiff = point.getY() - npcLoc.getY();
	final double zDiff = point.getZ() - npcLoc.getZ();
	final double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
	final double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
	double newYaw = Math.acos(xDiff / DistanceXZ) * 180 / Math.PI;
	final double newPitch = Math.acos(yDiff / DistanceY) * 180 / Math.PI - 90;
	if (zDiff < 0.0) {
	    newYaw = newYaw + Math.abs(180 - newYaw) * 2;
	}
	getEntity().pitch = (float) newPitch;
	setYaw((float) newYaw - 90);

    }

    @Override
    public void moveTo(Location l) {
	getBukkitEntity().teleport(l);
	getEntity().setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
	setYaw(l.getYaw());
    }

    public void updateEquipment() {
	/*
	 * for (int i = 0; i < previousEquipment.length; i++) {
	 * net.minecraft.server.ItemStack previous = previousEquipment[i];
	 * net.minecraft.server.ItemStack current =
	 * ((EntityPlayer)getEntity()).getEquipment(i); if (previous != current)
	 * { NPCUtils.sendPacketNearby(getBukkitEntity().getLocation(), new
	 * Packet5EntityEquipment(getEntity().id, i, current));
	 * previousEquipment[i] = current; } }
	 */

	/**/
	int changes = 0;
	net.minecraft.server.v1_7_R1.ItemStack[] newI = new net.minecraft.server.v1_7_R1.ItemStack[previousEquipment.length];
	for (int i = 0; i < previousEquipment.length; i++) {
	    net.minecraft.server.v1_7_R1.ItemStack previous = previousEquipment[i];
	    net.minecraft.server.v1_7_R1.ItemStack current = ((EntityPlayer) getEntity()).getEquipment(i);
	    newI[i] = current;
	    if (current == null) {
		if (previous != null) {
		    NPCUtils.sendPacketNearby(getBukkitEntity().getLocation(), new PacketPlayOutEntityEquipment(
			    getEntity().getId(), i, current));
		    ++changes;
		}
	    } else {
		if (!net.minecraft.server.v1_7_R1.ItemStack.equals(previous, current)
			|| (previous != null && !previous.equals(current))) {
		    NPCUtils.sendPacketNearby(getBukkitEntity().getLocation(), new PacketPlayOutEntityEquipment(
			    getEntity().getId(), i, current));
		    ++changes;
		}
	    }
	}

	if (changes > 0) {
	    previousEquipment = newI;
	}
	/**/
    }

}
