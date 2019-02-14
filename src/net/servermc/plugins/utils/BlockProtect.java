package net.servermc.plugins.utils;

import net.servermc.plugins.AmazingLuckyBlocks;
import static net.servermc.plugins.utils.LocationManager.loc_list;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;


public class BlockProtect implements Listener{
    
    public String color(String str)
    {
    return ChatColor.translateAlternateColorCodes('&', str);
    }
    
    @EventHandler
    public void noSkullCrash(BlockFromToEvent event){
        
        if(CLBManager.getManager().getConfig().getString("LuckyBlock.Material").equals("SKULL")){
            if(loc_list.contains(event.getToBlock().getLocation())){
                event.setCancelled(true);
            }            
        }      
    }
    
    @EventHandler
    public void noSkullCrash2(EntityExplodeEvent event){
        for (Block blockList : event.blockList()){
            if(loc_list.contains(blockList.getLocation())){
                event.setCancelled(true);
            }
        }  
    }
    
    @EventHandler
    public void playerRenameItem(InventoryClickEvent event){
        if(event.getView().getType() == InventoryType.ANVIL) {
            if(event.getRawSlot() == 2) {
                ItemStack item = event.getView().getItem(0);
                if(event.getView().getItem(0).getType() != Material.AIR && event.getView().getItem(2).getType() != Material.AIR) {
                    if(item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(color(CLBManager.getManager().getConfig().getString("LuckyBlock.Name")))){
                        
                        if(CLBManager.getManager().getConfig().getString("LuckyBlock.Material").equals("SKULL")){
                            if(!AmazingLuckyBlocks.getInstance().minecraftVersion.equals("1.13")){
                                Material material = Material.valueOf("SKULL_ITEM");
                                if(item.getType().equals(material)){
                                event.setCancelled(true);
                                }
                            }else{
                                String material_name = "SKULL_ITEM";
                                String material_name2 = "PLAYER_WALL_HEAD";
                                if(item.getType().name().equals(material_name) || item.getType().name().equals(material_name2)){
                                    event.setCancelled(true);
                                    }
                                }
                            }
                        }                        
                    }
                }
            }
        }
    }
