package org.xpen.ojc;

import java.util.PriorityQueue;

public class P023MergeKSortedLists {
    
    class ListNode {
        int val;
        ListNode next;

        public ListNode(int x) {
            val = x;
        }
    }
    
    public void print(ListNode listNode) {
        System.out.print(listNode.val);
        listNode = listNode.next;
        while (listNode != null) {
            System.out.print("->" + listNode.val);
            listNode = listNode.next;
        }
    }
    public void test() {
        ListNode a1 = new ListNode(1);
        ListNode a2 = new ListNode(2);
        ListNode a3 = new ListNode(4);
        ListNode b1 = new ListNode(3);
        ListNode b2 = new ListNode(5);
        ListNode b3 = new ListNode(8);
        a1.next=a2;
        a2.next=a3;
        b1.next=b2;
        b2.next=b3;
        ListNode result = mergeKLists(new ListNode[]{a1,b1});
        print(result);
    }
    
    class Pair implements Comparable<Pair> {
        int v1;
        int v2;
        public Pair(int v1, int v2) {
            this.v1 = v1;
            this.v2 = v2;
        }
        @Override
        public int compareTo(Pair o) {
            if (v1 < o.v1) {
                return -1;
            } else if  (v1 > o.v1) {
                return 1;
            }
            return 0;
        }
        
    }
    
    public ListNode mergeKLists(ListNode[] lists) {
        PriorityQueue<Pair> priorityQueue = new PriorityQueue<Pair>();
        for (int i = lists.length - 1; i >= 0 ; i--) {
            if (lists[i] != null) {
                priorityQueue.add(new Pair(lists[i].val, i));
                lists[i] = lists[i].next;
            }
        }
        ListNode result;
        Pair head = priorityQueue.poll();
        if (head != null) {
            result = new ListNode(head.v1);
            int whichElement = head.v2;
            if (lists[whichElement] != null) {
                priorityQueue.add(new Pair(lists[whichElement].val, whichElement));
                lists[whichElement] = lists[whichElement].next;
            }
        } else {
            return null;
        }
        
        ListNode resultHead = result;
        while ((head = priorityQueue.poll()) != null) {
            
            ListNode newNode = new ListNode(head.v1);
            resultHead.next = newNode;
            resultHead = newNode;
            int whichElement = head.v2;
            if (lists[whichElement] != null) {
                priorityQueue.add(new Pair(lists[whichElement].val, whichElement));
                lists[whichElement] = lists[whichElement].next;
            }
        }
        
        return result;
    }

    public static void main(String[] args) {
        P023MergeKSortedLists main = new P023MergeKSortedLists();
        main.test();
    }
    

}

