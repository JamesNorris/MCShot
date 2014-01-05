package com.github.jamesnorris.mcshot.type;

import org.bukkit.entity.Entity;

import com.github.jamesnorris.mcshot.HitBox;
import com.github.jamesnorris.mcshot.util.Location;

public class EntityHitBox extends HitBox {
    private Entity entity;

    public EntityHitBox(Entity entity, double length, double width, double height) {
        super((Location) entity.getLocation().clone().add(0, height / 2, 0), length, width, height, entity.getLocation().getYaw());
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override public void update() {
        Location location = (Location) entity.getLocation().clone().add(0, Math.abs(getFrom().getY()
                - getTo().getY()) / 2, 0);
        moveTo(location.toVector());
        rotate((float) (location.getYaw() - getRotationY()));
    }
}
