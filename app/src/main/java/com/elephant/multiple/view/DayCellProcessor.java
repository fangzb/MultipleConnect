package com.elephant.multiple.view;

import android.graphics.Canvas;

/**
 * @class DayCellProcessor
 * @description 
 * @author Elephant
 * @time 16/9/18 下午4:25
 */
public interface DayCellProcessor {

    void draw(Canvas canvas, int day, int cellPadding, int cellW, int cellH, int cellLeft, int cellTop, int col, int row, int status);
    
    void click(int day, int month, int year, int status);

    int getPreDayId(int currentDayId);

    int getNextDayId(int currentDayId);

    void clickTitle(int year, int month);
}
