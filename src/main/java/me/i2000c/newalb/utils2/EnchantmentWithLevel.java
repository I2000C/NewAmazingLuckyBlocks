package me.i2000c.newalb.utils2;

import org.bukkit.enchantments.Enchantment;

public class EnchantmentWithLevel{
    public Enchantment enchantment;
    public int level;
    
    public EnchantmentWithLevel(Enchantment enchantment, int level){
        this.enchantment = enchantment;
        this.level = level;
    }

    @Override
    public String toString(){
        return "EnchantmentWithLevel{" + "enchantment=" + enchantment + ", level=" + level + '}';
    }
}
