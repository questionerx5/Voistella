package com.questionerx5.voistella.action;

import com.questionerx5.voistella.Creature;
import com.questionerx5.voistella.Effect;

public class EffectAction extends Action{
    private final Creature target;
    private final Effect effect;
    
    public EffectAction(Creature target, Effect effect){
        this.target = target;
        this.effect = effect;
    }

    @Override
    public ActionResult perform(){
        if(target == null || effect == null){
            if(actor instanceof Creature actorCreature){
                actorCreature.messageError("There's nothing there to affect.");
            }
            return new ActionResult(false);
        }
        effect.applyTo(target);
        return new ActionResult(true);
    }
}