package me.i2000c.newalb.lucky_blocks.editors;

import java.util.Objects;

import org.bukkit.entity.Player;

import me.i2000c.newalb.api.functions.EditorBackFunction;
import me.i2000c.newalb.api.functions.EditorNextFunction;

public abstract class Editor<T>{
    protected T item;
    protected EditorBackFunction onBack;
    protected EditorNextFunction<T> onNext;
    
    public void createNewItem(
            Player player, 
            EditorBackFunction onBack, 
            EditorNextFunction<T> onNext){
        Objects.requireNonNull(player);
        this.onBack = Objects.requireNonNull(onBack);
        
        if(onNext == null){
            this.onNext = (_1, _2) -> {};
        }else{
            this.onNext = onNext;
        }
        
        reset();
        newItem(player);
    }
    
    public void editExistingItem(
            T item, 
            Player player, 
            EditorBackFunction onBack, 
            EditorNextFunction<T> onNext){
        this.item = Objects.requireNonNull(item);
        this.onBack = Objects.requireNonNull(onBack);
        
        if(onNext == null){
            this.onNext = (_1, _2) -> {};
        }else{
            this.onNext = onNext;
        }
        
        reset();
        editItem(player);
    }
    
    protected void reset(){}
    
    protected abstract void newItem(Player player);
    
    protected abstract void editItem(Player player);
}
