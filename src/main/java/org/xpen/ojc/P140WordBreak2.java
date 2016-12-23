package org.xpen.ojc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

public class P140WordBreak2 {
    
    public List<String> wordBreak(String s, Set<String> wordDict) {
        List<String> result = new ArrayList<String>();
        int count = 0;
        Set<String> lookedUpWords = new HashSet<String>();
        LinkedList<String> oneAnswer = new LinkedList<String>();
        innerSolve(s, wordDict, count, lookedUpWords, result, oneAnswer);
        return result;
    }
    
    private boolean innerSolve(String s, Set<String> wordDict, int count, Set<String> lookedUpWords, List<String> result, LinkedList<String> oneAnswer) {
        boolean canSolve = false;
        if (lookedUpWords.contains(s)) {
            if (oneAnswer.size()>0) {
                oneAnswer.removeLast();
            }
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            String sub = s.substring(0, i+1);
            if (wordDict.contains(sub)) {
                oneAnswer.addLast(sub);
                if (i==s.length()-1) {
                    //solved
                    canSolve = true;
                    StringBuilder sb = new StringBuilder();
                    Iterator<String> iterator = oneAnswer.iterator();
                    while (iterator.hasNext()) {
                        String one = iterator.next();
                        if (sb.length()!=0) {
                            sb.append(' ');
                        }
                        sb.append(one);
                    }
                    result.add(sb.toString());
                }
                
                    String newS = s.substring(i+1);
                    boolean solved = innerSolve(newS, wordDict, count, lookedUpWords, result, oneAnswer);
                    if (solved) {
                        canSolve = true;
                    }
                
            }
        }
        
        if (!canSolve) {
            lookedUpWords.add(s);
        }
        if (oneAnswer.size()>0) {
            oneAnswer.removeLast();
        }
        return true;
    }

    public static void main(String[] args) {
        P140WordBreak2 main = new P140WordBreak2();
        
        main.doOne("catsanddog", new String[]{"cat", "cats", "and", "sand", "dog"});
        main.doOne("a", new String[]{"a"});
        main.doOne("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab",
            new String[]{"a","aa","aaa","aaaa","aaaaa","aaaaaa","aaaaaaa","aaaaaaaa","aaaaaaaaa","aaaaaaaaaa"});
        main.doOne("aaaaaaa", new String[]{"aaaa", "aaa"});
        main.doOne("aaaaaaa", new String[]{"aaaa","aa","a"});
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

