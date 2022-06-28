package me.i2000c.newalb.custom_outcomes.utils.rewards;

import me.i2000c.newalb.custom_outcomes.menus.CommandMenu;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils.Logger;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandReward extends Reward{
    private String sender;
    private String cmd;
    
    public CommandReward(Outcome outcome){
        super(outcome);
        sender = "Console";
        cmd = null;
    }
    
    public String getSender(){
        return this.sender;
    }
    public void setSender(String sender){
        this.sender = sender;
    }
    public String getCommand(){
        return this.cmd;
    }
    public void setCommand(String cmd){
        this.cmd = cmd;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        List<String> lore = new ArrayList<>();
        String senderName;
        if(sender.equals("Console")){
            senderName = "&8Console";
        }else{
            senderName = "&2Player";
        }
        lore.add(Logger.color("&5Sender: " + senderName));
        
        ItemStack stack = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color("&6Command: &b/" + cmd));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        
        return stack;
    }

    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        config.set(path + ".cmd", this.cmd);
        config.set(path + ".sender", this.sender);
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        this.cmd = config.getString(path + ".cmd");
        this.sender = config.getString(path + ".sender");
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
        
        if(sender.equals("Console")){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }else{
            Bukkit.dispatchCommand(player, command);
        }
    }
    
    @Override
    public void edit(Player player){
        CommandMenu.reset();
        CommandMenu.reward = this;
        CommandMenu.openCommandMenu(player);
    }
    
    @Override
    public Reward.RewardType getRewardType(){
        return Reward.RewardType.command;
    }
    
    @Override
    public Reward cloneReward(){
        CommandReward reward = new CommandReward(this.getOutcome());
        
        reward.sender = this.sender;
        reward.cmd = this.cmd;
        reward.setDelay(this.getDelay());
        return reward;
    }
}
