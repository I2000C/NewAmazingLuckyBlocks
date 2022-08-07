package me.i2000c.newalb.custom_outcomes.utils.rewards;

import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.menus.CommandMenu;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        List<String> lore = new ArrayList<>();
        String senderName;
        if(senderIsPlayer){
            senderName = "&2Player";
        }else{
            senderName = "&8Console";
        }
        lore.add("&5Sender: " + senderName);
        
        ItemStack stack = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("&6Command: &b/" + cmd);
        meta.setLore(lore);
        stack.setItemMeta(meta);
        
        return stack;
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
    public Reward clone(){
        CommandReward copy = (CommandReward) super.clone();
        return copy;
    }
}
