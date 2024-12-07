package com.questionerx5.voistella;

import java.util.List;

public class World{
    private Level[] levels;
    public Level level(int index){
        return levels[index];
    }

    public World(Level[] levels){
        this.levels = levels;
    }
    public void process(int main, int... others){
        this.levels[main].process();
        for(int level : others){
            this.levels[level].process();
        }
    }

    public void setEvents(List<DisplayEvent> events){
        for(Level level : levels){
            level.setEvents(events);
        }
    }
}