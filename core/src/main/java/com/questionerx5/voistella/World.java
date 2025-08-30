package com.questionerx5.voistella;

import com.github.tommyettinger.ds.ObjectList;

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

    public void setEvents(ObjectList<DisplayEvent> events){
        for(Level level : levels){
            level.setEvents(events);
        }
    }
}