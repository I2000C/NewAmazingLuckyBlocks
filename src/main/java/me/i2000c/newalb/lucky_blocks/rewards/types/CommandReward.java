package me.i2000c.newalb.lucky_blocks.rewards.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.lucky_blocks.rewards.Outcome;
import me.i2000c.newalb.lucky_blocks.rewards.Reward;
import me.i2000c.newalb.lucky_blocks.rewards.RewardType;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;

@Getter
@Setter
public class CommandReward extends Reward{
    private boolean sendFromPlayer;
    private String command;
    
    public CommandReward(Outcome outcome){
        super(outcome);
        sendFromPlayer = false;
        command = null;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemStackWrapper builder = ItemStackWrapper.newItem(XMaterial.NAME_TAG);
        builder.setDisplayName("&6Command: &b/" + command);
        if(sendFromPlayer){
            builder.addLoreLine("&5Sender: &2Player");
        }else{
            builder.addLoreLine("&5Sender: &8Console");
        }
        
        return builder.toItemStack();
    }

    @Override
    public void saveRewardIntoConfig(Config config, String path){
        config.set(path + ".cmd", this.command);
        if(this.sendFromPlayer){
            config.set(path + ".sender", "Player");
        }else{
            config.set(path + ".sender", "Console");
        }        
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        this.command = config.getString(path + ".cmd");
        String sender = config.getString(path + ".sender");
        this.sendFromPlayer = !sender.equals("Console");
    }
    
    @Override
    public void execute(Player player, Location location){
        String world = player.getLocation().getWorld().getName();
        String x = String.valueOf(player.getLocation().getBlockX());
        String y = String.valueOf(player.getLocation().getBlockY());
        String z = String.valueOf(player.getLocation().getBlockZ());
        String bx = String.valueOf(location.getBlockX());
        String by = String.valueOf(location.getBlockY());
        String bz = String.valueOf(location.getBlockZ());
        
        String command = this.command.replace("%player%", player.getName())
                                     .replace("%x%", x).replace("%y%", y).replace("%z%", z)
                                     .replace("%bx%", bx).replace("%by%", by).replace("%bz%", bz)
                                     .replace("%world%", world);
        
        if(sendFromPlayer){
            Bukkit.dispatchCommand(player, command);
        }else{
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.command;
    }
    
    @Override
    public Reward clone(){
        CommandReward copy = (CommandReward) super.clone();
        return copy;
    }
}
