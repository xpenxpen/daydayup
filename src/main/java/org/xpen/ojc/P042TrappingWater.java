package org.xpen.ojc;

public class P042TrappingWater {
    
    public int trap(int[] height) {
        //System.out.println("START-------");
        int total = 0;
        int left = 0;
        int right = 0;
        //find max left and right
        while (left < height.length - 1 && height[left + 1] >= height[left]) {
            left++;
        }
        right = left + 1;
        if (right >= height.length) {
            return 0;
        }
        //above has no water
        
        while (right <= height.length - 1) {
            //System.out.println("7777-----left="+left+",right"+right);
            int maxRight = right;
            while (right <= height.length - 1 && height[left] > height[right]) {
                if (height[right] >= height[maxRight]) {
                    maxRight = right;
                }
                right++;
            }
            
            //if go to end, recalculate
            if (right >= height.length) {
                //System.out.println("rewind cal");
                total = calRainBetween(height, left, maxRight, total);
                left = maxRight;
                right = left + 1;
                continue;
            }
            
            total = calRainBetween(height, left, right, total);
            
            //prepare for next round
            left = right;
            right = right + 1;
        }
        
        return total;
    }
    
    private int calRainBetween(int[] height, int left, int right, int oldTotal) {
        int total = oldTotal;
        int min = Math.min(height[left], height[right]);
        //System.out.println("left="+left+",right"+right);
        for (int i = left + 1; i <= right - 1; i++) {
            if (min > height[i]) {
                total += min - height[i];
            }
            //System.out.println(i + ">" + total);
        }
        return total;
    }

    public static void main(String[] args) {
        P042TrappingWater main = new P042TrappingWater();
        int[] height = new int[]{0,1,0,2,1,0,1,3,2,1,2,1};
        System.out.println(main.trap(height));
        height = new int[]{5,4,1,2};
        System.out.println(main.trap(height));
        height = new int[]{9,6,8,8,5,6,3};
        System.out.println(main.trap(height));

    }
}
