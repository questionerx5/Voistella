package com.questionerx5.voistella.data;

import java.util.List;
import java.util.ArrayList;

import com.questionerx5.voistella.ActionSupplier;
import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.EnemyMemory;
import com.questionerx5.voistella.Level;
import com.questionerx5.voistella.PlayerMemory;
import com.badlogic.gdx.graphics.Color;
import squidpony.squidmath.Coord;

public class CreatureData extends EntityData<Creature>{
    private ActionSupplier<? super Creature> ai;
    private int maxHp;
    private int attack;
    private double speed;

    private boolean isPlayer;
    private List<String> messages;
    public CreatureData makePlayer(List<String> messages){
        this.isPlayer = true;
        this.messages = messages;
        return this;
    }

    private boolean rememberEntities;

    private boolean isAlly;
    public CreatureData setAlly(boolean isAlly){
        this.isAlly = isAlly;
        return this;
    }
    private ItemData equipment;
    public CreatureData setEquipment(ItemData equipment){
        this.equipment = equipment;
        return this;
    }

    private List<SkillData> skills;
    public CreatureData addSkill(SkillData skill){
        skills.add(skill);
        return this;
    }

    public CreatureData(char glyph, Color color, String name, boolean unique, ActionSupplier<? super Creature> ai, boolean rememberEntities, int maxHp, int attack, double speed){
        this.glyph = glyph;
        this.color = color;
        this.name = name;
        this.unique = unique;
        this.ai = ai;
        this.rememberEntities = rememberEntities;
        this.maxHp = maxHp;
        this.attack = attack;
        this.speed = speed;
        this.isAlly = false;
        this.skills = new ArrayList<>();
    }
    public CreatureData(char glyph, Color color, String name, ActionSupplier<? super Creature> ai, boolean rememberEntities, int maxHp, int attack, double speed){
        this(glyph, color, name, Character.isUpperCase(name.charAt(0)), ai, rememberEntities, maxHp, attack, speed);
    }
    public CreatureData(CreatureData other){
        this(other.glyph, other.color, other.name, other.unique, other.ai, other.rememberEntities, other.maxHp, other.attack, other.speed);
        if(other.isPlayer){
            makePlayer(other.messages);
        }
        setAlly(other.isAlly);
        setEquipment(other.equipment);
        for(SkillData skill : other.skills){
            addSkill(skill);
        }
    }

    public Creature create(Level level, Coord pos){
        Creature creature = new Creature(level, this.glyph, this.color, this.name, this.maxHp, this.attack, this.speed, (isPlayer ? new PlayerMemory() : new EnemyMemory(rememberEntities)));
        if(this.isPlayer){
            creature.setAI(ActionSupplier.BLANK);
            creature.setMessages(messages);
            creature.message("Welcome.");
        }
        else{
            creature.setAI(this.ai);
        }
        creature.setPos(pos);
        if(this.isAlly){
            creature.setAlly(true);
        }
        if(this.equipment != null){
            creature.equip(this.equipment.create(null, null));
        }
        for(SkillData skill : skills){
            creature.addSkill(skill.create());
        }
        return creature;
    }
}