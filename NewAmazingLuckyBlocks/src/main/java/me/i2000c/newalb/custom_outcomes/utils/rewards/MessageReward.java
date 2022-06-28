package me.i2000c.newalb.custom_outcomes.utils.rewards;

import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils.Logger;
import java.util.ArrayList;
import java.util.List;
import me.i2000c.newalb.custom_outcomes.menus.MessageMenu;
import me.i2000c.newalb.utils2.ActionBarUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MessageReward extends Reward{
    private String title;
    private String subtitle;
    private MessageType type;
    
    public MessageReward(Outcome outcome){
        super(outcome);
        title = "";
        subtitle = "";
        type = MessageType.TITLE;
    }
    
    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getSubtitle(){
        return this.subtitle;
    }
    public void setSubtitle(String subtitle){
        this.subtitle = subtitle;
    }
    public MessageType getMessageType(){
        return this.type;
    }
    public void setMessageType(MessageType type){
        this.type = type;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        ItemStack stack = new ItemStack(Material.BOOK);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Logger.color("&6Message"));
        
        List<String> lore = new ArrayList<>();
        lore.add("&3MessageType: &b" + type.name());
        lore.add("&3Title: &r\"" + title + "&r\"");
        lore.add("&3Subtitle: &r\"" + subtitle + "&r\"");
        
        meta.setLore(Logger.color(lore));
        stack.setItemMeta(meta);
        
        return stack;
    }
    
    @Override
    public void saveRewardIntoConfig(FileConfiguration config, String path){
        config.set(path + ".title", this.title);
        config.set(path + ".subtitle", this.subtitle);
        config.set(path + ".messageType", type.name());
    }
    
    @Override
    public void loadRewardFromConfig(FileConfiguration config, String path){
        this.title = config.getString(path + ".title");
        this.subtitle = config.getString(path + ".subtitle");
        this.type = MessageType.valueOf(config.getString(path + ".messageType"));
    }
    
    @Override
    public void execute(Player player, Location location){
        switch(type){
            case TITLE:
                String titleAux = title.isEmpty() ? "&o" : title;
                String subtitleAux = subtitle.isEmpty() ? "&o" : subtitle;
                player.sendTitle(Logger.color(titleAux), Logger.color(subtitleAux));
                break;
            case ACTION_BAR:
                ActionBarUtils.sendMessage(player, title);
                break;
            case CHAT:
                player.sendMessage(Logger.color(title));
                break;
        }        
    }
    
    @Override
    public void edit(Player player){
        MessageMenu.reset();
        MessageMenu.reward = this;
        MessageMenu.openMessageMenu(player);
    }
    
    @Override
    public Reward.RewardType getRewardType(){
        return Reward.RewardType.message;
    }
    
    @Override
    public Reward cloneReward(){
        MessageReward reward = new MessageReward(this.getOutcome());
        reward.setDelay(this.getDelay());
        
        reward.title = this.title;
        reward.subtitle = this.subtitle;
        reward.type = this.type;
        return reward;
    }
    
    public static enum MessageType{
        TITLE,
        ACTION_BAR,
        CHAT;
        
        public MessageType getNextType(){
            switch(this){
                case TITLE:
                    return ACTION_BAR;
                case ACTION_BAR:
                    return CHAT;
                default:
                    return TITLE;
            }
        }
    }
}
