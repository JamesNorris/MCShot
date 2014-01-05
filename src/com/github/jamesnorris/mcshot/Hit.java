package com.github.jamesnorris.mcshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.jamesnorris.mcshot.util.LineSegment3D;
import com.github.jamesnorris.mcshot.util.Location;

public class Hit {
    private Location from, entrance, exit, to;
    private ShotData projectileType;
    private HitBox boxHit;
    private LineSegment3D directTrajectory;
    private List<Zone> zonesHit = new ArrayList<Zone>();
    private double damage, penetration;

    public Hit(Location from, Location to, Location entrance, Location exit, ShotData projectileType, HitBox boxHit) {
        this.from = from;
        this.to = to;
        this.entrance = entrance;
        this.exit = exit;
        this.projectileType = projectileType;
        this.boxHit = boxHit;
        directTrajectory = new LineSegment3D(from, to);
        for (Zone zone : boxHit.getZones()) {
            if (!zone.getIntersects(directTrajectory).isEmpty()) {
                zonesHit.add(zone);
            }
        }
        // sort closest->farthest
        Zone[] zones = new Zone[zonesHit.size()];
        quicksort(zonesHit.toArray(zones), 0, zonesHit.size());
        zonesHit = Arrays.asList(zones);
        double distance = entrance.distance(from);
        double penetrationDistance = distance;
        damage = projectileType.getDamage(distance);
        for (Zone zone : zonesHit) {
            if (!(zone instanceof DataZone)) {
                continue;
            }
            if (projectileType.getPenetration(penetrationDistance) <= 0) {
                break;
            }
            DataZone data = (DataZone) zone;
            damage *= data.multiplier;
            penetrationDistance *= data.distanceMultiplier;
        }
        penetration = projectileType.getPenetration(penetrationDistance);
    }

    public double getDamage() {
        return damage;
    }

    public LineSegment3D getDirectTrajectory() {
        return directTrajectory;
    }

    public Location getEntrance() {
        return entrance;
    }

    public Location getExit() {
        return exit;
    }

    public Location getFrom() {
        return from;
    }

    public List<Zone> getHitZones() {
        return zonesHit;
    }

    public double getPenetration() {
        return penetration;
    }

    public ShotData getProjectileType() {
        return projectileType;
    }

    public Location getTo() {
        return to;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setPenetration(double penetration) {
        this.penetration = penetration;
    }

    @Override public String toString() {
        return "Hit:(entrance: " + entrance + ", exit: " + exit + ", boxHit: "
                + boxHit + ")";
    }

    private int partition(Zone[] zones, int left, int right, int pivot) {
        double pivotVal = zones[pivot].getCenter().toLocation(boxHit.getWorld()).distance(from);
        swap(zones, pivot, right);
        int store = left;
        for (int i = left; i < right; i++) {
            if (zones[i].getCenter().toLocation(boxHit.getWorld()).distanceSquared(from) < pivotVal) {
                swap(zones, i, store);
                store++;
            }
        }
        swap(zones, store, right);
        return store;
    }

    private void quicksort(Zone[] zones, int left, int right) {
        if (zones.length >= 2 && left < right) {
            int pivot = /* median */5;
            int newPivot = partition(zones, left, right, pivot);
            quicksort(zones, left, newPivot - 1);
            quicksort(zones, newPivot + 1, right);
        }
    }

    private void swap(Zone[] zones, int index1, int index2) {
        Zone temp = zones[index1];
        zones[index1] = zones[index2];
        zones[index1] = temp;
    }
}
