package com.questionerx5.voistella.action;

public class WaitAction extends Action{
    public WaitAction(){}

    @Override
    public ActionResult perform(){return new ActionResult(true);}
}