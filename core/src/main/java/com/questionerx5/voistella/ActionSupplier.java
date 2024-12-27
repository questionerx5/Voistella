package com.questionerx5.voistella;

import com.questionerx5.voistella.action.*;
import java.util.List;
import java.util.function.BiFunction;
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
            c.rescanMap();
            Coord target = null;
            int leastDistance = Integer.MAX_VALUE;
            List<Coord> candidates = new ArrayList<>();
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
                        candidates.add(c.memEntities().get(creature));
                    }
                }
            }
            else{
                // Use visible creatures.
                for(Creature creature : c.level().creatures()){
                    if((c.isAlly() != creature.isAlly()) && c.canSee(creature.pos().x, creature.pos().y)){
                        candidates.add(creature.pos());
                    }
                }
            }
            RNGVars.aiRNG.shuffle(candidates);
            for(Coord candidate : candidates){
                List<Coord> path = c.pathTo(candidate);
                if(path == null){
                    continue;
                }
                if(!path.isEmpty() && path.size() < leastDistance){
                    leastDistance = path.size();
                    target = path.get(0);
                }
            }
            if(target == null){
                return defaultAction.getAction(c);
            }
            Creature other = c.level().creatureAt(target);
            if(other != null && c.isAlly() == other.isAlly()){
                return new WaitAction();
            }
            return new MoveAction(target.x - c.pos().x, target.y - c.pos().y);
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