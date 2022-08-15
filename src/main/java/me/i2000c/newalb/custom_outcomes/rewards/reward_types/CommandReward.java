package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandReward extends Reward{
    private boolean senderIsPlayer;
    private String cmd;
    
    public CommandReward(Outcome outcome){
        super(outcome);
        senderIsPlayer = false;
        cmd = null;
    }
    
    public boolean getSenderIsPlayer(){
        return this.senderIsPlayer;
    }
    public void setSenderIsPlayer(boolean senderIsPlayer){
        this.senderIsPlayer = senderIsPlayer;
    }
    public String getCommand(){
        return this.cmd;
    }
    public void setCommand(String cmd){
        this.cmd = cmd;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemBuilder builder = ItemBuilder.newItem(XMaterial.NAME_TAG);
        builder.withDisplayName("&6Command: &b/" + cmd);
        if(senderIsPlayer){
            builder.addLoreLine("&5Sender: &2Player");
        }else{
            builder.addLoreLine("&5Sender: &8Console");
        }
        
        return builder.build();
    }

    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        config.set(path + ".cmd", this.cmd);
        if(this.senderIsPlayer){
            config.set(path + ".sender", "Player");
        }else{
            config.set(path + ".sender", "Console");
        }        
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        this.cmd = config.getString(path + ".cmd");
        String sender = config.getString(path + ".sender");
        this.senderIsPlayer = !sender.equals("Console");
    }
    
    @Override
    public void execute(Player player, Location location){
        String x = String.valueOf(player.getLocation().getBlockX());
        String y = String.valueOf(player.getLocation().getBlockY());
        String z = String.valueOf(player.getLocation().getBlockZ());
        String bx = String.valueOf(location.getBlockX());
        String by = String.valueOf(location.getBlockY());
        String bz = String.valueOf(location.getBlockZ());
        
        String command = cmd.replace("%player%", player.getName())
                .replace("%x%", x).replace("%y%", y).replace("%z%", z)
                .replace("%bx%", bx).replace("%by%", by).replace("%bz%", bz);
        
        if(senderIsPlayer){
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
