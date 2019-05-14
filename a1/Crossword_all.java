/****************************************************************
*	Author: Nicolas Leo                    						*
*	Class: CS 1501, 10/01/2018             						*
*	Assignment 1												*
*	Description: Prints all solutions found even for DLB		*
*	I used this for testing 5a and 6a and 8b					*
*	Command line format: java Crossword dictType testFil.txt    *
*	Dict type can be "DLB" or any other string for MyDictionary *
****************************************************************/

import java.io.*;
import java.util.*;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
/** 
	This program can find a single solution to a given crossword puzze using the
	MyDictionary implementation of the DictInterface, or can also find all valid
	solutions to a given puzzle using the DLB implementation of the DictInterface
 	@author Nicolas Leo
 */
public class Crossword_all
{
		private static boolean DLB, 	// Used to change runs/outputs
							   canOpti;	// Tells whether the search can be be optimized
		private static int bsize, 		// Board size
						   bs1,			// Board size - 1, used for bound checking
						   backnum;		// How many cells to backtrack (optimization)	
		private static long nsoln=0; 	// Number of solutions found
							
		private static Cell [][] board;	// Cell objects hold info about the board
		private static StringBuilder rowStr[]; // Used to try different letters in rows
		private static StringBuilder colStr[]; // Used to try different letters in cols
		private static DictInterface D;	
		private static char alpha [] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
		'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
private static OperatingSystemMXBean osmxb = (OperatingSystemMXBean) 
								ManagementFactory.getOperatingSystemMXBean();
	public static void main(String [] args) throws IOException
	{

		Scanner dict = new Scanner(new FileInputStream("dict8.txt"));
		String dictType = args[0];

		// Command line argument should be DLB to use a DLB for the DictInterface
		// or any other string to use MyDictionary
		
		String st;
		StringBuilder sb;

		if (dictType.equals("DLB"))
		{
			D = new DLB();
			DLB = true; 
		}
		else
		{
			D = new MyDictionary();
			DLB = false;
		}

		while (dict.hasNext())
		{
			st = dict.nextLine();
			D.add(st);
		}

		dict.close();

		// read in the board
		Scanner sc = new Scanner(new FileInputStream(args[1]));
		bsize = sc.nextInt();
		bs1 = bsize - 1;
		board = new Cell[bsize][bsize];
		String line;

		rowStr = new StringBuilder[bsize];
		colStr = new StringBuilder [bsize];

		int rsubstrStart = 0, csubstrStart = 0;
		char cc; // Hold the current char from the file
		canOpti = false; 

		// Initialize the board and individual cells with data & where the substrings
		// start for the rows
		for (int i = 0; i < bsize ; i++ ) 
		{
			// Initialize the string builders
			rowStr[i] = new StringBuilder();
			colStr[i] = new StringBuilder();
			line = sc.next();
			for (int j = 0; j < bsize ; j++ ) 
			{
				cc = line.charAt(j);
				if(cc > '-') canOpti = true;
				// Else start remains unchanged from previous column
				if (cc == '-') 
				{
					rsubstrStart = j; csubstrStart = i; 
				}
				else
				{
					if (j == 0 || board[i][j-1].data == '-') 
						rsubstrStart = j; 

					if (i == 0 || board[i-1][j].data == '-') 
						csubstrStart = i; 
					else // Else start remains unchanged from previous cell above
						csubstrStart = board[i-1][j].css;
				}

				board[i][j] = new Cell(cc,rsubstrStart, csubstrStart);
			}
		}
		sc.close();

		// Set the booleans for if the current cell is the end of a substring
		// End of substrings need to be verified as whole word for word & prefix
		for (int i = 0; i < bsize ; i++ ) // End of row is always the end
		{
			for (int j = 0; j < bsize ; j++ ) // End of columns is always the end
			{
				if(board[i][j].data != '-')
				{
					// Check if cell is the end of a substring in this row
					if (j != bs1 && board[i][j+1].data != '-') board[i][j].rse = false;

					// Check if cell is the end of a substring in this column
					if (i != bs1 && board[i+1][j].data != '-') board[i][j].cse = false;
				}
			}
		}

		/*
			Because of the long runtime for certain test boards, I used an
			OperatingSystemMXBean object used to get total CPU time for this process. 
			This object was used as opposed to calculating the elapsed System time
			since it is resistant to any periods when the computer may go to sleep
			and is only incremented when the process is active on the CPU. 
		*/
		


		// Run the solve algorithm. If it returns True or more than 0 solutions found
		// The board has a solution. 
		if (solve(0, 0) || nsoln > 0)
		{
			// Used to store the CPU time for this process
			long time = osmxb.getProcessCpuTime();
            //Format and display run-time
            System.out.printf("%s run-time details for %s:\n\n", args[0], args[1]);
            System.out.printf("Elapsed time: %,d (ns).\n", time);
            System.out.printf("Elapsed time: %,6f (ms).\n", time/1000000.0);
            System.out.printf("Elapsed time: %,.6f (sec).\n", time/1000000000.0);
            System.out.printf("Elapsed time: %,.6f (mins).\n", time/60000000000.0);

            if (!DLB)
        	{	
        		System.out.println("\nSolution found:");
        		printbdnonDLB();
        	}
        	else System.out.printf("%,d solutions found.\n\n", nsoln);

		}
		else
		{
			// Used to store the CPU time for this process
			long time = osmxb.getProcessCpuTime();
            //Format and display run-time
            System.out.printf("%s run-time details for %s:\n\n", args[0], args[1]);
            System.out.printf("Elapsed time: %,d (ns).\n", time);
            System.out.printf("Elapsed time: %,6f (ms).\n", time/1000000.0);
            System.out.printf("Elapsed time: %,.6f (sec).\n", time/1000000000.0);
            System.out.printf("Elapsed time: %,.6f (mins).\n", time/60000000000.0);

			System.out.println("No solution found");
			System.out.println();
		}
	}

	private static void printbd()
	{
		System.out.printf("Solution %d took %d (ns):\n", nsoln, osmxb.getProcessCpuTime());
		for (int i = 0; i < bsize ; i++ ) 
			System.out.println(rowStr[i]);
		System.out.println();
	}

	private static void printbdnonDLB()
	{
		for (int i = 0; i < bsize ; i++ ) 
			System.out.println(rowStr[i]);
		System.out.println();
	}

	private static boolean solve (int row, int col)
	{
		int rprefix = 2;
		int cprefix = 2;
		int yfailcnt = 0;
		boolean ans = false, opti = false;
		switch (board[row][col].data) 
		{
			case '+':
			
				// Assume word is valid in both directions
				int ch = 0; // Used to append a character
				while (ch < 26)
				{
					
					// Append next char to the end of each Stringbuilder
					rowStr[row].append(alpha[ch]);
					colStr[col].append(alpha[ch]);

					// Search only if the x-substring started in a prior column 
					if(col != board[row][col].rss)// 
						rprefix = D.searchPrefix(rowStr[row], board[row][col].rss, col);


					// Search only if the y-substring started in a prior row
					if(row != board[row][col].css)// 
						cprefix = D.searchPrefix(colStr[col], board[row][col].css, row);

					// Check if the row & column are valid prefixes or words
					if (rprefix > 0 && cprefix > 0)
					{
						//Valid prefix/word
	
						// Optimization: 
						// If there's a fixed char below the cell see if appending it 
						// will be a valid prefix. If it isn't, this cell is invalid
						if (canOpti && row < bs1 && board[row+1][col].data > '-')
						{
							//System.out.println("Looking 1 row down");
							colStr[col].append(board[row+1][col].data);

							if(D.searchPrefix(colStr[col], board[row+1][col].css,row+1)==0)
								opti = true; 

							// delete the test char
					  		colStr[col].deleteCharAt(colStr[col].length() -1);
						}

						if (!opti) // Skip code if section above is true
						{
							// Check if cell is the end of a x-direction substring
							if (board[row][col].rse)  
							{
								if (rprefix > 1) // Verify valid word
								{
									// Check if cell is the end of a y-direction substring
									if (board[row][col].cse)
									{
										if (cprefix > 1) // Verify valid word
										{
											// Check if there are more columns
											if (col < bs1)  
											{
												ans = solve(row, col + 1);
											}
											// Check if there are more rows
											else if (row < bs1 ) 
											{
												ans = solve(row + 1, 0);
											}
											// no more rows, this is the last cell & word is
											// valid. this puzzle is solved
											else
											{
												if(!DLB)
													return true;
												else
												{
													
													printbd();
													nsoln++;
												}
											}  
										}
									} 
									else // more rows to check
									{
										// Check if there are more columns in row
										if (col < bs1)  
										{
											ans = solve(row, col + 1);
										}
										else if (row < bs1 ) // Check if there are more rows
										{
											ans = solve(row + 1, 0);
										}
										// no more rows, this is the last cell & word is
										// valid. this puzzle is solved
										else
										{
											if(!DLB)
												return true;
											else
											{
												
												printbd();
												nsoln++;
											}
										} 
									}
								}
							}
							// Cell isn't the end of a x-direction substring
							// Check if cell is the end of a y-direction substring
							else if (board[row][col].cse) 
							{	// Verify valid word
								if (cprefix > 1) ans = solve(row, col +1);
							}
							// in the middle of substrings in both directions
							// go to the next column
							else  ans = solve(row, col +1);	
						}
					}
	
					// Optimization to backtrack for a given length of chars
					if (backnum > 0) 
					{
						rowStr[row].deleteCharAt(rowStr[row].length() -1);
						colStr[col].deleteCharAt(colStr[col].length() -1);
						backnum--;
						return false;				
					}

					// Used to count if all letters failed in y-direction
					if(cprefix == 0) yfailcnt++; 

					if (!ans) // Ans if false by default
					{
						// Remove the appended character and try the next
						rowStr[row].deleteCharAt(rowStr[row].length() -1);
						colStr[col].deleteCharAt(colStr[col].length() -1);
						ch++;

						opti = false; 
						rprefix = 2;
						cprefix = 2;
						if(cprefix == 0) yfailcnt++;

						// All letters failed
						if(yfailcnt == 26) 
						{
							int x = 1; // Potentially backtrack multiple lines
							while(true)
							{
								if(board[row-x][col].data == '+') break; 
								else x++;
							}

							backnum = (bsize *x) -1; // Can be used to skip multiple lies
						}
					}
					else
					{
						if(!DLB)
							return true;
						else
						{
							printbd();
							nsoln++;
						}
					} 
				}

				return ans;

			case '-':

				rowStr[row].append('-');
				colStr[col].append('-');


				if (col < bs1)  // Check if there are more columns
				{
					ans = solve(row, col + 1);
				}
				// Check if there are more rows
				else if (row < bs1) 
				{
					ans = solve(row + 1, 0);
				}
				// no more rows, this is the last cell & word is
				// valid. this puzzle is solved
				else
				{
					if(!DLB)
						return true;
					else
					{	
						printbd();
						nsoln++;
					}
				} 

				if (!ans) 
				{
					// Remove the appended character and try the next
					rowStr[row].deleteCharAt(rowStr[row].length() -1);
					colStr[col].deleteCharAt(colStr[col].length() -1);
					if (backnum > 0) backnum--;
				}

				return ans;

			default: //there's an alpha char in the cell already

				rowStr[row].append(board[row][col].data);
				colStr[col].append(board[row][col].data);

				// Search only if the x-substring started in a prior column 
				if(col != board[row][col].rss)//
					rprefix = D.searchPrefix(rowStr[row], board[row][col].rss, col);


				// Search only if the y-substring started in a prior row
				if(row != board[row][col].css)// 
					cprefix = D.searchPrefix(colStr[col], board[row][col].css, row);
				
				// Check if the row & column have valid prefixes or words
				if (rprefix > 0 && cprefix > 0)
				{
					//Valid prefix/word

					// Optimization: 
					// If there's a fixed char below the cell see if appending it will be 
					// a valid prefix. If it isn't, this cell is invalid
					if (canOpti && row < bs1 && board[row+1][col].data > '-')
					{
						colStr[col].append(board[row+1][col].data);

						if (D.searchPrefix(colStr[col], board[row+1][col].css, row+1) ==0)
							opti = true; 

						// delete the test char
				  		colStr[col].deleteCharAt(colStr[col].length() -1);
					}
					
					if (!opti)
					{
						// Check if cell is the end of a x-direction substring
						if (board[row][col].rse)  
						{
							if (rprefix > 1) // Verify valid word
							{
								// Check if cell is the end of a y-direction substring
								if (board[row][col].cse)
								{
									if (cprefix > 1) // Verify valid word
									{
										if (col < bs1)  // Check if there are more columns
										{
											ans = solve(row, col + 1);
										}
										// Check if there are more rows
										else if (row < bs1 ) 
										{
											ans = solve(row + 1, 0);
										}
										// no more rows, this is the last cell & word is
										// valid. this puzzle is solved
										else
										{
											if(!DLB)
												return true;
											else
											{	
												printbd();
												nsoln++;
											}
										} 
									}
								} 
								else // more rows to check
								{
									// Check if there are more columns in row
									if (col < bs1)  
									{
										ans = solve(row, col + 1);
									}
									else if (row < bs1 ) // Check if there are more rows
									{
										ans = solve(row + 1, 0);
									}
									// no more rows, this is the last cell & word is
									// valid. this puzzle is solved
									else
									{
										if(!DLB)
											return true;
										else
										{
											printbd();
											nsoln++;
										}
									} 
								}
							}
						}
						// Cell isn't the end of a x-direction substring
						// Check if cell is the end of a y-direction substring
						else if (board[row][col].cse) 
						{	// Verify valid word
							if (cprefix > 1) ans = solve(row, col +1);
						}
						// in the middle of substrings in both directions
						// go to the next column
						else  ans = solve(row, col +1);
					}
				}

				if (backnum > 0) 
				{
					rowStr[row].deleteCharAt(rowStr[row].length() -1);
					colStr[col].deleteCharAt(colStr[col].length() -1);
					backnum--;

					return false;				
				}

				if (!ans) 
				{
					// Remove the appended character and try the next
					rowStr[row].deleteCharAt(rowStr[row].length() -1);
					colStr[col].deleteCharAt(colStr[col].length() -1);

					// Cell 1 or more rows above is invalid 
					if(cprefix == 0)
					{
						int x = 1; // Potentially backtrack multiple lines
						while(true)
						{
							if(board[row-x][col].data == '+') break; 
							else x++;
						}

						backnum = (bsize *x) -1;
					}				
				}

				return ans;
		}
	}

	private static class Cell
	{
		private char data; 	// + - or [a-z]
		private int rss; 	// which column of this row where the current substring starts
		private boolean rse;// Is this the end of a word in the row
		private int css;	// which row of this column where the current substring starts
		private boolean cse; // Is this the end of a word in the column
		public Cell(char d, int r, int c)
		{
			data = d;
			rss = r;
			rse = true;
			css = c;
			cse = true;
		}
	}
}