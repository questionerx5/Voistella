//TODO: New library is untested
package com.questionerx5.voistella;

import com.github.tommyettinger.ds.ObjectDeque;
import com.github.tommyettinger.ds.ObjectList;
import com.github.tommyettinger.function.ObjObjPredicate;
import com.github.tommyettinger.function.ObjObjToObjBiFunction;
import com.github.yellowstonegames.grid.BresenhamLine;
import com.github.yellowstonegames.grid.Coord;
import com.github.yellowstonegames.grid.Radius;
import com.questionerx5.voistella.action.*;

@FunctionalInterface
public interface ActionSupplier<T extends Actor>{
    ActionSupplier<Actor> BLANK = a -> null;
    ActionSupplier<Actor> NOTHING = a -> new WaitAction();
    
    ActionSupplier<Creature> WANDER = c -> {
        int x = RNGVars.aiRNG.nextInt(-1, 2);
        int y = RNGVars.aiRNG.nextInt(-1, 2);
        if(x == 0 && y == 0){
            return new WaitAction();
        }
        if(c.level().tile(c.pos().x + x, c.pos().y + y).testFlag(Tile.TileFlag.BLOCKING)){
            return new WaitAction();
        }
        Creature other = c.level().creatureAt(c.pos().translate(x, y));
        if(other != null && c.isAlly() == other.isAlly()){
            return new WaitAction();
        }
        return new MoveAction(x, y);
    };
    static ActionSupplier<Creature> CHASE(final ActionSupplier<? super Creature> defaultAction){
        return c -> {
            ObjectList<Coord> targets = new ObjectList<>();
            if(c.tracksEntities()){
                // Check memories.
                // Iterate in level creature order to ensure determinism.
                //TODO: maybe should target last known position instead
                c.getVisible();
                for(Creature creature : c.level().creatures()){
                    if(!c.memEntities().containsKey(creature)){
                        continue;
                    }
                    if(c.isAlly() != creature.isAlly()){
                        targets.add(c.memEntities().get(creature));
                    }
                }
            }
            else{
                // Use visible creatures.
                for(Creature creature : c.level().creatures()){
                    if((c.isAlly() != creature.isAlly()) && c.canSee(creature.pos().x, creature.pos().y)){
                        targets.add(creature.pos());
                    }
                }
            }
            ObjectDeque<Coord> path = c.pathTo(targets);
            if(path == null || path.isEmpty()){
                return defaultAction.getAction(c);
            }
            Creature other = c.level().creatureAt(path.get(0));
            if(other != null && c.isAlly() == other.isAlly()){
                return new WaitAction();
            }
            return new MoveAction(path.get(0).x - c.pos().x, path.get(0).y - c.pos().y);
        };
    }
    static ActionSupplier<Creature> FLEE(final ActionSupplier<? super Creature> defaultAction){
        return c -> {
            ObjectList<Coord> targets = new ObjectList<>();
            if(c.tracksEntities()){
                // Check memories.
                // Iterate in level creature order to ensure determinism.
                //TODO: maybe should target last known position instead
                c.getVisible();
                for(Creature creature : c.level().creatures()){
                    if(!c.memEntities().containsKey(creature)){
                        continue;
                    }
                    if(c.isAlly() != creature.isAlly()){
                        targets.add(c.memEntities().get(creature));
                    }
                }
            }
            else{
                // Use visible creatures.
                for(Creature creature : c.level().creatures()){
                    if((c.isAlly() != creature.isAlly()) && c.canSee(creature.pos().x, creature.pos().y)){
                        targets.add(creature.pos());
                    }
                }
            }
            ObjectDeque<Coord> path = c.pathAway(targets);
            //System.out.println("Path:" + path);
            if(path == null || path.isEmpty()){
                return defaultAction.getAction(c);
            }
            Creature other = c.level().creatureAt(path.get(0));
            if(other != null && c.isAlly() == other.isAlly()){
                return new WaitAction();
            }
            return new MoveAction(path.get(0).x - c.pos().x, path.get(0).y - c.pos().y);
        };
    }
    static ActionSupplier<Creature> RANGED_ATTACK(final ActionSupplier<? super Creature> defaultAction){
        return c -> {
            ObjectList<Creature> candidates = new ObjectList<>();
            for(Creature creature : c.level().creatures()){
                if((c.isAlly() == creature.isAlly()) || Radius.CIRCLE.radius(c.pos(), creature.pos()) > c.attack().range){
                    continue;
                }
                boolean blocked = false;
                //TODO: i don't trust this to match LOS
                //BresenhamLine.reachable(null, null, null)
                for(Coord point : BresenhamLine.line(c.pos(), creature.pos())){
                    if(creature.level().tile(point).testFlag(Tile.TileFlag.BLOCKING)){
                        blocked = true;
                        break;
                    }
                }
                if(!blocked){
                    candidates.add(creature);
                }
            }
            if(candidates.isEmpty()){
                return defaultAction.getAction(c);
            }

            return new AttackAction(RNGVars.aiRNG.randomElement(candidates));
        };
    }
    static ActionSupplier<Creature> PICKUP(final ActionSupplier<? super Creature> defaultAction){
        return c -> {
            if(c.inventory().isFull()){
                return defaultAction.getAction(c);
            }
            ObjectList<Item> items = c.level().itemsAt(c.pos());
            if(items.isEmpty()){
                return defaultAction.getAction(c);
            }
            return new PickupAction(RNGVars.aiRNG.randomElement(items));
        };
    }
    static ActionSupplier<Creature> EQUIP(final ActionSupplier<? super Creature> defaultAction){
        return c -> {
            double bestRating = c.attack().damage * c.attack().range;
            Item best = null;
            for(Item item : c.inventory().items()){
                if(item == null || item.equippableComponent == null){
                    continue;
                }
                double rating = item.equippableComponent.attack.damage * item.equippableComponent.attack.range;
                if(rating > bestRating){
                    bestRating = rating;
                    best = item;
                }
            }
            if(best == null){
                return defaultAction.getAction(c);
            }
            return new EquipAction(best);
        };
    }

    // Returns aboveAction if hp/max >= threshold and belowAction otherwise
    static ActionSupplier<Creature> HP_CHECK(double threshold, final ActionSupplier<? super Creature> aboveAction, final ActionSupplier<? super Creature> belowAction){
        return c -> {
            if(c.health() >= c.maxHealth() * threshold){
                return aboveAction.getAction(c);
            }
            else{
                return belowAction.getAction(c);
            }
        };
    }
    // Returns aboveAction if (# of nearby creatures satisfying pred) >= threshold, belowAction otherwise
    static ActionSupplier<Creature> CREATURE_CHECK(ObjObjPredicate<Creature, Creature> pred, int threshold, final ActionSupplier<? super Creature> aboveAction, final ActionSupplier<? super Creature> belowAction){
        return c -> {
            int count = 0;
            for(Creature creature : c.level().creatures()){
                if(creature.pos.x - c.pos.x >= -5 && creature.pos.x - c.pos.x <= 5 &&
                creature.pos.y - c.pos.y >= -5 && creature.pos.y - c.pos.y <= 5 &&
                creature != c && c.canSee(creature.pos.x, creature.pos.y) && pred.test(c, creature)){
                    count++;
                }
            }
            if(count >= threshold){
                return aboveAction.getAction(c);
            }
            else{
                return belowAction.getAction(c);
            }
        };
    }

    static ActionSupplier<Effect> HEAL(int amount){
        return e -> new HealAction(e.creature(), amount);
    }

    static ObjObjToObjBiFunction<Creature, Coord, Action> APPLY_EFFECT(Effect effect){
        return (c, pos) -> {
            Creature target = c.level().creatureAt(pos);
            if(target == null){
                c.messageError("There's nothing there to affect.");
                return null;
            }
            return new EffectAction(target, new Effect(effect));
        };
    }

    public Action getAction(T actor);
}