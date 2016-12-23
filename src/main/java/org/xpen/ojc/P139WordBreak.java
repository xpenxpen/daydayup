package org.xpen.ojc;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

public class P139WordBreak {
    
    public boolean wordBreak(String s, Set<String> wordDict) {
        int count = 0;
        Set<String> lookedUpWords = new HashSet<String>();
        boolean b = innerSolve(s, wordDict, count, lookedUpWords);
        return b;
    }
    
    private boolean innerSolve(String s, Set<String> wordDict, int count, Set<String> lookedUpWords) {
        if (lookedUpWords.contains(s)) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (wordDict.contains(s.substring(0, i+1))) {
                if (i==s.length()-1) {
                    //solved
                    return true;
                }
                String newS = s.substring(i+1);
                boolean solved = innerSolve(newS, wordDict, count, lookedUpWords);
                if (solved) {
                    return true;
                }
                
            }
        }
        lookedUpWords.add(s);
        return false;
    }

    public static void main(String[] args) {
        P139WordBreak main = new P139WordBreak();
        
        main.doOne("catsanddog", new String[]{"cat", "cats", "and", "sand", "dog"});
        main.doOne("a", new String[]{"a"});
        main.doOne("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab",
            new String[]{"a","aa","aaa","aaaa","aaaaa","aaaaaa","aaaaaaa","aaaaaaaa","aaaaaaaaa","aaaaaaaaaa"});
    }
    
    public void doOne(String s, String[] dict) {
        Set<String> wordDict = new HashSet<String>();
        for (String aWord: dict) {
            wordDict.add(aWord);
        }
        StopWatch sw = new StopWatch();
        
        sw.start();
        System.out.println(wordBreak(s, wordDict));
        sw.stop();
        System.out.println(sw.getTime(TimeUnit.MICROSECONDS));
        
    }

}

