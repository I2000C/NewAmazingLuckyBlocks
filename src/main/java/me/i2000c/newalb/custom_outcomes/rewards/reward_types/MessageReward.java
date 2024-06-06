package me.i2000c.newalb.custom_outcomes.rewards.reward_types;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.messages.ActionBar;

import lombok.Getter;
import lombok.Setter;
import me.i2000c.newalb.config.Config;
import me.i2000c.newalb.custom_outcomes.rewards.Outcome;
import me.i2000c.newalb.custom_outcomes.rewards.Reward;
import me.i2000c.newalb.custom_outcomes.rewards.RewardType;
import me.i2000c.newalb.utils.Logger;
import me.i2000c.newalb.utils2.ItemStackWrapper;

@Getter
@Setter
public class MessageReward extends Reward{
    private String title;
    private String subtitle;
    private MessageType messageType;
    
    public MessageReward(Outcome outcome){
        super(outcome);
        title = "";
        subtitle = "";
        messageType = MessageType.TITLE;
    }
    
    @Override
    public ItemStack getItemToDisplay(){
        return ItemStackWrapper.newItem(XMaterial.BOOK)
                               .setDisplayName("&6Message")
                               .addLoreLine("&3MessageType: &b" + messageType.name())
                               .addLoreLine("&3Title: &r\"" + title + "&r\"")
                               .addLoreLine("&3Subtitle: &r\"" + subtitle + "&r\"")
                               .toItemStack();
    }
    
    @Override
    public void saveRewardIntoConfig(Config config, String path){
        config.set(path + ".title", this.title);
        config.set(path + ".subtitle", this.subtitle);
        config.set(path + ".messageType", messageType);
    }
    
    @Override
    public void loadRewardFromConfig(Config config, String path){
        this.title = config.getString(path + ".title");
        this.subtitle = config.getString(path + ".subtitle");
        this.messageType = config.getEnum(path + ".messageType", MessageType.class);
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
        
        switch(messageType){
            case TITLE:
                Logger.sendTitle(replacedTitle, replacedSubtitle, player);
                break;
            case ACTION_BAR:
                ActionBar.sendActionBar(player, Logger.color(replacedTitle));
                break;
            case CHAT:
                Logger.sendMessage(replacedTitle, player, false);
                break;
        }        
    }
    
    @Override
    public RewardType getRewardType(){
        return RewardType.message;
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
