package com.sharesc.caliog.npclib;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.server.v1_7_R1.EntityPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class NPC {
    private final net.minecraft.server.v1_7_R1.Entity entity;
    private NPCPathFinder path;
    private Iterator<Node> pathIterator;
    private Node last;
    private NPCPath runningPath;
    private int taskid;
    private Runnable onFail;

    public NPC(net.minecraft.server.v1_7_R1.Entity entity) {
	this.entity = entity;
    }

    public net.minecraft.server.v1_7_R1.Entity getEntity() {
	return this.entity;
    }

    public void removeFromWorld() {
	try {
	    this.entity.world.removeEntity(this.entity);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public org.bukkit.entity.Entity getBukkitEntity() {
	return this.entity.getBukkitEntity();
    }

    public void moveTo(Location l) {
	getBukkitEntity().teleport(l);
    }

    public void pathFindTo(Location l, PathReturn callback) {
	pathFindTo(l, 3000, callback);
    }

    public void pathFindTo(Location l, int maxIterations, PathReturn callback) {
	if (this.path != null) {
	    this.path.cancel = true;
	}
	if (l.getWorld() != getBukkitEntity().getWorld()) {
	    ArrayList<Node> pathList = new ArrayList<Node>();
	    pathList.add(new Node(l.getBlock()));
	    callback.run(new NPCPath(null, pathList, l));
	} else {
	    this.path = new NPCPathFinder(getBukkitEntity().getLocation(), l, maxIterations, callback);
	    Bukkit.getScheduler().scheduleSyncDelayedTask(NPCManager.plugin, this.path);
	}
    }

    public void walkTo(Location l) {
	walkTo(l, 3000);
    }

    public void walkTo(final Location l, final int maxIterations) {
	pathFindTo(l, maxIterations, new PathReturn() {
	    public void run(NPCPath path) {
		NPC.this.usePath(path, new Runnable() {
		    public void run() {
			NPC.this.walkTo(l, maxIterations);
		    }
		});
	    }
	});
    }

    public void usePath(NPCPath path) {
	usePath(path, new Runnable() {
	    public void run() {
		NPC.this.walkTo(NPC.this.runningPath.getEnd(), 3000);
	    }
	});
    }

    public void usePath(NPCPath path, Runnable onFail) {
	if (this.taskid == 0) {
	    this.taskid = Bukkit.getServer().getScheduler()
		    .scheduleSyncRepeatingTask(NPCManager.plugin, new Runnable() {
			public void run() {
			    NPC.this.pathStep();
			}
		    }, 8L, 8L);
	}
	this.pathIterator = path.getPath().iterator();
	this.runningPath = path;
	this.onFail = onFail;
    }

    private void pathStep() {
	if (this.pathIterator.hasNext()) {
	    Node n = (Node) this.pathIterator.next();
	    if (n.b.getWorld() != getBukkitEntity().getWorld()) {
		getBukkitEntity().teleport(n.b.getLocation());
	    } else {
		float angle = getEntity().yaw;
		float look = getEntity().pitch;
		if ((this.last == null) || (this.runningPath.checkPath(n, this.last, true))) {
		    if (this.last != null) {
			angle = (float) Math.toDegrees(Math.atan2(this.last.b.getX() - n.b.getX(), n.b.getZ()
				- this.last.b.getZ()));
			look = (float) (Math.toDegrees(Math.asin(this.last.b.getY() - n.b.getY())) / 2.0D);
		    }
		    getEntity().setPositionRotation(n.b.getX() + 0.5D, n.b.getY(), n.b.getZ() + 0.5D, angle, look);
		    setYaw(angle);
		} else {
		    this.onFail.run();
		}
	    }
	    this.last = n;
	} else {
	    getEntity().setPositionRotation(this.runningPath.getEnd().getX(), this.runningPath.getEnd().getY(),
		    this.runningPath.getEnd().getZ(), this.runningPath.getEnd().getYaw(),
		    this.runningPath.getEnd().getPitch());
	    setYaw(this.runningPath.getEnd().getYaw());
	    Bukkit.getServer().getScheduler().cancelTask(this.taskid);
	    this.taskid = 0;
	}
    }

    public void setYaw(float yaw) {
	getEntity().yaw = yaw;
	((EntityPlayer) getEntity()).aP = yaw;
    }
}
