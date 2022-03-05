package me.i2000c.newalb.utils;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantmentUtils{
    public static List<String> getEnchantments(ItemMeta meta){
        ArrayList<Enchantment> keyList = new ArrayList<>(meta.getEnchants().keySet());
        ArrayList<Integer> values = new ArrayList<>(meta.getEnchants().values());
        List<String> list = new ArrayList();
        for(int j=0;j<keyList.size();j++){
            list.add(keyList.get(j).getName() + ";" + values.get(j));
        }
        return list;
    }
    
    public static void setEnchantments(ItemMeta meta, List<String> enchantments){
        meta.getEnchants().keySet().forEach((ench) -> {
            meta.removeEnchant(ench);
        });
        for(String ench : enchantments){
            String[] enchData = ench.split(";");
            meta.addEnchant(Enchantment.getByName(enchData[0]), Integer.parseInt(enchData[1]), true);
        }
    }
}
