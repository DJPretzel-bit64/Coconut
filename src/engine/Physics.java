package engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Physics(List<Entity> entities) {
    public void update() {
        for (Entity entity : entities) {
            // store the current position for use later
            Vec2 previousPos = new Vec2(entity.getPos().x, entity.getPos().y);

            // apply the entities velocities in the x before checking collisions
            entity.getPos().x += entity.getVelocity().x;
            for (Hitbox hitbox : entity.getHitboxes())
                hitbox.getPos().x += entity.getVelocity().x;

            // clear the lastCollision list
            entity.setLastCollision(new ArrayList<>());

            for (Entity collisionEntity : entities) {
                if (!Objects.equals(entity, collisionEntity)) {
                    for (Hitbox hitbox : entity.getHitboxes()) {
                        for (Hitbox collisionHitbox : collisionEntity.getHitboxes()) {
                            if (hitbox.intersects(collisionHitbox)) {
                                if (entity.getCollidesWith().contains(collisionEntity.getName())) {
                                    // for all the hitboxes in each entity, check if they collide in the x and revert changes if they do
                                    entity.getPos().x = previousPos.x;
                                    for (Hitbox revert : entity.getHitboxes()) {
                                        revert.getPos().x -= entity.getVelocity().x;
                                    }
                                    entity.getVelocity().x = 0;
                                }
                                entity.getLastCollision().add(collisionEntity);
                            }
                        }
                    }
                }
            }

            // apply the entities velocity in the y before checking collisions
            entity.getPos().y += entity.getVelocity().y;
            for (Hitbox hitbox : entity.getHitboxes())
                hitbox.getPos().y += entity.getVelocity().y;

            for (Entity collisionEntity : entities) {
                if (!Objects.equals(entity, collisionEntity) && entity.getCollidesWith().contains(collisionEntity.getName())) {
                    for (Hitbox hitbox : entity.getHitboxes()) {
                        for (Hitbox collisionHitbox : collisionEntity.getHitboxes()) {
                            if (hitbox.intersects(collisionHitbox)) {
                                if (entity.getCollidesWith().contains(collisionEntity.getName())) {
                                    // for all the hitboxes in each entity, check if they collide in the y and revert changes if they do
                                    entity.getPos().y = previousPos.y;
                                    for (Hitbox revert : entity.getHitboxes()) {
                                        revert.getPos().y -= entity.getVelocity().y;
                                    }
                                }
                                entity.getVelocity().y = 0;
                            }
                            entity.getLastCollision().add(collisionEntity);
                        }
                    }
                }
            }
        }
    }
}
