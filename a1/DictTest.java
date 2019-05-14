// CS 1501
// 

import java.io.*;
import java.util.*;
/** 
	Simple test program to demonstrate DictInterface and its implementations.  
 	Note that the variable is type DictInterface but the object could be a MyDictionary
 	or it could be a DLB. This is fine since both of these types implement 
 	DictInterface.  In your assignment you will initially use MyDictionary for your
 	object but in Task 2 you will create a DLB which should be a much more efficient
 	implementation of the DictInterface.  The output to this program should be
 	identical with both types.
 	@author Sherif Khattab
 	Adapted from Dr. John Ramirez's CS 1501 Assignment 1
 */
public class DictTest
{
	public static void main(String [] args) throws IOException
	{
		Scanner fileScan = new Scanner(new FileInputStream("dict8.txt"));
		String dictType = args[0];
		// Command line argument should be DLB to use a DLB for the DictInterface
		// or any other string to use MyDictionary
		
		String st;
		StringBuilder sb;
		DictInterface D;
		if (dictType.equals("DLB"))
			D = new DLB();
		else
			D = new MyDictionary();
		
		while (fileScan.hasNext())
		{
			st = fileScan.nextLine();
			D.add(st);
		}

		String [] tests = {"abc", "abe", "abet", "abx", "ace", "acid", "hives",
						   "iodin", "inval", "zoo", "zool", "zurich"};
		for (int i = 0; i < tests.length; i++)
		{
			sb = new StringBuilder(tests[i]);
			int ans = D.searchPrefix(sb);
			System.out.print(sb + " is ");
			switch (ans)
			{
				case 0: System.out.println("not found");
					break;
				case 1: System.out.println("a prefix");
					break;
				case 2: System.out.println("a word");
					break;
				case 3: System.out.println("a word and prefix");
			}
		}
	}
}