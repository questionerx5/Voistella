package com.questionerx5.voistella;

public abstract class Countdown extends Actor{
    protected int duration;
    public void countdown(){
        if(duration > 0){
            duration--;
        }
        if(duration <= 0){
            end();
        }
    }

    protected abstract void end();
}
