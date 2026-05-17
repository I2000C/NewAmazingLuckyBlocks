package me.i2000c.newalb.utils.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum EquipmentSlot {
    HELMET("Helmet"),
    CHESTPLATE("Chestplate"),
    LEGGINGS("Leggings"),
    BOOTS("Boots"),
    ITEM_IN_HAND("ItemInHand"),
    ITEM_IN_OFF_HAND("ItemInOffHand");
    
    private final String configKey;
    
    public static EquipmentSlot[] VALUES = values();
}
