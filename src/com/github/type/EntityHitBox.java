package com.github.type;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.github.HitBox;

public class EntityHitBox extends HitBox {
    private Entity entity;
    private double height = -1;

    public EntityHitBox(Entity entity, double length, double width, double height) {
        super(entity.getLocation().clone().add(0, height / 2, 0), length, width, height, entity.getLocation().getYaw());
        this.entity = entity;
        this.height = height;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    @Override public void update() {
        Location location = entity.getLocation().clone().add(0, height / 2, 0);
        move(location);
        rotate(location.getYaw() - getYawRotation());
    }    
}
