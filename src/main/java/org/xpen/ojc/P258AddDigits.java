package org.xpen.ojc;


public class P258AddDigits {
    
    public int addDigits(int num) {
        //return num - 9 * ((num - 1) / 9);
        return 1 + ((num - 1) % 9);
    }
    
    
    
    public static void main(String[] args) {
        P258AddDigits main = new P258AddDigits();
        
        for (int i = 1; i < 100; i++) {
            int result = main.addDigits(i);
            System.out.println(i + "-->" + result);
        }
    }

}

