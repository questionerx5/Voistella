package com.questionerx5.voistella.action;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Skill;
import squidpony.squidmath.Coord;

public class SkillAction extends Action{
    private final Skill skill;
    private final Coord pos;

    public SkillAction(Skill skill, Coord pos){
        this.skill = skill;
        this.pos = pos;
    }

    @Override
    public ActionResult perform(){
        if(skill == null){
            return new ActionResult(false);
        }
        if(actor instanceof Creature){
            Creature actorCreature = (Creature) actor;
            if(!skill.castable(actorCreature)){
                return new ActionResult(false);
            }
            Action action = skill.castAction(actorCreature, pos);
            if(action == null){
                return new ActionResult(false);
            }
            actorCreature.emitMessage("@Name use$ ~.", true, skill.name());
            skill.cast(actorCreature);
            return new ActionResult(action);
        }
        return new ActionResult(false);
    }
}
