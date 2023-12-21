package Coconut;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Physics(List<Entity> entities) {
    public void update() {
        for (Entity entity : entities) {
            // clear the lastCollision list
            entity.setLastCollision(new ArrayList<>());

            checkCollisionX(entity);
            checkCollisionY(entity);
        }
    }

    public void checkCollisionX(Entity entity) {
        // store the current position for use later
        Vec2 previousPos = new Vec2(entity.getPos().x, entity.getPos().y);

        // apply the entities velocities in the x before checking collisions
        entity.getPos().x += entity.getVelocity().x;
        for (Hitbox hitbox : entity.getHitboxes())
            hitbox.getPos().x += entity.getVelocity().x;

        for (Entity collisionEntity : entities) {
            if (!Objects.equals(entity, collisionEntity)) {
                for (Hitbox hitbox : entity.getHitboxes()) {
                    for (Hitbox collisionHitbox : collisionEntity.getHitboxes()) {
                        if (hitbox.intersects(collisionHitbox)) {
                            if(entity.getCollidesWith().contains(collisionEntity.getName())) {
                                // for all the hitboxes in each entity, check if they collide in the x and revert changes if they do
                                if(entity.getMass() != -1 && collisionEntity.getMass() != -1) {
                                    collisionEntity.getVelocity().x = entity.getVelocity().x * entity.getMass() / collisionEntity.getMass();
                                    checkCollisionX(collisionEntity);
                                    if(!(collisionEntity.getVelocity().x == 0)) {
                                        entity.getPos().x -= entity.getVelocity().x * entity.getMass() / collisionEntity.getMass();
                                        for (Hitbox revert : entity.getHitboxes()) {
                                            revert.getPos().x -= entity.getVelocity().x * entity.getMass() / collisionEntity.getMass();
                                        }
                                        collisionEntity.getVelocity().x = 0;
                                    }
                                    else {
                                        entity.getPos().x = previousPos.x;
                                        for (Hitbox revert : entity.getHitboxes()) {
                                            revert.getPos().x -= entity.getVelocity().x;
                                        }
                                    }
                                }
                                else {
                                    entity.getPos().x = previousPos.x;
                                    for (Hitbox revert : entity.getHitboxes()) {
                                        revert.getPos().x -= entity.getVelocity().x;
                                    }
                                }
                                entity.getVelocity().x = 0;
                            }
                            entity.getLastCollision().add(collisionEntity);
                        }
                    }
                }
            }
        }
    }

    public void checkCollisionY(Entity entity) {
        // store the current position for use later
        Vec2 previousPos = new Vec2(entity.getPos().x, entity.getPos().y);

        // apply the entities velocities in the x before checking collisions
        entity.getPos().y += entity.getVelocity().y;
        for (Hitbox hitbox : entity.getHitboxes())
            hitbox.getPos().y += entity.getVelocity().y;

        for (Entity collisionEntity : entities) {
            if (!Objects.equals(entity, collisionEntity)) {
                for (Hitbox hitbox : entity.getHitboxes()) {
                    for (Hitbox collisionHitbox : collisionEntity.getHitboxes()) {
                        if (hitbox.intersects(collisionHitbox)) {
                            if(entity.getCollidesWith().contains(collisionEntity.getName())) {
                                // for all the hitboxes in each entity, check if they collide in the x and revert changes if they do
                                if(entity.getMass() != -1 && collisionEntity.getMass() != -1) {
                                    collisionEntity.getVelocity().y = entity.getVelocity().y * entity.getMass() / collisionEntity.getMass();
                                    checkCollisionY(collisionEntity);
                                    if(!(collisionEntity.getVelocity().y == 0)) {
                                        entity.getPos().y -= entity.getVelocity().y * entity.getMass() / collisionEntity.getMass();
                                        for (Hitbox revert : entity.getHitboxes()) {
                                            revert.getPos().y -= entity.getVelocity().y * entity.getMass() / collisionEntity.getMass();
                                        }
                                        collisionEntity.getVelocity().y = 0;
                                    }
                                    else {
                                        entity.getPos().y = previousPos.y;
                                        for (Hitbox revert : entity.getHitboxes()) {
                                            revert.getPos().y -= entity.getVelocity().y;
                                        }
                                    }
                                }
                                else {
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
