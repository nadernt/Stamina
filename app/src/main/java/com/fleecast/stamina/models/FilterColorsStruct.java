package com.fleecast.stamina.models;

/**
 * Created by nnt on 18/12/16.
 */

public class FilterColorsStruct {
    private final int color0;
    private final int color1;
    private final int color2;
    private final int color3;
    private final int color4;
    private final int color5;
    private final int color6;
    private final int color7;
    private final int color8;
    private final int color9;


    public FilterColorsStruct(int[] colors) {
        this.color0 = colors[0];
        this.color1 = colors[1];
        this.color2 = colors[2];
        this.color3 = colors[3];
        this.color4 = colors[4];
        this.color5 = colors[5];
        this.color6 = colors[6];
        this.color7 = colors[7];
        this.color8 = colors[8];
        this.color9 = colors[9];
    }

    public int getColor0() {
        return color0;
    }

    public int getColor1() {
        return color1;
    }

    public int getColor2() {return color2; }

    public int getColor3() {
        return color3;
    }

    public int getColor4() {
        return color4;
    }

    public int getColor5() {
        return color5;
    }

    public int getColor6() {
        return color6;
    }

    public int getColor7() {
        return color7;
    }

    public int getColor8() {
        return color8;
    }

    public int getColor9() { return color9; }
public int [] getAllColors(){
        return new int[]{color0,color1,color2,color3,color4,color5,color6,color7,color8};
    }
}
