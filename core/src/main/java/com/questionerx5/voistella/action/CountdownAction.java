package com.questionerx5.voistella.action;

import com.questionerx5.voistella.Countdown;

public class CountdownAction extends Action{    
    public CountdownAction(){}

    @Override
    public ActionResult perform(){
        if(actor instanceof Countdown){
            Countdown countdown = (Countdown) actor;
            countdown.countdown();
            return new ActionResult(true);
        }
        return new ActionResult(false);
    }
}