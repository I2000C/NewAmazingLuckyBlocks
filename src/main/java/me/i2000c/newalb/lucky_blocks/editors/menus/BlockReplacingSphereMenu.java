package me.i2000c.newalb.lucky_blocks.editors.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import me.i2000c.newalb.api.gui.GUIItem;
import me.i2000c.newalb.api.gui.GlassColor;
import me.i2000c.newalb.api.gui.MenuSize;
import me.i2000c.newalb.api.gui.menus.EditorMenu;
import me.i2000c.newalb.listeners.inventories.MenuClickEvent;
import me.i2000c.newalb.lucky_blocks.rewards.types.BlockReplacingSphereReward;
import me.i2000c.newalb.utils.misc.ItemStackWrapper;
import me.i2000c.newalb.utils.misc.OtherUtils;

public class BlockReplacingSphereMenu extends EditorMenu<BlockReplacingSphereReward> {
    
    private static final int[] VALUES = {-100, -10, -1, 0, +1, +10, +100};
    
    private final List<Consumer<Integer>> brsPropertyList = new ArrayList<>();
    
    public BlockReplacingSphereMenu() {
        super("&b&lBRS Reward", MenuSize.SIZE_6_ROWS, true);
        
        brsPropertyList.add(value -> {
            if(value == 0) {
                value = -item.getMinRadius();
            }
            
            int minRadius = OtherUtils.clamp(item.getMinRadius() + value, 0, item.getMaxRadius());
            item.setMinRadius(minRadius);
        });
        brsPropertyList.add(value -> {
            if(value == 0) {
                value = -item.getMaxRadius();
            }
            
            int maxRadius = OtherUtils.clamp(item.getMaxRadius() + value, item.getMinRadius(), Integer.MAX_VALUE);
            item.setMaxRadius(maxRadius);
        });
        brsPropertyList.add(value -> {
            if(value == 0) {
                value = -item.getTicksBetweenLayers();
            }
            
            int ticksBetweenLayers = OtherUtils.clamp(item.getTicksBetweenLayers() + value, 0, Integer.MAX_VALUE);
            item.setTicksBetweenLayers(ticksBetweenLayers);
        });
    }
    
    @Override
    protected void buildMenu(Player player) {
        addGlassBorder(GlassColor.LIGHT_BLUE);
        
        ItemStack usePlayerLocItem = GUIItem.getUsePlayerLocItem(item.isUsePlayerLoc());
        
        ItemStack replaceLiquids = GUIItem.getBooleanItem(
                item.isReplaceLiquids(), 
                "&bReplace liquids", 
                XMaterial.WATER_BUCKET, 
                XMaterial.BUCKET);
        
        ItemStack removeMaterialsItem = ItemStackWrapper.newItem(XMaterial.BARRIER)
                                                        .setDisplayName("&cClick to remove all materials")
                                                        .toItemStack();
        
        ItemStack materialsItem = ItemStackWrapper.newItem(XMaterial.DIAMOND_ORE)
                                                  .setDisplayName("&6Sphere materials")
                                                  .addLoreLine("&aLeft click on an item of your inventory to add it to the list")
                                                  .addLoreLine("&cRight click on an item of your inventory to remove it from the list")
                                                  .addLoreLine("")
                                                  .addLoreLine("&bNote:")
                                                  .addLoreLine("&b  - You can select a &6water bucket &bto generate a &6water block")
                                                  .addLoreLine("&b  - You can select a &6lava bucket &bto generate a &6lava block")
                                                  .addLoreLine("&b  - You can select a &6fire charge &bto generate a &6fire block")
                                                  .addLoreLine("")
                                                  .addLoreLine("&2Current materials:")
                                                  .addLore(item.getSortedMaterialList())
                                                  .toItemStack();
        
        ItemStack minRadiusItem = ItemStackWrapper.newItem(XMaterial.SNOWBALL)
                                                  .setDisplayName("&bMin radius: &6" + item.getMinRadius())
                                                  .addLoreLine("&3Click to reset")
                                                  .toItemStack();
        
        ItemStack maxRadiusItem = ItemStackWrapper.newItem(XMaterial.SLIME_BALL)
                                                  .setDisplayName("&bMax radius: &6" + item.getMaxRadius())
                                                  .addLoreLine("&3Click to reset")
                                                  .toItemStack();
        
        ItemStack ticksBetweenLayersItem = ItemStackWrapper.newItem(XMaterial.CLOCK)
                                                           .setDisplayName("&bTicks between layers: &6" + item.getTicksBetweenLayers())
                                                           .addLoreLine("&3Click to reset")
                                                           .toItemStack();
        
        setBackItem(10);
        setNextItem(16, e -> {
            if(!item.isEmptyMaterialList()) {
                onNext(player, item);
            }
        });
        
        setItem(4, removeMaterialsItem, e -> {
            item.clearMaterials();
            openToPlayer(player);
        });
        
        setItem(12, usePlayerLocItem, e -> {
            item.setUsePlayerLoc(!item.isUsePlayerLoc());
            openToPlayer(player);
        });
        
        setItem(13, materialsItem);
        
        setItem(14, replaceLiquids, e -> {
            item.setReplaceLiquids(!item.isReplaceLiquids());
            openToPlayer(player);
        });
        
        ItemStack[] resetItems = {minRadiusItem, maxRadiusItem, ticksBetweenLayersItem};
        
        for(int row=0; row<brsPropertyList.size(); row++) {
            int slot = row * 9 + 19;
            for(int value : VALUES) {
                ItemStack stack = value == 0 ? resetItems[row] : GUIItem.getPlusLessItem(value);
                Consumer<Integer> propertyModifier = brsPropertyList.get(row);
                setItem(slot++, stack, e -> {
                    propertyModifier.accept(value);
                    openToPlayer(e.getPlayer());
                });
            }
        }
    }
    
    @Override
    protected void onClickDefault(MenuClickEvent event) {
        if(event.isBottomInventory() && !event.isEmptyItem()) {
            Player player = event.getPlayer();
            ItemStack stack = event.getCurrentItem();
            
            XMaterial material = XMaterial.matchXMaterial(stack);
            if(!stack.getType().isBlock()) {
                switch(material) {
                    case WATER_BUCKET:  material = XMaterial.WATER; break;
                    case LAVA_BUCKET:   material = XMaterial.LAVA;  break;
                    case FIRE_CHARGE:   material = XMaterial.FIRE;  break;
                    default:            material = null;
                }
            }
            
            if(material != null) {
                if(event.getClick() == ClickType.LEFT) {
                    item.addMaterial(material);
                } else if(event.getClick() == ClickType.RIGHT) {
                    item.removeMaterial(material);
                }
                openToPlayer(player);
            }
        }
    }
}
