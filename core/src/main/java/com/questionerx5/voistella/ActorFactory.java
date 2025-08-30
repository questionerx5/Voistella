package com.questionerx5.voistella;

import com.badlogic.gdx.graphics.Color;
import com.github.tommyettinger.ds.ObjectObjectMap;
import com.github.yellowstonegames.grid.Coord;
import com.questionerx5.voistella.component.feature.*;
import com.questionerx5.voistella.component.item.*;
import com.questionerx5.voistella.data.*;

public class ActorFactory{
    private static ObjectObjectMap<String, CreatureData> creatures;
    public static CreatureData creature(String id){
        initCreatures();
        CreatureData result = creatures.get(id);
        if(result == null){
            throw new RuntimeException("No creature with id " + id);
        }
        return result;
    }
    public static CreatureData creatureNewInstance(String id){
        return new CreatureData(creature(id));
    }
    private static void initCreatures(){
        if(creatures == null){
            creatures = new ObjectObjectMap<>();
            creatures.put("player",
                new CreatureData('@', Color.WHITE, "cursed monstrosity",
                ActionSupplier.WANDER, false,
                20, 2, 100).setAlly(true)
                .addSkill(new SkillData("heal", 7, 0, 0,
                    ActionSupplier.APPLY_EFFECT(new Effect(ActionSupplier.HEAL(5)))
                ))
                .addSkill(new SkillData("heal2", 0, 0, 5,
                    ActionSupplier.APPLY_EFFECT(new Effect(ActionSupplier.HEAL(1)))
                ))
                .addSkill(new SkillData("temp hp boost", 0, 0, 0,
                    ActionSupplier.APPLY_EFFECT(new StatEffect(50, Creature::maxHealthStat, new StatMod(StatMod.ModType.MULT, 1.5)))
                ))
            );
            creatures.put("test:fungus",
                new CreatureData('f', Color.GREEN, "fungus",
                ActionSupplier.NOTHING, false,
                5, 0, -1)
            );
            creatures.put("test:bat",
                new CreatureData('b', Color.BROWN, "bat",
                ActionSupplier.WANDER, false,
                2, 1, 200)
            );
            creatures.put("test:zombie",
                new CreatureData('Z', Color.LIGHT_GRAY, "zombie",
                ActionSupplier.CHASE(ActionSupplier.NOTHING), true,
                6, 3, 50)
            );
            creatures.put("test:goblin",
                new CreatureData('g', Color.GREEN, "goblin",
                ActionSupplier.RANGED_ATTACK(ActionSupplier.WANDER), false,
                4, 2, 100)
            );
            creatures.put("test:rogue",
                new CreatureData('@', Color.BLUE, "rogue",
                ActionSupplier.CHASE(ActionSupplier.WANDER), true,
                1, 1, 200)
            );
            creatures.put("test:stealer",
                new CreatureData('@', Color.BROWN, "stealer",
                ActionSupplier.EQUIP(ActionSupplier.PICKUP(ActionSupplier.CHASE(ActionSupplier.WANDER))), true,
                10, 0, 100)
            );
            creatures.put("test:scaredy cat",
                new CreatureData('c', Color.ORANGE, "scaredy cat",
                ActionSupplier.HP_CHECK(1,
                    ActionSupplier.FLEE(ActionSupplier.NOTHING),
                    ActionSupplier.CHASE(ActionSupplier.WANDER)),
                true,
                3, 2, 200)
            );
            creatures.put("test:helper",
                new CreatureData('@', Color.SKY, "helper",
                ActionSupplier.HP_CHECK(0.3,
                    ActionSupplier.FLEE(ActionSupplier.RANGED_ATTACK(ActionSupplier.NOTHING)),
                    ActionSupplier.CHASE(ActionSupplier.WANDER)),
                true,
                20, 2, 100)
            );
        }
    }

    private static ObjectObjectMap<String, ItemData> items;
    public static ItemData item(String name){
        initItems();
        return items.get(name);
    }
    private static void initItems(){
        if(items == null){
            items = new ObjectObjectMap<>();
            items.put("junk",
                new ItemData(',', Color.BROWN, "junk")
            );
            items.put("sword",
                new ItemData(')', Color.WHITE, "sword")
                .setEquippableComponent(new EquippableComponent(new Attack(6), EquipSlot.WEAPON))
            );
            items.put("bow",
                new ItemData(')', Color.BROWN, "bow")
                .setEquippableComponent(new EquippableComponent(new Attack(2, 4.5), EquipSlot.WEAPON))
            );
            items.put("health potion",
                new ItemData('!', Color.RED, "health potion")
                .setPotionComponent(new PotionComponent(new Effect(ActionSupplier.HEAL(10))))
            );
            items.put("regen potion",
                new ItemData('!', Color.PINK, "regen potion")
                .setPotionComponent(new PotionComponent(new Effect(20, ActionSupplier.HEAL(1))))
            );
            items.put("delayed health potion",
                new ItemData('!', Color.SALMON, "delayed health potion")
                .setPotionComponent(new PotionComponent(new Effect(10, null, ActionSupplier.HEAL(20))))
            );
        }
    }

    public static Feature gameEnder(Level level, Coord pos){
        Feature feature = new Feature(level, pos, 'O', Color.LIME, "victory podium");
        level.addFeature(feature);
        feature.winComponent = new WinComponent();
        return feature;
    }
    public static Feature stairsDown(Level level, Coord pos, Level destinationLevel, Coord destinationCoord){
        Feature feature = new Feature(level, pos, '>', Color.WHITE, "stairs leading down");
        level.addFeature(feature);
        feature.levelChangeComponent = new LevelChangeComponent(destinationCoord, destinationLevel, false);
        return feature;
    }
    public static Feature stairsUp(Level level, Coord pos, Level destinationLevel, Coord destinationCoord){
        Feature feature = new Feature(level, pos, '<', Color.WHITE, "stairs leading up");
        level.addFeature(feature);
        feature.levelChangeComponent = new LevelChangeComponent(destinationCoord, destinationLevel, true);
        return feature;
    }

    public static Item junk(Level level, Coord pos){
        ItemData item = new ItemData(',', Color.BROWN, "junk");
        return item.create(level, pos);
    }
}