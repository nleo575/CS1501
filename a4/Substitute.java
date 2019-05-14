import java.util.*; // For random

/**
	@author Nicolas Leo
	CS 1501 Project 4
*/
public class Substitute implements SymCipher 
{
	private byte [] key,		// Stores the key to encode words
					inverseKey; // Stores the inverse key to decode words
	
	/**
		Constructor used for Secure Chat Client. Creates a random 256-byte array which is a
		permutation of the 256 possible byte values and will serve as a map from bytes to 
		their substitution values.
	*/
	public Substitute()
	{
		key = new byte[256];
		inverseKey = new byte[256];

		Random rand = new Random();
		
		int zeroIndex = rand.nextInt(256),
			temp;
		key[0] = (byte) zeroIndex;

		for (int i = 1; i < 256 ; i++) 
		{
			while(true)
			{
				temp = rand.nextInt(256); // Generate random #.  

				// Verify that the mapping isn't already used
				if(temp != zeroIndex && inverseKey[temp] == 0)
				{
					key[i] = (byte) temp;
					inverseKey[temp] = (byte) i; // Map key to inverse key
					break;
				}
			}
		}
	}

	/**
		Constructor used for Secure Chat Server. Takes a 256-byte array which is a
		permutation of the 256 possible byte values and serves as a map from bytes to 
		their substitution values.
		@param k The 256-byte key provided by the chat server
	*/	
	public Substitute(byte [] k)
	{
		key = k;
		inverseKey = new byte[256];
		for(int i = 0; i < 256; i++)
			inverseKey[key[i] & 0xff] = (byte) i; // Map key to inverse key
	}

	/**
		Returns an array of bytes that represent the key for the cipher
		@return an array of bytes that represent the key for the cipher
	*/
	public byte [] getKey()
	{
		return key;
	}
	
	/**
		Encodes the given string using the key and returns the result as an array of
		bytes. 
		@param S String to encrypt
		@return byte array representing the encoded input string
	*/
	public byte [] encode(String S)
	{	
		byte [] bytes = S.getBytes();
		int len = bytes.length;
		for (int i = 0; i < len ; i++) 
			bytes[i] = (byte) key[bytes[i] & 0xff]; // Expand without sign extension	

		return bytes;
	}
	
	/**
		Decrypts the array of bytes and returns the message as a String.
		@param bytes Array of bytes representing the encrypted message
		@return Decrypted message as a string
	*/
	public String decode(byte [] bytes)
	{
		int len = bytes.length;
		for (int i = 0; i < len ; i++) 
			bytes[i] = (byte) inverseKey[bytes[i] & 0xff]; // Expand without sign extension

		return new String(bytes);
	}	
}