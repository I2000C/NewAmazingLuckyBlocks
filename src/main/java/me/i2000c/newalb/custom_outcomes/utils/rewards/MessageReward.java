package me.i2000c.newalb.custom_outcomes.utils.rewards;

import com.cryptomorin.xseries.XMaterial;
import me.i2000c.newalb.custom_outcomes.menus.MessageMenu;
import me.i2000c.newalb.custom_outcomes.utils.Outcome;
import me.i2000c.newalb.utils.logger.Logger;
import me.i2000c.newalb.utils2.ActionBarUtils;
import me.i2000c.newalb.utils2.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        return ItemBuilder.newItem(XMaterial.BOOK)
                .withDisplayName("&6Message")
                .addLoreLine("&3MessageType: &b" + type.name())
                .addLoreLine("&3Title: &r\"" + title + "&r\"")
                .addLoreLine("&3Subtitle: &r\"" + subtitle + "&r\"")
                .build();
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
        String x = String.valueOf(player.getLocation().getBlockX());
        String y = String.valueOf(player.getLocation().getBlockY());
        String z = String.valueOf(player.getLocation().getBlockZ());
        String bx = String.valueOf(location.getBlockX());
        String by = String.valueOf(location.getBlockY());
        String bz = String.valueOf(location.getBlockZ());
        
        String replacedTitle = title
                .replace("%player%", player.getName())
                .replace("%x%", x).replace("%y%", y).replace("%z%", z)
                .replace("%bx%", bx).replace("%by%", by).replace("%bz%", bz);
        
        String replacedSubtitle = subtitle
                .replace("%player%", player.getName())
                .replace("%x%", x).replace("%y%", y).replace("%z%", z)
                .replace("%bx%", bx).replace("%by%", by).replace("%bz%", bz);
        
        switch(type){
            case TITLE:
                Logger.sendTitle(title, subtitle, player);
                break;
            case ACTION_BAR:
                ActionBarUtils.sendMessage(player, replacedTitle);
                break;
            case CHAT:
                player.sendMessage(replacedTitle);
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
    public Reward clone(){
        MessageReward copy = (MessageReward) super.clone();
        return copy;
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
