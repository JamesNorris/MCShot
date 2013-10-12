package com.github.type;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.github.HitBox;

public class EntityHitBox extends HitBox {
    private Entity entity;

    public EntityHitBox(Entity entity, Location origin, Location center, float yawRotation) {
        super(origin, center, yawRotation);
        this.entity = entity;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    @Override public void update() {
        move(entity.getLocation());
        rotate(entity.getLocation().getYaw() - getYawRotation());
    }    
}
