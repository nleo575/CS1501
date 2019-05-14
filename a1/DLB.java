/** 
	A class that provides a de la Briandais tree implementation of DictInterface. 
	Class: CS 1501, 10/01/2018
 	@author Nicolas Leo
 	 

 */

public class DLB implements DictInterface
{
	// Store Strings in an ArrayList
	private Node root = null;	// reference to 1st node of the DLB.  
	private int words = 0;  	// number of words in the DLB

	/** 
		Add a new String to the DictInterface. If String should come before
		previous last string (i.e. it is out of order) sort the list.
		We are keeping the data sorted in this implementation of
		DictInterface to make searches a bit faster.
	 	@param s the string to be added
	 	@return true if the string was added successfully; false otherwise
	 */
	public boolean add(String s)
	{
		if (s != null && s.length() > 0)
		{
			if (root == null) 	// Initialize first node if empty
			{
				root = new Node(s.charAt(0));
				if(s.length() == 1) 
					root.child = new Node('^');
				else
				{
					Node temp = root;
		    		int slen = s.length();
		    		for (int i = 1; i < slen; i++) 
		    		{
		    			temp.child = new Node(s.charAt(i));
		    			temp = temp.child;
		    		}

	    			temp.child = new Node('^'); 
				}

				words = 1;
				return true;
			}

			int pos = 0,// Starting position of the string
				sl1 = s.length() -1;
			char ch = s.charAt(0);
			Node temp = root; 

			while(true)
			{
				if (ch > temp.data)
				{
					if(temp.sibling != null) // Check the siblings
					{
						temp = temp.sibling;
					} 	
				    else // No siblings
			    	{
			    		temp.sibling = new Node(ch);
			    		temp = temp.sibling; 
			    		int slen = s.length();
			    		pos++;
			    		for (; pos < slen; pos++) 
			    		{
			    			temp.child = new Node(s.charAt(pos));
			    			temp = temp.child;
			    		}

		    			temp.child = new Node('^'); 

						words++;
						return true;
			    	}
			    }
			    else if (ch == temp.data) 
				{
		    		if (pos < sl1) 
		    		{
		    			pos++; temp = temp.child; ch = s.charAt(pos);
		    		}
		    		//reached the end of the string, check child for terminator '^'
		    		else if (temp.child.data != '^')
		    		{
		    			temp = temp.child;
			    		Node temp2  = new Node(temp.data);
			    		temp2.child = temp.child;			
			    		temp2.sibling = temp.sibling;		
			    		temp.sibling = temp2; 				
			    		temp.data = '^';	

						words++;
						return true;		
		    		}
		    		else
		    			return true;
				}
				else  // ch < temp.data 
				{
		    		Node temp2  = new Node(temp.data); 	// duplicate the current node's data
		    		temp2.child = temp.child;			// link back to the temp's children
		    		temp2.sibling = temp.sibling;		// link to temp's sibling
		    		temp.sibling = temp2; 				// link to the temp node
		    		temp.data = ch;						// Swap out the data

		    		int slen = sl1 + 1;
		    		pos++;
		    		for (; pos < slen; pos++) 
		    		{
		    			temp.child = new Node(s.charAt(pos));
		    			temp = temp.child;
		    		}

	    			temp.child = new Node('^');

					words++;
					return true;
				}
			}
		}
		return false;
	}

	/** 
		The method below could be defined with various parameters.
	 	However, in our program, we will only use the version with
	 	the StringBuilder argument shown below.  This is so that we
	 	don't have the overhead of converting back and forth between
	 	StringBuilder and String each time we add a new character
	 	@param s the string to be searched for
	 	@return 0 if s is not a word or prefix within the DictInterface
	 	        1 if s is a prefix within the DictInterface but not a valid word
	 	        2 if s is a word within the DictInterface but not a prefix to other words
	 	        3 if s is both a word within the DictInterface and a prefix to other words
	 */   
	public int searchPrefix(StringBuilder s)
	{
		return searchPrefix(s, 0, s.length()-1);
	}

	/** 
		Same logic as method above.  However, now we can search a substring
		from start (inclusive) to end (inclusive) within the StringBuilder.
		@param s
		@param start
		@param end
	 	@return 0 if s is not a word or prefix within the DictInterface
	 	        1 if s is a prefix within the DictInterface but not a valid word
	 	        2 if s is a word within the DictInterface but not a prefix to other words
	 	        3 if s is both a word within the DictInterface and a prefix to other words
	*/
	public int searchPrefix(StringBuilder s, int start, int end)
	{
		if (root != null)
		{
			char ch = s.charAt(start);
			Node temp = root; 

			while(true)
			{
				if (ch > temp.data)
				{
					if(temp.sibling != null) // Check the siblings
					{
						temp = temp.sibling;
					} 	
				    else 	return 0;		// Not a word/prefix in the dictionary
						
			    }
			    else if (ch == temp.data) 
				{
		    		if (start < end) 
		    		{
		    			start++; temp = temp.child; ch = s.charAt(start);
		    		}
		    		// Check if word && prefix
		    		else if (temp.child.data == '^')
		    		{
						if (temp.child.sibling != null) return 3;

						return 2;
		    		}
		    		else 	return 1; // Only a prefix
				}
				else  	return 0; // ch < temp.data & not in the DLB
			}
		}	
		return 0; 
	}

	public int getNumWords()
	{
		return words;
	}

	// Private inner class for implementing the DLB
	private class Node
	{
		private char data;
		private Node sibling;
		private Node child;

		public Node(char c)
		{
			data = c;
			sibling = null;
			child = null;
		}
	}
}