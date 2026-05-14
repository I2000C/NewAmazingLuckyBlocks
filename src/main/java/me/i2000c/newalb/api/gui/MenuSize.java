package me.i2000c.newalb.api.gui;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum MenuSize {
    /** 1 row = 9 slots */
    SIZE_1_ROW(9*1),
    
    /** 2 rows = 18 slots */
    SIZE_2_ROWS(9*2),
    
    /** 3 rows = 27 slots */
    SIZE_3_ROWS(9*3),
    
    /** 4 rows = 36 slots */
    SIZE_4_ROWS(9*4),
    
    /** 5 rows = 45 slots */
    SIZE_5_ROWS(9*5),
    
    /** 6 rows = 54 slots */
    SIZE_6_ROWS(9*6);
    
    private final int size;
}
