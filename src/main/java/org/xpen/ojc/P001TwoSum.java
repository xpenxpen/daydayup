package org.xpen.ojc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class P001TwoSum {

    public static void main(String[] args) {
        int nums[] = new int[]{3,2,4,77};
        int target = 6;
        int[] result = twoSum(nums, target);
        System.out.println(result[0]);
        System.out.println(result[1]);

    }
    
    public static int[] twoSum1(int[] nums, int target) {
        Map<Integer, List<Integer>> numSet = new HashMap<Integer, List<Integer>>();
        for (int i=0; i<nums.length;i++) {
            if (!numSet.containsKey(nums[i])) {
                List<Integer> list  = new ArrayList<Integer>();
                list.add(i+1);
                numSet.put(nums[i], list);
                
            } else {
                numSet.get(nums[i]).add(i+1);
            }
        }
        
        for (int i=0; i<nums.length;i++) {
            int toFind = target - nums[i];
            //same 2 numbers
            if (toFind == nums[i]) {
                if (numSet.containsKey(toFind)) {
                    List<Integer> list = numSet.get(toFind);
                    if (list.size()>=2) {
                        return new int[]{list.get(0), list.get(1)};
                    }
                }
            } else if (numSet.containsKey(toFind)) {
                int another =numSet.get(toFind).get(0);
                return new int[]{i+1, another};
            }

        }
        return null;
    }
    
    public static int[] twoSum(int[] nums, int target) {
        int[] result = new int[2];
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(target - nums[i])) {
                result[1] = i + 1;
                result[0] = map.get(target - nums[i]);
                return result;
            }
            map.put(nums[i], i + 1);
        }
        return result;
    }

}
