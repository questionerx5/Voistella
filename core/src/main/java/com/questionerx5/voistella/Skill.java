package com.questionerx5.voistella;

import com.questionerx5.voistella.action.Action;
import com.questionerx5.voistella.action.CountdownAction;
import com.questionerx5.voistella.action.WaitAction;

import squidpony.squidmath.Coord;
import java.util.function.BiFunction;

public class Skill extends Countdown{
    private String name;
    public String name(){
        return name;
    }
    public String costs(Creature creature){
        String costs = "";
        if(manaCost != 0){
            costs += creature.mana() + "/" + manaCost + " mana,";
        }
        if(staminaCost != 0){
            costs += creature.stamina() + "/" + staminaCost + " stamina,";
        }
        if(cooldown != 0){
            if(ready){
                costs += "cooldown " + cooldown + ",";
            }
            else{
                costs += "cooldown " + duration + "/" + cooldown + ",";

            }
        }
        if("".equals(costs)){
            costs = "(free)";
        }
        else{
            costs = costs.substring(0, costs.length() - 1);
        }
        return costs;
    }

    private int manaCost, staminaCost, cooldown;
    private boolean ready;
    public boolean castable(Creature creature){
        return ready && creature.mana() >= manaCost && creature.stamina() >= staminaCost;
    }
    public String noncastableReason(Creature creature){
        String reason = "";
        if(!ready){
            reason += "That skill is on cooldown.\n";
        }
        if(creature.mana() < manaCost){
            reason += "You don't have enough [Sky Blue]mana[WHITE] for that skill.\n";
        }
        if(creature.stamina() < staminaCost){
            reason += "You don't have enough [PINK]stamina[WHITE] for that skill.\n";
        }
        return reason.trim();
    }

    private BiFunction<Creature, Coord, Action> action;
    public Action castAction(Creature caster, Coord destination){
        return action.apply(caster, destination);
    }

    public Skill(String name, int manaCost, int staminaCost, int cooldown, BiFunction<Creature, Coord, Action> action){
        this.name = name;
        this.manaCost = manaCost;
        this.staminaCost = staminaCost;
        this.cooldown = cooldown;
        this.duration = 0;
        this.ready = true;
        this.action = action;
    }

    public void cast(Creature caster){
        this.duration = cooldown + 1;
        ready = false;
        caster.modifyMana(-manaCost);
        caster.modifyStamina(-staminaCost);
    }

    @Override
    protected void end(){
        ready = true;
    }

    @Override
    protected int speed(){
        return 100;
    }

    @Override
    public Action getAction(){
        if(ready){
            return new WaitAction();
        }
        else{
            return new CountdownAction();
        }
    }
}
