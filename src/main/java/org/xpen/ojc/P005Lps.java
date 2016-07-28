package org.xpen.ojc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class P005Lps {
    
    public String longestPalindrome(String s) {
        String reverseS = reverse(s);
        List<String> lpsList = lcs(s, reverseS);
        //System.out.println(lpsList);
        
        for (int i = lpsList.size() - 1; i >= 0; i--) {
            String lps = lpsList.get(i);
            int startIndex1 = s.indexOf(lps);
            
            int startSearchPos = 0;
            while (true) {
                int startIndex2 = reverseS.indexOf(lps, startSearchPos);
                if (startIndex2 == -1) {
                    break;
                }
                int expectedStartIndex2 = s.length() - startIndex1 - lps.length();
                
                if (startIndex2 == expectedStartIndex2) {
                    return lps;
                } else {
                    startSearchPos = startIndex2 + 1;
                }
            }
        }
                
        return "";
    }
    
    public String reverse(final String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }
    
    //longest common substring
    //bottleneck 2 37%
    public List<String> lcs(String s, String t) {
        List<String> result = new ArrayList<String>();
        
        int N1 = s.length();

        // concatenate two string with intervening '\1'
        String text  = s + '\1' + t;
        int N  = text.length();

        // compute suffix array of concatenated text
        SuffixArray suffix = new SuffixArray(text);
        //ManberSuffixArray suffix = new ManberSuffixArray(text);

        // search for longest common substring
        String lcs = "";
        result.add(lcs);
        for (int i = 1; i < N; i++) {

            // adjacent suffixes both from first text string
            if (suffix.index(i) < N1 && suffix.index(i-1) < N1) continue;

            // adjacent suffixes both from secondt text string
            if (suffix.index(i) > N1 && suffix.index(i-1) > N1) continue;

            // check if adjacent suffixes longer common substring
            int length = suffix.lcp(i);
            //if (length > lcs.length()) {
                lcs = text.substring(suffix.index(i), suffix.index(i) + length);
                result.add(lcs);
            //}
        }
        
        Collections.sort(result, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        });
        
        return result;
    }    
    
    public void test(String s) {
        String result = s + "-->\n" + longestPalindrome(s);
        //System.out.println(result);
    }
    
    public static void main(String[] args) {
        Date startTime = new Date();
        P005Lps main = new P005Lps();
        main.test("abacdefg");
        main.test("abcabbaabc");
        main.test("12134xy43121");
        main.test("12134abbaX43121");
        main.test("12345abbaX54321");
        for (int i=0;i<8000;i++) {
        main.test("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabcaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        }
        Date endTime = new Date();
        System.out.println("cost:"+ (endTime.getTime()-startTime.getTime())/1000+ "s");
        
//        ManberSuffixArray m = main.new ManberSuffixArray("12345abbaX54321");
//        m.show();
        
        //System.out.println(main.lcs("abacdefg", "gfedcaba"));
        
//        System.out.println("  i ind lcp rnk select");
//        System.out.println("---------------------------");
//
//        String s = "ABRACADABRA!";
//        SuffixArray suffix = main.new SuffixArray(s);
//        for (int i = 0; i < s.length(); i++) {
//            int index = suffix.index(i);
//            String ith = "\"" + s.substring(index, Math.min(index + 50, s.length())) + "\"";
//            assert s.substring(index).equals(suffix.select(i));
//            int rank = suffix.rank(s.substring(index));
//            if (i == 0) {
//                System.out.printf("%3d %3d %3s %3d %s\n", i, index, "-", rank, ith);
//            } else {
//                int lcp = suffix.lcp(i);
//                System.out.printf("%3d %3d %3d %3d %s\n", i, index, lcp, rank, ith);
//            }
//        }
        
    }
    
    private class Suffix implements Comparable<Suffix> {
        private final String text;
        private final int index;

        private Suffix(String text, int index) {
            this.text = text;
            this.index = index;
        }
        private int length() {
            return text.length() - index;
        }
        private char charAt(int i) {
            return text.charAt(index + i);
        }

        //bottleneck 3 14%
        public int compareTo(Suffix that) {
            if (this == that) return 0;  // optimization
            int lengthA = this.length();
            int lengthB = that.length();
            int N = Math.min(lengthA, lengthB);
            for (int i = 0; i < N; i++) {
                char charA = this.charAt(i);
                char charB = that.charAt(i);
                if (charA < charB) return -1;
                if (charA > charB) return 1;
            }
            return lengthA - lengthB;
        }

        public String toString() {
            return text.substring(index);
        }
    }
    
    private class SuffixArray {
        private Suffix[] suffixes;

        //bottleneck 1 44%
        public SuffixArray(String text) {
            int N = text.length();
            this.suffixes = new Suffix[N];
            for (int i = 0; i < N; i++)
                suffixes[i] = new Suffix(text, i);
            Arrays.sort(suffixes);
        }

        public int length() {
            return suffixes.length;
        }

        public int index(int i) {
            if (i < 0 || i >= suffixes.length) throw new IndexOutOfBoundsException();
            return suffixes[i].index;
        }

        public int lcp(int i) {
            if (i < 1 || i >= suffixes.length) throw new IndexOutOfBoundsException();
            return lcp(suffixes[i], suffixes[i-1]);
        }

        // longest common prefix of s and t
        private int lcp(Suffix s, Suffix t) {
            int N = Math.min(s.length(), t.length());
            for (int i = 0; i < N; i++) {
                if (s.charAt(i) != t.charAt(i)) return i;
            }
            return N;
        }

        public String select(int i) {
            if (i < 0 || i >= suffixes.length) throw new IndexOutOfBoundsException();
            return suffixes[i].toString();
        }

        public int rank(String query) {
            int lo = 0, hi = suffixes.length - 1;
            while (lo <= hi) {
                int mid = lo + (hi - lo) / 2;
                int cmp = compare(query, suffixes[mid]);
                if (cmp < 0) hi = mid - 1;
                else if (cmp > 0) lo = mid + 1;
                else return mid;
            }
            return lo;
        }

        // compare query string to suffix
        private int compare(String query, Suffix suffix) {
            int N = Math.min(query.length(), suffix.length());
            for (int i = 0; i < N; i++) {
                if (query.charAt(i) < suffix.charAt(i)) return -1;
                if (query.charAt(i) > suffix.charAt(i)) return +1;
            }
            return query.length() - suffix.length();
        }

    }
    
    private class ManberSuffixArray {
        private int N;               // length of input string
        private String text;         // input text
        private int[] index;         // offset of ith string in order
        private int[] rank;          // rank of ith string
        private int[] newrank;       // rank of ith string (temporary)
        private int offset;
        private Suffix[] suffixes;
       
        public ManberSuffixArray(String s) {
            N    = s.length();
            text = s;
            index   = new int[N+1];
            rank    = new int[N+1];
            newrank = new int[N+1];

            // sentinels
            index[N] = N; 
            rank[N] = -1;

            msd();
            doit();
            show();
        }

        // do one pass of msd sorting by rank at given offset
        private void doit() {
            for (offset = 1; offset < N; offset += offset) {
                //System.out.println("offset = " + offset);

                int count = 0;
                for (int i = 1; i <= N; i++) {
                    if (rank[index[i]] == rank[index[i-1]]) count++;
                    else if (count > 0) {
                        // sort
                        int left = i-1-count;
                        int right = i-1;
                        quicksort(left, right);

                        // now fix up ranks
                        int r = rank[index[left]];
                        for (int j = left + 1; j <= right; j++) {
                            if (less(index[j-1], index[j]))  {
                                r = rank[index[left]] + j - left; 
                            }
                            newrank[index[j]] = r;
                        }

                        // copy back - note can't update rank too eagerly
                        for (int j = left + 1; j <= right; j++) {
                            rank[index[j]] = newrank[index[j]];
                        }

                        count = 0;
                    }
                }
            }
        }

        // sort by leading char, assumes extended ASCII (256 values)
        private void msd() {
            // calculate frequencies
            int[] freq = new int[256];
            for (int i = 0; i < N; i++)
                freq[text.charAt(i)]++;

            // calculate cumulative frequencies
            int[] cumm = new int[256];
            for (int i = 1; i < 256; i++)
                cumm[i] = cumm[i-1] + freq[i-1];

            // compute ranks
            for (int i = 0; i < N; i++)
                rank[i] = cumm[text.charAt(i)];

            // sort by first char
            for (int i = 0; i < N; i++)
                index[cumm[text.charAt(i)]++] = i;
        }

        public int index(int i) {
            return suffixes[i].index;
        }

        public int lcp(int i) {
            if (i < 1 || i >= N) throw new IndexOutOfBoundsException();
            return lcp(suffixes[i], suffixes[i-1]);
        }

        // longest common prefix of s and t
        private int lcp(Suffix s, Suffix t) {
            int N = Math.min(s.length(), t.length());
            for (int i = 0; i < N; i++) {
                if (s.charAt(i) != t.charAt(i)) return i;
            }
            return N;
        }
        
        public void show() {
            //String texttext = text + text;  // make cyclic
            //System.out.println("j, rank[index[j]], index[j]");
            suffixes = new Suffix[N];
            for (int i = 0; i < N; i++) {
                suffixes[i] = new Suffix(text, index[i]);
                //String s = text.substring(index[i]);
                //System.out.println(s + " " + i + " " + rank[index[i]] + " " + index[i]);
            }
            //System.out.println();
        }
        
        private boolean less(int v, int w) {
            if (v + offset >= N) v -= N;
            if (w + offset >= N) w -= N;
            return rank[v + offset] < rank[w + offset];
        }

        // swap pointer sort indices
        private void exch(int i, int j) {
            int swap = index[i];
            index[i] = index[j];
            index[j] = swap;
        }


        // SUGGEST REPLACING WITH 3-WAY QUICKSORT SINCE ELEMENTS ARE
        // RANKS AND THERE MAY BE DUPLICATES
        void quicksort(int lo, int hi) { 
            if (hi <= lo) return;
            int i = partition(lo, hi);
            quicksort(lo, i-1);
            quicksort(i+1, hi);
        }

        int partition(int lo, int hi) {
            int i = lo-1, j = hi;
            int v = index[hi];

            while (true) { 

                // find item on left to swap
                while (less(index[++i], v))
                    if (i == hi) break;   // redundant

                // find item on right to swap
                while (less(v, index[--j]))
                    if (j == lo) break;

                // check if pointers cross
                if (i >= j) break;

                exch(i, j);
            }

            // swap with partition element
            exch(i, hi);

            return i;
        }
    }
}

