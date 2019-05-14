/****************************************************************
*	Author: Nicolas Leo                    						*
*	Class: CS 1501, 10/19/2018             						*
*	Assignment 2												*
*	Description: Java implementation of a PHP array 			*
****************************************************************/
import java.util.*;

public class PHPArray<V> implements Iterable<V>
{

	private Node<V>[] hashTable; // Parameter used for array data
	private Node<V> head, tail, curr;

	private static final int INIT_CAPACITY = 16;
    private int N,           // number of key-value pairs in the array
    			M;           // Physical size of the array

    /** Default constructor with an array size of 16.  */
    public PHPArray() 
    {
        this(INIT_CAPACITY);
    }

    /** Constructor with the array size set by the user.  */
	public PHPArray(int capacity)
	{
		N = 0;
        M = capacity;
        curr = head = tail = null;
        @SuppressWarnings("unchecked")
        Node<V> [] tempArray = new Node[capacity];
        hashTable = tempArray;
	}

///////////////////////////////////////////////////////////////////////////////////////////	
/////////////////////////////////	Inner classes 	///////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////	
	/**
		Simple method to return a new Iterator object on this PHPArray object.
		@return New iterator of type V
	*/
	public Iterator<V> iterator()
	{
		return new PHPIterator();
	}
	
	/** Simple Iterator for an array. */
	private class PHPIterator implements Iterator<V>
	{
		private Node<V> tempN, prev;
		
		private PHPIterator() { tempN = head;}
		
		public boolean hasNext() { return tempN != null;}
		
		public V next()
		{
			if(tempN != null)
			{
				prev = tempN;
				tempN = tempN.next;
				return prev.val;				
			}
			else return null;
		}
	}


	/** Private inner class for implementing a linked list */
	private class Node <V>
	{
		private String key;
		private V val;
		private Node<V> prev; // Previously added (key, value) pair
		private Node<V> next; // Next Node in the list

		public Node(String k, V v)
		{
			key = k;
			val = v;
			prev = next = null;
		}

		public String toString()
		{
			StringBuilder sb = new StringBuilder("Key: ");
			sb.append(key);
			sb.append(" Value: ");
			sb.append(val);
			return sb.toString();
		}
	}

	/**
		Pair<V> has two public instance variables:  
			key of type String
			value of type V 
	*/
	public static class Pair<V>
	{
		String key;
		V value;
        public Pair(String k, V v)
        {
            key = k;
            value = v;
        }
	}


///////////////////////////////////////////////////////////////////////////////////////////	
///////////////////////////////		PHPArray methods	///////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////	

    /**
     	Transpose the keys and values of the original array in a new PHPArray.  Note that 
     	this is NOT a mutator. Since the key for any PHPArray object must be a String, the 
     	array_flip() method will only work if value of the original PHPArray is also a 
     	String. If the value of the original array is not a String, the array_flip() method
     	should throw a ClassCastException. If in the original array, multiple keys had the 
     	same value, in the flipped array only the last original key will be preserved as 
     	a value (since the others will be replaced).
     */
    public PHPArray<String> array_flip() throws ClassCastException
    {
        PHPArray<String> newArr = new PHPArray<String>(M);
        Node<V> tempN = head;

        for (int i = 0; i < N; i++)
        {
            newArr.put((String) tempN.val, tempN.key);
            tempN = tempN.next;
        }

        return newArr;
    }

	/**
		This method will sort the values just like sort(), but instead of
		reassigning the keys to ints starting at 0, it will keep the keys as they
		were.
	*/
	public void asort() throws ClassCastException
	{
        // Only works on Comparable data types.
        if (!(head.val instanceof Comparable)) throw new ClassCastException();
            
		head = mergeSort(head);

		// Rehash the sorted values into a blank array using the same key values
		@SuppressWarnings("unchecked")
		Node<V> [] tempArray = new Node[M];

        int j = 0; 
        Node<V> tempN = head;
        for (int i = 0; i < N; i++)
        {
        	// Rehash the key, using linear probing to find the next available index
        	// in the new array if necessary.
	        for (j = hash(tempN.key); tempArray[j] != null; j = (j + 1) % M);
	        	
			tempArray[j] = tempN; // Move the existing node into the new array
	        tempN = tempN.next;
        }

        tail = tempArray[j];
        hashTable = tempArray;
	}

    /**
        Tells if a key-value pair with the given key exist in the symbol table.
        @return True if the key is in the table, else false.
    */
    public boolean contains(String key) 
    {   return get(key) != null;}

    /**
		This method returns the next (key, value) pair within a new Pair<V> object until 
		the end of the list is reached -- at which time it will return null.  
		@return next (key, value) pair as Pair<V>
	*/
    public Pair<V> each()
    {
    	if (curr!= null) 
    	{
            Pair<V> p = new Pair<V>(curr.key, curr.val);
    		curr = curr.next;
    		return p;
    	}
    	else 
    		return null;
    }


    /**
        Overloaded method to handle if the user provides an integer as a key
        @param key Integer key value
        @return Value for the given key, or null if the key isn't in the table
    */
    public V get (int key)
    {   return get(Integer.toString(key));}

	/**
		Returns the value for a key
		@param key The string of the key
		@return Value for the given key, or null if the key isn't in the table
	*/
	public V get(String key)
	{
		// System.out.println("Hashing \"" + key + "\" to " + hash(key) + " for size " + M);
		// System.out.println("Actual table size is " + hashTable.length);
	    for (int i = hash(key); hashTable[i] != null; i = (i + 1) % M)
	    	// If the key matches the key at the index, return the value
	        if (hashTable[i].key.equals(key)) 
	        	return hashTable[i].val;
	
        return null;	
	}


    /**
    	Hash function for keys
    	@param key String key to generate a hash value for
    	@return A hased value between 0 and M-1
    */
    private int hash(String key) 
    {   return (key.hashCode() & 0x7fffffff) % M;}

    /** 
        Tells if the symbol table is empty? 
        @return True if empty, else false.
    */
    public boolean isEmpty() 
    {   return N == 0; }


	/**
		Returns an ArrayList<String> of the keys, in the order they were added to the table
		@return Arraylist<String> of the values.
	*/
	public ArrayList<String> keys()
	{
		ArrayList<String> al = new ArrayList<String>(N);
		Node<V> tempN = head;
		while(tempN != null)
		{
			al.add(tempN.key);
			tempN = tempN.next;
		}
		return al;
	}

	/** @return Returns the logical size of the array */
	public int length()
	{  return N; }

    /**
    	Overloaded method to allow for integers to be used as keys
    	@param key Integer that will be transformed into a String key
    	@param val Parameterized value
    */
    public void put(int k, V v)
    {   put(Integer.toString(k), v);}

    /**
  		Inserts the specified key-value pair into the symbol table, overwriting the old 
  		value with the new value if the symbol table already contains the specified key.
  		Deletes the specified key (and its associated value) from this symbol table
  		if the specified value is null.
 		
  		@param  key String representing the key
  		@param  val Parameterized value
     */
    public void put(String key, V val) 
    {
        if (val == null){ unset(key); return;}

        // double table size if 50% full
        if (N >= M/2) resize(2*M);

        int i = hash(key);
        for (; hashTable[i] != null; i = (i + 1) % M) 
        {
        	// Overwrite the value if the same key is added more than once
            if (hashTable[i].key.equals(key))
            { 
            	hashTable[i].val = val; 
            	return;
            }
        }

        if (N == 0) // Set head node if list is empty, 
        {
        	curr = tail = head = new Node<V>(key, val);
        }
        else
        {
        	tail.next = new Node<V>(key, val);
        	tail.next.prev = tail;
        	tail = tail.next;
        }

        hashTable[i] = tail;
        N++;
    }

    /**
    	Resizes the hash table to the given capacity by re-hashing all of the keys.
    	@param capacity Integer value of the new capacity
    */
    private void resize(int capacity) 
    {
    	System.out.printf("\t\tSize: %d -- resizing array from %d to %d\n", N, M, capacity);

        M = capacity;
        @SuppressWarnings("unchecked")
        Node<V> [] tempArray = new Node[M];
        
        // Similar to put(), but no new nodes are created 
        int j; 
        Node<V> tNode = head;
        for (int i = 0; i < N; i++)
        {
        	// Rehash the key. Then use linear probing to find the next available index
        	// in the new array.
	        for (j = hash(tNode.key); tempArray[j] != null; j = (j + 1) % M);
	        	
			tempArray[j] = tNode; // Move the existing node into the new array
	        tNode = tNode.next;
        }

        hashTable = tempArray;
    }

    /** 
        Re-initializes the iteration such that each() will again go through the 
        (key, value) pairs of the PHPArray. 
    */
    public void reset()
    {   curr = head; }

    /** Prints out the contents of the entire physical array. */
	public void showTable()
	{
		System.out.println("\tRaw Hash Table Contents:");
		for (int i = 0; i < M ;i++)
			System.out.printf("%d: %s\n", i, hashTable[i]);
	}

    /**
    	@return The number of key-value pairs in the symbol table
    */
    public int size() 
    {
        return N;
    }

    /**
    	Sort does the following:
			- Sort the values using the Comparable interface (if the data is not
					Comparable you should throw an exception when this method is tried)
			- Assign new keys to the values starting at 0 and ending at length()-1
			- Have the linked access also be the sorted result (i.e. an iterator()
					should iterate in sorted order)
		The sort is done using MergeSort which is O(NlgN))
	*/
	public void sort() throws ClassCastException
	{
        // Only works on Comparable data types.
        if (!(head.val instanceof Comparable)) throw new ClassCastException();

		head = mergeSort(head);

		//Change all of the key values to integers
		Node<V> tempN = head;
		for (int i = 0; i < N; i++)
		{
			tempN.key = Integer.toString(i);
			tempN = tempN.next;
		}

		// Rehash the sorted values into a blank array using the integer key values
		// Similar to put(), but no new nodes are created 
		@SuppressWarnings("unchecked")
		Node<V> [] tempArray = new Node[M];

        int j = 0; 
        tempN = head;
        for (int i = 0; i < N; i++)
        {
        	// Rehash the key. Then use linear probing to find the next available index
        	// in the new array.
	        for (j = hash(tempN.key); tempArray[j] != null; j = (j + 1) % M);
	        
			tempArray[j] = tempN; // Move the existing node into the new array
	        tempN = tempN.next;
        }

        tail = tempArray[j];
        hashTable = tempArray;
	}


    /** MergeSort implementation for Node<V> */
    private Node<V> mergeSort(Node<V> leftN) throws ClassCastException
    { 
    	// A single node is by definition sorted
        if (leftN == null || leftN.next == null) { return leftN;} 

        Node<V> rightN = halve(leftN); 
  
        // Sort left and right halves 
        leftN = mergeSort(leftN); 
        rightN = mergeSort(rightN); 
  
        // Merge the two sorted halves 
        return merge(leftN, rightN); 
    } 
  
    /**	Splits the linked list in half. */
    private Node<V> halve(Node<V> tempN)
    { 
        Node<V> faster = tempN, slower = tempN; 
        while (faster.next != null && faster.next.next != null) 
        { 
            faster = faster.next.next; 
            slower = slower.next; 
        } 
        tempN = slower.next; 
        slower.next = null; 
        return tempN; 
    } 


    /** Merges to the halves together again */
    private Node<V> merge(Node<V> l, Node<V> r) throws ClassCastException
    { 
        // If left list is empty, return the right list
        if (l == null) { return r;} 
  
        // If right  list is empty, return the left list
        if (r == null) { return l;} 
  
        // Check which value is smaller
        if (((Comparable) l.val).compareTo((Comparable) r.val) < 0) 
        { 
            l.next = merge(l.next, r); 
            l.next.prev = l; 
            l.prev = null; 
            tail = l.next;
            return l; 
        } 
        else 
        { 
            r.next = merge(l, r.next); 
            r.next.prev = r; 
            r.prev = null; 
            tail = r.next;
            return r; 
        } 
    } 

    /** 
        Overloaded version of unset() in order to accommodate integer keys.
        Deletes the key (and associated value) from the symbol table
        @param key Integer representation of a key
    */
    public void unset(int key)
    {
        unset(Integer.toString(key));
    }

    /**
		Deletes the key & its value from the array
		@param key The String key to delete from the array
	*/
	public void unset(String key)
	{  
		// Find the key in the table
        for (int i = hash(key); hashTable[i] != null; i = (i + 1) % M) 
        {
            if (hashTable[i].key.equals(key)) 
            { 	
		        // First need to check if deleting the head or tail node
            	if(hashTable[i] == head)
            	{
            		if(N > 1) head = head.next;
            		else { 	  head = null; tail = null;}
            	}
				else if(hashTable[i] == tail) 
				{ 
					tail = tail.prev; 
					tail.next = null;
				}
				else 
				{	
					hashTable[i].prev.next = hashTable[i].next; 
					hashTable[i].next.prev = hashTable[i].prev;
				}

				hashTable[i] = null; // Delete this (key, value) from the table

		        // Rehash the remaining keys in this cluster (if there are any)
		        int j;
		        for (i = (i + 1) % M; hashTable[i] != null; i = (i + 1) % M) 
		        {
		        	j = hash(hashTable[i].key);
			        for (; i != j && hashTable[j] != null; j = (j + 1) % M);

			        System.out.printf("\t\tKey %s rehashed...\n\n", hashTable[i].key);
			        // If key rehashes to the same index, do nothing. Otherwise, 
			        // move the corresponding node into the new index.
			        if (i != j){ hashTable[j] = hashTable[i]; hashTable[i] = null;}
		        }

		        N--;
		        // halves size of array if it's 12.5% full or less
		        if (N > 0 && N <= M/8) resize(M/2);	
            }
        }
	}


	/**
		Returns an ArrayList<V> of the values, in the order they were added to the array
		@return Arraylist<V> of the values
	*/
	public ArrayList<V> values()
	{
		ArrayList<V> al = new ArrayList<V>(N);
		Node<V> tempN = head;
		while(tempN != null)
		{
			al.add(tempN.val);
			tempN = tempN.next;
		}
		return al;
	}
}