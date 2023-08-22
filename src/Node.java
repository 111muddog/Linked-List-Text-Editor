
/*
Author: Angus Webb
Created: February 11th, 2022
Class Description: Node class for linked list. contains pointers for next/previous, and each node holds a string and an int for referencing.
 */
public class Node {
    String line; //data
    Node next; //next node
    Node prev; //previous node
    int counter; //the reference value that each node stores
    public Node(Node prev, String line, Node next) {
        this.prev = prev;
        this.line = line;
        this.next = next;
    }
}
