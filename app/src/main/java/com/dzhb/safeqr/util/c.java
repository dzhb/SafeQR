package com.dzhb.safeqr.util;

public class c {
    public static int count_up = 0;
    public static int count_down = 0;

    public static void c_up(){
        if(count_up == 0) count_up = 1;
        else count_up = 0;
    }

    public static void c_down(){
        if(count_down == 0) count_down = 1;
        else count_down = 0;
    }

    public static void clean(){
        count_up = 0;
        count_down = 0;
    }
}
