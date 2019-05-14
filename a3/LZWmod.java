/**
 @author Nicolas Leo, nll21@pitt.edu
 Project 3: LZW compression
 Compilation:  javac LZWmod.java
 Execution:    java LZWmod - [r] < input_file (compress)
 Execution:    java LZW + < input_file > input_name.file_type   (expand)
 Dependencies: BinaryStdIn.java BinaryStdOut.java, DLB.java
 
 Compress or expand binary input from standard input using LZW.
 Use r flag to turn on optional codebook reset
*/
public class LZWmod 
{
    private static final int R = 256;       // number of input chars
    private static       int L = 512;       // Max # of codes = 2^W, default is 512
    private static final int minL = L;      // Default number of codes, used in resets
    private static       int W = 9;         // codeword width, default is 9
    private static final int minW = W;
    private static final int maxW = 16;     // max codeword width is 16
    private static   boolean reset = false; // User elected to have the codebook
                                            // reset once it became full. Default is false 

    private static void compress() 
    {
        BinaryStdOut.write(reset);  // First bit of compressed file will tell if codebook
                                    // is reset when it becomes full
        StringBuilder sb = new StringBuilder();

        // Symbol table (ST) used to store the code words and values
        DLB st = new DLB(R);// DLB is modified in order to initialize with R size alphabet
        int code = -1,      // Code returned by the ST
            maxCode = R+1,  // R is reserved for EOF. Maximum code generated
            oldCode = -1;   // Last code before this append
        boolean empty = false; 

        while (!empty) 
        {
            while (! (empty = BinaryStdIn.isEmpty()))
            {
                sb.append(BinaryStdIn.readChar());
                code = st.searchPrefix(sb);
                if ( code > oldCode)  oldCode = code;
                else break; // Break when append doesn't change the code value             
            }

            BinaryStdOut.write(code, W);

            if (maxCode < 65535) // Code must be < 2^16 - 1
            {
                st.put(sb, maxCode++);
                if(maxCode+1 == L)
                {
                    if(W< maxW){ W++; L = L << 1;}
                    else if (reset) // Codebook is full. Check if user wants ST reset
                    {
                        L = minL; W = minW; maxCode = R+1;
                        st.reset(); // Reset the ST
                    }
                }
            }

            if (!empty) 
            {
                // Delete all but the last character from the StringBuilder
                sb.delete(0, sb.length() -1);
                oldCode = st.search1(sb); // Search the ST starting from the beginning
            }
        }

        BinaryStdOut.write(R, W);       // Write EOF code
        BinaryStdOut.close();           // Close the file
        BinaryStdIn.close();            // CLose input stream
    }

    private static void expand() 
    {
        reset = BinaryStdIn.readBoolean();
        String[] st = new String[65536];

        // initialize symbol table with all 1-character strings
        int maxCode; // next available codeword value
        for (maxCode = 0; maxCode < R; maxCode++)
            st[maxCode] = "" + (char) maxCode;
        st[maxCode++] = ""; // (unused) lookahead for EOF, maxCode is R+1 now

        int code = BinaryStdIn.readInt(W);
        String val = st[code];
        BinaryStdOut.write(val);

        while (true) 
        {
            code = BinaryStdIn.readInt(W);
            if (code == R) break;
            String s = st[code];

            if (maxCode == code) s = val + val.charAt(0); // special case hack
            if (maxCode < L) st[maxCode++] = val + s.charAt(0);
            val = s;

            BinaryStdOut.write(val);

            if (maxCode+2 == L) // Keep in sync with compression which will be one ahead
            {
                if(W < maxW) { W++; L = L << 1;}
                else if(reset)
                {
                    for (int i = R+1; i < L; i++) // Reset the ST
                        st[i] = null;
                    W = minW; L = minL; maxCode = R+1; 

                    code = BinaryStdIn.readInt(W);
                    val = st[code];
                    BinaryStdOut.write(val);
                }                     
            }

        }
        BinaryStdOut.close();           // Close the file
        BinaryStdIn.close();            // CLose input stream
    }

    public static void main(String[] args) 
    {

        // Check if user wants the dictionary reset once if it becomes full
        if (args.length > 1 && args[1].equals("r")) { reset = true;}

        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }
}