package com.questionerx5.voistella;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

import com.questionerx5.voistella.component.feature.*;
import com.questionerx5.voistella.component.item.*;
import com.questionerx5.voistella.data.*;

import java.util.HashMap;

public class ActorFactory{
    private static HashMap<String, CreatureData> creatures;
    public static CreatureData creature(String name){
        initCreatures();
        return creatures.get(name);
    }
    public static CreatureData creatureNewInstance(String name){
        initCreatures();
        return new CreatureData(creatures.get(name));
    }
    private static void initCreatures(){
        if(creatures == null){
            creatures = new HashMap<>();
            creatures.put("player",
                new CreatureData('@', SColor.WHITE, "cursed monstrosity",
                ActionSupplier.WANDER, false,
                20, 2, 100).setAlly(true)
                .addSkill(new SkillData("heal", 7, 0, 0,
                    ActionSupplier.APPLY_EFFECT(new Effect(ActionSupplier.HEAL(5)))
                ))
                .addSkill(new SkillData("heal2", 0, 0, 5,
                    ActionSupplier.APPLY_EFFECT(new Effect(ActionSupplier.HEAL(1)))
                ))
                .addSkill(new SkillData("-1 max hp", 0, 0, 0, //TEMPORARY
                    ActionSupplier.APPLY_EFFECT(new Effect(e -> new com.questionerx5.voistella.action.Action(){
                        public com.questionerx5.voistella.action.ActionResult perform(){
                            if(e.creature() == null){
                                return new com.questionerx5.voistella.action.ActionResult(false);
                            }
                            e.creature().addMaxHealthModifier(new StatMod(StatMod.ModType.FLAT_INCREASE, -1));
                            return new com.questionerx5.voistella.action.ActionResult(true);
                        }
                    }))
                ))
            );
            creatures.put("fungus",
                new CreatureData('f', SColor.GREEN, "fungus",
                ActionSupplier.NOTHING, false,
                5, 0, -1)
            );
            creatures.put("bat",
                new CreatureData('b', SColor.BROWN, "bat",
                ActionSupplier.WANDER, false,
                2, 1, 200)
            );
            creatures.put("zombie",
                new CreatureData('Z', SColor.LIGHT_GRAY, "zombie",
                ActionSupplier.CHASE(ActionSupplier.NOTHING), true,
                6, 3, 50)
            );
            creatures.put("goblin",
                new CreatureData('g', SColor.GREEN, "goblin",
                ActionSupplier.RANGED_ATTACK(ActionSupplier.WANDER), false,
                4, 2, 100)
            );
            creatures.put("rogue",
                new CreatureData('@', SColor.BLUE, "rogue",
                ActionSupplier.CHASE(ActionSupplier.WANDER), true,
                1, 1, 100)
            );
            creatures.put("stealer",
                new CreatureData('@', SColor.BROWN, "stealer",
                ActionSupplier.EQUIP(ActionSupplier.PICKUP(ActionSupplier.CHASE(ActionSupplier.WANDER))), true,
                10, 0, 100)
            );
        }
    }

    private static HashMap<String, ItemData> items;
    public static ItemData item(String name){
        initItems();
        return items.get(name);
    }
    private static void initItems(){
        if(items == null){
            items = new HashMap<>();
            items.put("junk",
                new ItemData(',', SColor.BROWN, "junk")
            );
            items.put("sword",
                new ItemData(')', SColor.WHITE, "sword")
                .setEquippableComponent(new EquippableComponent(new Attack(6), EquipSlot.WEAPON))
            );
            items.put("bow",
                new ItemData(')', SColor.BROWN, "bow")
                .setEquippableComponent(new EquippableComponent(new Attack(2, 4.5), EquipSlot.WEAPON))
            );
            items.put("health potion",
                new ItemData('!', SColor.RED, "health potion")
                .setPotionComponent(new PotionComponent(new Effect(ActionSupplier.HEAL(10))))
            );
            items.put("regen potion",
                new ItemData('!', SColor.PINK, "regen potion")
                .setPotionComponent(new PotionComponent(new Effect(20, ActionSupplier.HEAL(1))))
            );
        }
    }

    public static Feature gameEnder(Level level, Coord pos){
        Feature feature = new Feature(level, pos, 'O', SColor.LIME, "victory podium");
        level.addFeature(feature);
        feature.winComponent = new WinComponent();
        return feature;
    }
    public static Feature stairsDown(Level level, Coord pos, Level destinationLevel, Coord destinationCoord){
        Feature feature = new Feature(level, pos, '>', SColor.WHITE, "stairs leading down");
        level.addFeature(feature);
        feature.levelChangeComponent = new LevelChangeComponent(destinationCoord, destinationLevel, false);
        return feature;
    }
    public static Feature stairsUp(Level level, Coord pos, Level destinationLevel, Coord destinationCoord){
        Feature feature = new Feature(level, pos, '<', SColor.WHITE, "stairs leading up");
        level.addFeature(feature);
        feature.levelChangeComponent = new LevelChangeComponent(destinationCoord, destinationLevel, true);
        return feature;
    }

    public static Item junk(Level level, Coord pos){
        ItemData item = new ItemData(',', SColor.BROWN, "junk");
        return item.create(level, pos);
    }
}