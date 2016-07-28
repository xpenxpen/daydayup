package org.xpen.ojc;


public class P014Lcp {
    
    public String longestCommonPrefix(String[] strs) {
        if (strs.length == 0) {
            return "";
        }
        if (strs.length == 1) {
            return strs[0];
        }
        
        int index = 0;
        StringBuilder lcp = new StringBuilder();
        
        while (index < strs[0].length()) {
            
            char oneChar = strs[0].charAt(index);
            
            for (int i = 1; i < strs.length; i++) {
                if ((strs[i].length() <= index) || (strs[i].charAt(index) != oneChar)) {
                    return lcp.toString();
                }
            }
            
            lcp.append(oneChar);
            index++;
        }
        
        return lcp.toString();
    }
    
    
    public static void main(String[] args) {
        P014Lcp main = new P014Lcp();
        String longestCommonPrefix = main.longestCommonPrefix(new String[]{"abcd", "abcdef", "abc", "abcde"});
        System.out.println(longestCommonPrefix);
    }
}

