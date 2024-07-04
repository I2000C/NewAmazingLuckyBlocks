package me.i2000c.newalb.utils2;

import com.cryptomorin.xseries.XEnchantment;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnchantmentWithLevel {
    public XEnchantment enchantment;
    public int level;
}
