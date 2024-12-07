package com.questionerx5.voistella;

import com.questionerx5.voistella.action.Action;

public abstract class Actor{
    private static long nextId = 0;
    protected long id = -1;
    protected void setId(){
        if(id != -1){
            throw new IllegalStateException("Actor's id has already been set!");
        }
        id = nextId;
        nextId++;
    }
    @Override
    public int hashCode(){
        return Long.hashCode(id);
    }

    public static final int COOLDOWN_RESET_TO = 1000;
    protected int cooldown;
    protected abstract int speed();
    public boolean takesTurns(){
        return speed() >= 0;
    }
    public void tick(){
        cooldown -= speed();
    }
    public boolean turnReady(){
        return cooldown <= 0;
    }
    public void resetCooldown(){
        cooldown += COOLDOWN_RESET_TO;
    }
    public abstract Action getAction();
}