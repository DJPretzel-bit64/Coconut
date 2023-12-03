package engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Physics {
    public List<Entity> entities;

    public Physics(List<Entity> entities) {
        this.entities = entities;
    }

    public void update() {
        for(Entity entity : entities) {
            Vec2 previousPos = new Vec2(entity.getPos().x, entity.getPos().y);

            entity.getPos().x += entity.getVelocity().x;
            for(Hitbox hitbox : entity.getHitboxes())
                hitbox.getPos().x += entity.getVelocity().x;

            entity.setLastCollision(new ArrayList<>());

            for(Entity collisionEntity : entities) {
                if(!Objects.equals(entity, collisionEntity)) {
                    for(Hitbox hitbox : entity.getHitboxes()) {
                        for(Hitbox collisionHitbox : collisionEntity.getHitboxes()) {
                            if(hitbox.intersects(collisionHitbox)) {
                                if(entity.getCollidesWith().contains(collisionEntity.getName())) {
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

            entity.getPos().y += entity.getVelocity().y;
            for(Hitbox hitbox : entity.getHitboxes())
                hitbox.getPos().y += entity.getVelocity().y;

            for(Entity collisionEntity : entities) {
                if(!Objects.equals(entity, collisionEntity) && entity.getCollidesWith().contains(collisionEntity.getName())) {
                    for(Hitbox hitbox : entity.getHitboxes()) {
                        for(Hitbox collisionHitbox : collisionEntity.getHitboxes()) {
                            if(hitbox.intersects(collisionHitbox)) {
                                if(entity.getCollidesWith().contains(collisionEntity.getName())) {
                                    entity.getPos().y = previousPos.y;
                                    for (Hitbox revert : entity.getHitboxes()) {
                                        revert.getPos().y -= entity.getVelocity().y;
                                    }
                                }
                                entity.getVelocity().y = 0;
                            }
//                            System.out.println(collisionEntity.getName());
                            entity.getLastCollision().add(collisionEntity);
                        }
                    }
                }
            }
        }
    }
}
