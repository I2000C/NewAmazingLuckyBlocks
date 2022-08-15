package me.i2000c.newalb.custom_outcomes.rewards;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class Equipment implements Cloneable{
    public ItemStack helmet = null;
    public ItemStack chestplate = null;
    public ItemStack leggings = null;
    public ItemStack boots = null;
    public ItemStack itemInHand = null;

    public Equipment(){
    }

    public Equipment(List<ItemStack> equip){
        if(equip.size() == 5){
            if(equip.get(0) != null){
                this.helmet = equip.get(0).clone();
            }
            if(equip.get(1) != null){
                this.chestplate = equip.get(1).clone();
            }
            if(equip.get(2) != null){
                this.leggings = equip.get(2).clone();
            }
            if(equip.get(3) != null){
                 this.boots = equip.get(3).clone();
            }
            if(equip.get(4) != null){
                this.itemInHand = equip.get(4).clone();
            }
        }else{
            throw new IllegalArgumentException("Equipment size must be equals to 5");
        }
    }

    public boolean isEmpty(){
        return this.helmet == null && this.chestplate == null && this.leggings == null &&
                this.boots == null && this.itemInHand == null;
    }

    public List<ItemStack> asArrayList(){
        List<ItemStack> list = new ArrayList();
        list.add(this.helmet);
        list.add(this.chestplate);
        list.add(this.leggings);
        list.add(this.boots);
        list.add(this.itemInHand);
        return list;
    }

    public void resetEquipment(){
        this.helmet = null;
        this.chestplate = null;
        this.leggings = null;
        this.boots = null;
        this.itemInHand = null;
    }

    @Override
    @SuppressWarnings("CloneDeclaresCloneNotSupported")
    public Equipment clone(){
        try{
            Equipment copy = (Equipment) super.clone();

            if(this.helmet != null){
                copy.helmet = this.helmet.clone();
            }
            if(this.chestplate != null){
                copy.chestplate = this.chestplate.clone();
            }
            if(this.leggings != null){
                copy.leggings = this.leggings.clone();
            }
            if(this.boots != null){
                copy.boots = this.boots.clone(); 
            }
            if(this.itemInHand != null){
                copy.itemInHand = this.itemInHand.clone();
            }

            return copy;
        }catch(CloneNotSupportedException ex){
            return null;
        }
    }
}
