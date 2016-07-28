package org.xpen.ojc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class P004MedianOfTwoSortedArrays {
    
    //not finished
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int num1Size = nums1.length;
        int num2Size = nums2.length;
        int totalSize = nums1.length + nums2.length;
        int targetPos = totalSize / 2;
        if (totalSize % 2 == 0) {
            
        } else {
            
        }
        

        if (nums1[num1Size/2-1] < nums2[num2Size/2-1]) {
            //throw all nums1[num1Size/2-1]
        } else {
            
        }
        
        List<Integer> wholeList = new ArrayList<Integer>();
        for (int num: nums1) {
            wholeList.add(num);
        }
        for (int num: nums2) {
            wholeList.add(num);
        }
        Collections.sort(wholeList);
        int size = wholeList.size();
        if (size % 2 != 0) {
            return wholeList.get(size / 2);
        }
        return (wholeList.get(size / 2 - 1) + wholeList.get(size / 2)) / 2.0d;
    }
    
    public double findMedianSortedArraysV1(int[] nums1, int[] nums2) {
        List<Integer> wholeList = new ArrayList<Integer>();
        for (int num: nums1) {
            wholeList.add(num);
        }
        for (int num: nums2) {
            wholeList.add(num);
        }
        Collections.sort(wholeList);
        int size = wholeList.size();
        if (size % 2 != 0) {
            return wholeList.get(size / 2);
        }
        return (wholeList.get(size / 2 - 1) + wholeList.get(size / 2)) / 2.0d;
    }
    
    
    public static void main(String[] args) {
        P004MedianOfTwoSortedArrays main = new P004MedianOfTwoSortedArrays();
        int[] nums1 = {1, 2, 3};
        int[] nums2 = {2, 3, 4};
        
        double result = main.findMedianSortedArrays(nums1, nums2);
        System.out.println(result);
    }
    

}

