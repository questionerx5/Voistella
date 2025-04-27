package com.questionerx5.voistella;

import com.questionerx5.voistella.action.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.ArrayList;

import squidpony.squidgrid.Radius;
import squidpony.squidmath.Coord;
import squidpony.squidmath.DDALine;

@FunctionalInterface
public interface ActionSupplier<T extends Actor>{
    ActionSupplier<Actor> BLANK = a -> null;
    ActionSupplier<Actor> NOTHING = a -> new WaitAction();
    
    ActionSupplier<Creature> WANDER = c -> {
        int x = RNGVars.aiRNG.between(-1, 2);
        int y = RNGVars.aiRNG.between(-1, 2);
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
            List<Coord> targets = new ArrayList<>();
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
            List<Coord> path = c.pathTo(targets);
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
            List<Coord> targets = new ArrayList<>();
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
            List<Coord> path = c.pathAway(targets);
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
            List<Creature> candidates = new ArrayList<>();
            for(Creature creature : c.level().creatures()){
                if((c.isAlly() == creature.isAlly()) || Radius.CIRCLE.radius(c.pos(), creature.pos()) > c.attack().range){
                    continue;
                }
                boolean blocked = false;
                for(Coord point : DDALine.line(c.pos(), creature.pos())){
                    if(creature.level().tile(point).testFlag(Tile.TileFlag.BLOCKING)){
                        blocked = true;
                        break;
                    }
                }
                if(!blocked){
                    candidates.add(creature);
                }
            }
            if(candidates.size() == 0){
                return defaultAction.getAction(c);
            }

            return new AttackAction(RNGVars.aiRNG.getRandomElement(candidates));
        };
    }
    static ActionSupplier<Creature> PICKUP(final ActionSupplier<? super Creature> defaultAction){
        return c -> {
            if(c.inventory().isFull()){
                return defaultAction.getAction(c);
            }
            List<Item> items = c.level().itemsAt(c.pos());
            if(items.isEmpty()){
                return defaultAction.getAction(c);
            }
            return new PickupAction(RNGVars.aiRNG.getRandomElement(items));
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
    static ActionSupplier<Creature> CREATURE_CHECK(BiPredicate<Creature, Creature> pred, int threshold, final ActionSupplier<? super Creature> aboveAction, final ActionSupplier<? super Creature> belowAction){
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

    static BiFunction<Creature, Coord, Action> APPLY_EFFECT(Effect effect){
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