/** 
	A class that provides a de la Briandais tree implementation.
	Modified code from assignment 1 that initialzies the DLB with the 256 extended ASCII
	characters in order to use with the LZWmod class. 
	Class: CS 1501, 10/01/2018
 	@author Nicolas Leo

 */
public class DLB
{
	private static Node head;			// reference to 1st node of the DLB.  
	private static Node lastFound; 		// Reference to last node found
	private static Node lastParent;		// Reference to parent of the last found
	private static Node lSibling;		// Reference to left sibling;

	private static int R; 						// Size of the alphabet
	private static boolean initialized = false;	// Keeps track if the DLB is initialized yet

    /**
     * Initializes the symbol table with the R ASCII symbols.
     */
    public DLB (int R)
    {
    	this.R = R;
		if (initialized) return; // Only initialize the DLB once

		head = new Node('\0', 0);
		Node temp = head; 
		for (int i = 1; i < R ; i++) 
		{
			temp.sibling = new Node((char) i, i);
			temp = temp.sibling;
		}

		lastFound = head;
		lastParent = null;
		initialized = true;
    }


	/** 
		Allows for the addition of a string by only addition the last character. This method
		must be called after subsequent appends to the String builder object. 
	 	@param s Stringbuilder to be put into the ST
	 	@param code Code to add to the ST
		@return true if successful and resets the search. 
	 */
	public boolean put(StringBuilder s, int code)
	{
		if (s != null && s.length() > 0 && code > -1)
		{
			char ch = s.charAt(s.length() - 1); // Current character of the prefix

			// Resume searching from last position in the DLB
			Node temp = lastFound, parent = lastParent;

			if (temp == null) // There are no nodes on this level from the parent
			{
				parent.child = new Node(ch, code);

				lastFound = head; 	// Search will start at the beginning next time
				parent = null;
				lSibling = null;

				return true;
			}

			while(true) // Traverse the DLB until a match is found, or add a new prefix
			{
				if (ch > temp.data) 
				{
					if(temp.sibling != null) // Check the siblings
					{	lSibling = temp;	temp = temp.sibling;}
				    else // No siblings
			    	{
			    		temp.sibling = new Node(ch, code);

						lastFound = head; 	// Search will start at the beginning next time
						lSibling = lastParent = null;

						return true; // Code of the parent node
			    	}
			    }
			    else if (ch == temp.data)  // Matching prefix found
				{
					// Don't add anything since the code word is already in the ST. 
					lastFound = head; 	// Search will start at the beginning next time
					lSibling = lastParent = null;

	    			return true; // Return the corresponding code
				}
				else  // ch < temp.data 
				{
					Node temp2 = new Node(ch, code); // Create a new node

					if (parent.child == temp) // temp is the 1st node in the list 
					{
						temp2.sibling = temp; // New node will be temp's L sibling
						parent.child = temp2; // New node will be the 1st child in the list
					}
					else
					{
						lSibling.sibling = temp2;
						temp2.sibling = temp;						
					}

					lastFound = head; 	// Search will start at the beginning next time
					lSibling = lastParent = null;

					return true; // Code of the parent node
				}
			}
		}

		return false;
	}	

	/** 
		Similar to search, except this method remembers its position in the symbol table.
		This will only search the last character added to the StringBuilder and assumes
		that this method was called starting when the SB only had 1 character. 
		Use search1() to reset the search from the beginning. 
	 	@param s the string to be searched for
	 	@return value of the longest codeword in the Symbol table
	 */ 
	public int searchPrefix(StringBuilder s)
	{
		if (s != null && s.length() > 0)
		{
			char ch = s.charAt(s.length() - 1); 	// Current character of the prefix

			// Resume searching from last position in the DLB
			Node temp = lastFound, parent = lastParent;

			if (temp == null) // There are no nodes on this level from the parent
				return parent.val; // put() will add value here if called.


			while(true) // Traverse the DLB until a match is found, or add a new prefix
			{
				if (ch > temp.data) 
				{
					if(temp.sibling != null) // Check the siblings
					{ lSibling = temp;	temp = temp.sibling;}
				    else // No siblings
			    	{
						lastFound = temp; // Next search continues where this one left off
						return parent.val; // Code of the parent node
			    	}
			    }
			    else if (ch == temp.data) 
				{
	    			lastParent = temp; // Next search continues where this one left off
	    			lastFound = temp.child;
	    			lSibling = null;

	    			return temp.val;
				}
				else  // ch < temp.data 
				{
					lastFound = temp; 	// Next search continues where this one left off
					return parent.val; // Code of the parent node
				}
			}
		}

		return -1;
	}

	/** 
		Search the symbol table for a single char. This will also reinitialize the 
		positions in the ST for searchPrefix and put.
	 	@param s The string to be searched for
	 	@return -1 means string not found. >-1 is the corresponding code. 
	 */ 
    public int search1(StringBuilder s) 
    {
		if (initialized && s != null && s.length() == 1)
		{
	    	Node temp = head;
	        int code = -1, slen = s.length();
	        char ch = s.charAt(0);

            while (temp != null) 
            {
                if (ch == temp.data)
                { 
                	lastParent = temp;  lastFound = temp.child; 
                	return temp.val;
                }
                temp = temp.sibling;
            }
	    }
	    return -1;
    }

	/** 
		Search the symbol table for the desired StringBuilder
	 	@param s The string to be searched for
	 	@return -1 means string not found. >-1 is the corresponding code. 
	 */ 
    public int search(StringBuilder s) 
    {
		if (s != null && s.length() > 0)
		{
			Node temp = head;
	    	char ch;
	        int code = -1, slen = s.length();
	        int sl1 = slen - 1;
	       

	        for (int i = 0; i < slen; i++) 
	        {
	            ch = s.charAt(i);
	            while (true) 
	            {
	                if (ch == temp.data) 
	                {
	                    code = temp.val;
	                    if(temp.child != null) 
	                    {	temp = temp.child; break; }// Break to check next letter
	                    else if(i < sl1)   return -1;  // Stringbuilder isn't in ST
	                } 
	                else if (temp.sibling == null) { return -1; } 
	                else {	temp = temp.sibling;} // Char didn't match this node, 
	                						      // check next one
	            }
	        }
	        return code;
	    }
	    return -1;
    }
	public void reset()
	{
		Node temp = head; 
		int i = 0;
		for (; i < R ; i++) 
		{
			temp.child = null;
			temp = temp.sibling;
		}

		initialized = true;
		lastFound = head;
		lSibling = lastParent = null;
	}


	// Private inner class for implementing the DLB
	private class Node
	{
		private char data;
		private int val;
		private Node sibling, child;

		public Node(char c, int v)
		{
			data = c;
			val = v;
			sibling = null;
			child = null;
		}
	}
}