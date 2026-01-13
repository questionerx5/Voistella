package com.questionerx5.voistella;

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
    public static CreatureData creatureCopy(String id){
        return new CreatureData(creature(id));
    }
    private static void initCreatures(){
        if(creatures == null){
            creatures = new ObjectObjectMap<>();
            creatures.put("player",
                new CreatureData('@', Palette.WHITE, "cursed monstrosity",
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
                new CreatureData('f', Palette.GREEN, "fungus",
                ActionSupplier.NOTHING, false,
                5, 0, -1)
            );
            creatures.put("test:bat",
                new CreatureData('b', Palette.BROWN, "bat",
                ActionSupplier.WANDER, false,
                2, 1, 200)
            );
            creatures.put("test:zombie",
                new CreatureData('Z', Palette.LIGHT_GREY, "zombie",
                ActionSupplier.CHASE(ActionSupplier.NOTHING), true,
                6, 3, 50)
            );
            creatures.put("test:goblin",
                new CreatureData('g', Palette.GREEN, "goblin",
                ActionSupplier.RANGED_ATTACK(ActionSupplier.WANDER), false,
                4, 2, 100)
            );
            creatures.put("test:rogue",
                new CreatureData('@', Palette.BLUE, "rogue",
                ActionSupplier.CHASE(ActionSupplier.WANDER), true,
                1, 1, 200)
            );
            creatures.put("test:stealer",
                new CreatureData('@', Palette.BROWN, "stealer",
                ActionSupplier.EQUIP(ActionSupplier.PICKUP(ActionSupplier.CHASE(ActionSupplier.WANDER))), true,
                10, 0, 100)
            );
            creatures.put("test:scaredy_cat",
                new CreatureData('c', Palette.ORANGE, "scaredy cat",
                ActionSupplier.HP_CHECK(1,
                    ActionSupplier.FLEE(ActionSupplier.NOTHING),
                    ActionSupplier.CHASE(ActionSupplier.WANDER)),
                true,
                3, 2, 200)
            );
            creatures.put("test:helper",
                new CreatureData('@', Palette.SKY, "helper",
                ActionSupplier.HP_CHECK(0.3,
                    ActionSupplier.RANGED_ATTACK(ActionSupplier.CHASE(ActionSupplier.WANDER)),
                    ActionSupplier.FLEE(ActionSupplier.RANGED_ATTACK(ActionSupplier.NOTHING))),
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
                new ItemData(',', Palette.BROWN, "junk")
            );
            items.put("sword",
                new ItemData(')', Palette.WHITE, "sword")
                .setEquippableComponent(new EquippableComponent(new Attack(6), EquipSlot.WEAPON))
            );
            items.put("bow",
                new ItemData(')', Palette.BROWN, "bow")
                .setEquippableComponent(new EquippableComponent(new Attack(2, 4.5), EquipSlot.WEAPON))
            );
            items.put("health potion",
                new ItemData('!', Palette.RED, "health potion")
                .setPotionComponent(new PotionComponent(new Effect(ActionSupplier.HEAL(10))))
            );
            items.put("regen potion",
                new ItemData('!', Palette.MAGENTA, "regen potion")
                .setPotionComponent(new PotionComponent(new Effect(20, ActionSupplier.HEAL(1))))
            );
            items.put("delayed health potion",
                new ItemData('!', Palette.SALMON, "delayed health potion")
                .setPotionComponent(new PotionComponent(new Effect(10, null, ActionSupplier.HEAL(20))))
            );
        }
    }

    public static Feature gameEnder(Level level, Coord pos){
        Feature feature = new Feature(level, pos, 'O', Palette.LIME, "victory podium");
        level.addFeature(feature);
        feature.winComponent = new WinComponent();
        return feature;
    }
    public static Feature stairsDown(Level level, Coord pos, Level destinationLevel, Coord destinationCoord){
        Feature feature = new Feature(level, pos, '>', Palette.WHITE, "stairs leading down");
        level.addFeature(feature);
        feature.levelChangeComponent = new LevelChangeComponent(destinationCoord, destinationLevel, false);
        return feature;
    }
    public static Feature stairsUp(Level level, Coord pos, Level destinationLevel, Coord destinationCoord){
        Feature feature = new Feature(level, pos, '<', Palette.WHITE, "stairs leading up");
        level.addFeature(feature);
        feature.levelChangeComponent = new LevelChangeComponent(destinationCoord, destinationLevel, true);
        return feature;
    }

    public static Item junk(Level level, Coord pos){
        ItemData item = new ItemData(',', Palette.BROWN, "junk");
        return item.create(level, pos);
    }
}