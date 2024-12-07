package com.questionerx5.voistella.action;

public class RedirectAction extends Action{
    private Action action1;
    private Action action2;

    public RedirectAction(Action action1, Action action2){
        this.action1 = action1;
        this.action2 = action2;
    }

    @Override
    public ActionResult perform(){
        action1.bind(actor);
        ActionResult success = action1.perform();
        if(!success.success){
            return new ActionResult(false);
        }
        if(success.alternate != null){
            return new ActionResult(new RedirectAction(success.alternate, action2));
        }
        return new ActionResult(action2);
    }
}