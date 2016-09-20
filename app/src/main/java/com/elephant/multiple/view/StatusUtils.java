package com.elephant.multiple.view;

/**
 * @class StatusUtils
 * @description
 * @author Elephant
 * @time 16/9/18 下午4:26
 * status 十六进制，每位代表不同状态:  0xABCDE
 * <p/>
 * A 是否可用 :     0 - disable, 1 - enable
 * <p/>
 * B 是否高亮 :     0 - normal, 1 - highLight
 * <p/>
 * C 前一天是否选中 :      0 - selected, 1 - unselected
 * D 今天是否选中 :        0 - selected, 1 - unselected
 * E 后一天是否选中 :      0 - selected, 1 - unselected
 */
public class StatusUtils {
    public static boolean isEnable(int status) {
        int enable = status >> (4 * 4);
        return enable == 1;
    }

    /**
     * @return 可用code
     */
    public static int enable() {
        return 0x10000;
    }

    /**
     * @return 不可用code
     */
    public static int disable() {
        return 0x00000;
    }

    /**
     * @param status 要检测的日期状态
     * @return 是否高亮
     */
    public static boolean isHighLight(int status) {
        int highlight = (status & 0x0F000) >> (4 * 3);
        return highlight == 1;
    }

    /**
     * @param status 要被设置日期的状态
     * @return 将高亮位设置为1
     */
    public static int highLight(int status) {
        return status | 0x01000;
    }

    /**
     * @param status 要被设置日期的状态
     * @return 将高亮位设置为0
     */
    public static int clearHighLight(int status) {
        return status & 0xF0FFF;
    }

    /**
     * @param status 要检测的日期状态
     * @return 是否被选中
     */
    public static boolean isSelected(int status) {
        return (status & 0x000F0) == 0x00010;
    }
}
