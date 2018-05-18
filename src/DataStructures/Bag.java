/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;
import IO.StdIn;
import IO.StdOut;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  The <tt>Bag</tt> class represents a bag (or multiset) of 
 *  generic items. It supports insertion and iterating over the 
 *  items in arbitrary order.
 *  <p>
 *  This implementation uses a singly-linked list with a static nested class Node.
 *  See {@link LinkedBag} for the version from the
 *  textbook that uses a non-static nested class.
 *  The <em>add</em>, <em>isEmpty</em>, and <em>size</em> operations
 *  take constant time. Iteration takes time proportional to the number of items.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/13stacks">Section 1.3</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class Bag<Item> implements Iterable<Item>,Serializable {
    private int N;               // number of elements in bag
    private Node<Item> first;    // beginning of bag

    // helper linked list class
    private class Node<Item> implements Serializable{
        private Item item;
        private Node<Item> next;
        private Node<Item> prev;
    }

    /**
     * Initializes an empty bag.
     */
    public Bag() {
        first = null;
        N = 0;
    }

    /**
     * Is this bag empty?
     * @return true if this bag is empty; false otherwise
     */
    public boolean isEmpty() {
        return first == null;
    }

    /**
     * Returns the number of items in this bag.
     * @return the number of items in this bag
     */
    public int size() {
        return N;
    }

    /**
     * Adds the item to this bag.
     * @param item the item to add to this bag
     */
    public void add(Item item) {
        Node<Item> oldfirst = first;
        
        first = new Node<Item>();
        first.item = item;
        first.next = oldfirst;
        first.prev = null;
        
        if(oldfirst != null)
            oldfirst.prev = first;
        
        N++;
    }


    /**
     * Returns an iterator that iterates over the items in the bag in arbitrary order.
     * @return an iterator that iterates over the items in the bag in arbitrary order
     */
    public Iterator<Item> iterator()  {
        return new ListIterator<Item>(first,this);  
    }

    // an iterator, doesn't implement remove() since it's optional
    private class ListIterator<Item> implements Iterator<Item> {
        private Node<Item> current;
        private Node<Item> previous;
        private Bag bag;
        
        public ListIterator(Node<Item> first, Bag bag) {
            current = first;
            previous = null;
            this.bag = bag;
        }

        public boolean hasNext()  { return current != null;                     }
        
        public void remove()      { 

            if(current == null){
               previous.prev.next = current;
            }
            else if(previous.prev == null){
               bag.first = current;
               if(current != null)
                current.prev = null;
            }
            else{
                previous.prev.next = current;
                current.prev = current.prev.prev;
            }
        
            N--;
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item item = current.item;
            previous = current;
            current = current.next; 
            return item;
        }
    }

    /**
     * Unit tests the <tt>Bag</tt> data type.
     */
    public static void main(String[] args) {
        Bag<String> bag = new Bag<String>();
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            if(item.equalsIgnoreCase("q"))
                break;
            bag.add(item);
        }

        StdOut.println("size of bag = " + bag.size());
        for (String s : bag) {
            StdOut.println(s);
        }
        StdOut.println("----------------------");
        
        Iterator<String> it = bag.iterator();
        while(it.hasNext()){
            String item = it.next();
            if(item.equalsIgnoreCase("aabb"))
                it.remove();
        }
        
        
        StdOut.println("after remove size of bag ="+ bag.size());
        for (String s : bag) {
            StdOut.println(s);
        }
        
    }


}