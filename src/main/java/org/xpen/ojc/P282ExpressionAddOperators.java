package org.xpen.ojc;

import java.util.ArrayList;
import java.util.List;

public class P282ExpressionAddOperators {
    
    
    public void testAndprint(String num, int target) {
        List<String> expressions = addOperators(num, target);
        for (String expression : expressions) {
            System.out.println(expression);
        }
        System.out.println();
    }
    public void test() {
        testAndprint("123", 6);
        testAndprint("232", 8);
        testAndprint("105", 5);
        testAndprint("00", 0);
    }
    
    public List<String> addOperators(String num, int target) {
        List<String> res = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        dfs(res, sb, num, 0, target, 0, 0);
        return res;
    }
    
    public void dfs(List<String> res, StringBuilder sb, String num, int pos, int target, long prev, long multi) { 
        if(pos == num.length()) {
            if(target == prev) res.add(sb.toString());
            return;
        }
        for(int i = pos; i < num.length(); i++) {
            if(num.charAt(pos) == '0' && i != pos) break;
            long curr = Long.parseLong(num.substring(pos, i + 1));
            int len = sb.length();
            if(pos == 0) {
                dfs(res, sb.append(curr), num, i + 1, target, curr, curr); 
                sb.setLength(len);
            } else {
                dfs(res, sb.append("+").append(curr), num, i + 1, target, prev + curr, curr); 
                sb.setLength(len);
                dfs(res, sb.append("-").append(curr), num, i + 1, target, prev - curr, -curr); 
                sb.setLength(len);
                dfs(res, sb.append("*").append(curr), num, i + 1, target, prev - multi + multi * curr, multi * curr); 
                sb.setLength(len);
            }
        }
    }
    public static void main(String[] args) {
        P282ExpressionAddOperators main = new P282ExpressionAddOperators();
        main.test();
    }
    

}

