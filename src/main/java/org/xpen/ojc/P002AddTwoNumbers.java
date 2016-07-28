package org.xpen.ojc;


public class P002AddTwoNumbers {
    
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        int first  = l1.val + l2.val;
        Carry carry = new Carry();
        carry.val = 0;
        if (first >= 10) {
            first -= 10;
            carry.val = 1;
        }
        ListNode result = new ListNode(first);
        ListNode end = result;
        
        ListNode currentL1 = l1;
        ListNode currentL2 = l2;
        
        
        while (currentL1.next != null && currentL2.next != null) {
            
            currentL1 = currentL1.next;
            currentL2 = currentL2.next;
            end = oneAddTurn(carry, end, currentL1.val, currentL2.val);
        }
        
        if (currentL1.next != null) {
            while (currentL1.next != null) {
                currentL1 = currentL1.next;
                end = oneAddTurn(carry, end, currentL1.val, 0);
            }
        } else if  (currentL2.next != null) {
            while (currentL2.next != null) {
                currentL2 = currentL2.next;
                end = oneAddTurn(carry, end, 0, currentL2.val);
            }
        }
        
        if (carry.val == 1) {
            end.next = new ListNode(carry.val);
            end = end.next;
        }
        return result;
    }
    
    private ListNode oneAddTurn(Carry carry, ListNode end, int val1, int val2) {
        int current  = val1 + val2 + carry.val;
        //System.out.println("--->" +current);
        carry.val = 0;
        if (current >= 10) {
            current -= 10;
            carry.val = 1;
        }
        end.next = new ListNode(current);
        end = end.next;
        return end;
    }
    
    
    public static void main(String[] args) {
        P002AddTwoNumbers main = new P002AddTwoNumbers();
        ListNode l1 = main.new ListNode(2);
        ListNode l12  = main.new ListNode(4);
        ListNode l13  = main.new ListNode(3);
        
        l1.next = l12;
        l12.next = l13;
        
        ListNode l2 = main.new ListNode(5);
        ListNode l22  = main.new ListNode(6);
        ListNode l23  = main.new ListNode(4);
        
        l2.next = l22;
        l22.next = l23;
        
        ListNode result = main.addTwoNumbers(l1, l2);
        printListNode(l1);
        printListNode(l2);
        printListNode(result);
    }
    
    private static void printListNode(ListNode listNode) {
        ListNode current = listNode;
        while (current != null) {
            System.out.print(current.val);
            current = current.next;
        }
        System.out.println();
    }
    
    private class Carry {
        int val;
    }
    
    private class ListNode {
        int val;
        ListNode next;
        ListNode(int x) { val = x; }
    }

}

