package com.questionerx5.voistella;

public class Inventory{
    private Item[] items;
    public Item[] items(){
        return items;
    }

    // The first slot that's empty. -1 if not calculated, and items.length if there is none.
    private int nextEmpty;

    public boolean isFull(){
        if(nextEmpty == items.length){
            return true;
        }
        if(nextEmpty != -1){
            return false;
        }
        for(int i = 0; i < items.length; i++){
            if(items[i] == null){
                nextEmpty = i;
                return false;
            }
        }
        nextEmpty = items.length;
        return true;
    }
    
    public Item get(int i){
        return items[i];
    }
    public void add(Item item){
        if(nextEmpty == -1){
            isFull();
        }
        if(nextEmpty == items.length){
            return;
        }
        items[nextEmpty] = item;
        item.setLevel(null, null);
        nextEmpty = -1;
    }
    public void remove(int i){
        items[i] = null;
        if(nextEmpty != -1 && nextEmpty > i){
            nextEmpty = i;
        }
    }
    public boolean remove(Item item){
        for(int i = 0; i < items.length; i++){
            if(items[i] == item){
                remove(i);
                return true;
            }
        }
        return false;
    }

    public Inventory(int max){
        items = new Item[max];
        nextEmpty = -1;
    }
}