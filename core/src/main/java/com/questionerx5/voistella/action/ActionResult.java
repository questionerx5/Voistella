package com.questionerx5.voistella.action;

public class ActionResult{
    public final boolean success;
    public final Action alternate;
    public ActionResult(boolean success){
        this.success = success;
        this.alternate = null;
    }
    public ActionResult(Action alternate){
        this.success = true;
        this.alternate = alternate;
    }
}