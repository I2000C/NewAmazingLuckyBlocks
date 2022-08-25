package me.i2000c.newalb.custom_outcomes.rewards;

import me.i2000c.newalb.custom_outcomes.editor.Editor;
import me.i2000c.newalb.functions.EditorBackFunction;
import me.i2000c.newalb.functions.EditorNextFunction;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract class Reward implements Displayable, Executable, Cloneable{
    public abstract RewardType getRewardType();
    public abstract void loadRewardFromConfig(FileConfiguration config, String path);
    public abstract void saveRewardIntoConfig(FileConfiguration config, String path);

    private Outcome outcome;
    private int delay;

    public Reward(Outcome outcome){
        this.outcome = outcome;
        this.delay = 0;
    }
    public final Outcome getOutcome(){
        return this.outcome;
    }
    final void setOutcome(Outcome outcome){
        this.outcome = outcome;
    }

    public final int getDelay(){
        return this.delay;
    }
    public final void setDelay(int ticks){
        this.delay = ticks;
    }
    
    public void edit(
            Player player, 
            EditorBackFunction onBack, 
            EditorNextFunction onNext){
        Editor editor = this.getRewardType().getEditorType().getEditor();
        editor.editExistingItem(this.clone(), player, onBack, onNext);
    }
    
    @Override
    public Reward clone(){
        try{
            return (Reward) super.clone();
        }catch(CloneNotSupportedException ex){
            return null;
        }
    }
}


    

