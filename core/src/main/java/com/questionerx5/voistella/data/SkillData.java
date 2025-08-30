package com.questionerx5.voistella.data;

import com.github.tommyettinger.function.ObjObjToObjBiFunction;
import com.github.yellowstonegames.grid.Coord;
import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Skill;
import com.questionerx5.voistella.action.Action;

public class SkillData {
    private String name;
    private int manaCost, staminaCost, cooldown;
    private ObjObjToObjBiFunction<Creature, Coord, Action> action;

    public SkillData(String name, int manaCost, int staminaCost, int cooldown, ObjObjToObjBiFunction<Creature, Coord, Action> action){
        this.name = name;
        this.manaCost = manaCost;
        this.staminaCost = staminaCost;
        this.cooldown = cooldown;
        this.action = action;
    }

    public Skill create(){
        return new Skill(name, manaCost, staminaCost, cooldown, action);
    }
}
