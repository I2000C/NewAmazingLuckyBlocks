package me.i2000c.newalb.lucky_blocks.rewards;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.i2000c.newalb.api.functions.EditorNextFunction;
import me.i2000c.newalb.api.functions.PlayerConsumer;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.config.Config;

@Getter
@Setter
public abstract class Reward<T extends Reward<T>> implements Displayable, Executable, Comparable<Reward<?>>, Cloneable {
    public abstract RewardType getRewardType();
    public abstract void loadRewardFromConfig(Config config, String path);
    public abstract void saveRewardIntoConfig(Config config, String path);
    public abstract EditorMenu<T> getEditor();
    
    private Outcome outcome;
    private int delay;

    public Reward(Outcome outcome) {
        this.outcome = outcome;
        this.delay = 0;
    }
    
    @Override
    public int compareTo(@NonNull Reward<?> other) {
        return this.getRewardType().compareTo(other.getRewardType());
    }
    
    public final void edit(Player player, PlayerConsumer onBack, EditorNextFunction<T> onNext) {
        EditorMenu<T> menu = this.getEditor();
        menu.setItemToEdit(this.clone());
        menu.setOnBack(onBack);
        menu.setOnNext(onNext);
        menu.openToPlayer(player);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T clone() {
        try {
            return (T) super.clone();
        } catch(CloneNotSupportedException ex) {
            return null;
        }
    }
}


    

