/** 
	An interface for a dictionary of words. The dictionary allows 
	searching for complete words as well as word prefixes.
	@author Sherif Khattab
	Adapted from Dr. John Ramirez's CS 1501 Assignment 1
 */
public interface DictInterface
{
	/** 
		Add a new String to the DictInterface
	 	@param s the string to be added
	 	@return true if the string was added successfully; false otherwise
	*/
	public boolean add(String s);
	
	

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
	public int searchPrefix(StringBuilder s);

	/** 
		Same logic as method above.  However, now we can search a substring
		from start (inclusive) to end (inclusive) within the StringBuilder.
		Depending on how you implement your main search algorithm, you may
		find this version to be more convenient or appropriate than the first
		one above.
		@param s
		@param start
		@param end
	 	@return 0 if s is not a word or prefix within the DictInterface
	 	        1 if s is a prefix within the DictInterface but not a valid word
	 	        2 if s is a word within the DictInterface but not a prefix to other words
	 	        3 if s is both a word within the DictInterface and a prefix to other words
	*/
	public int searchPrefix(StringBuilder s, int start, int end);
}