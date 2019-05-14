import java.util.*; // For random

/**
	@author Nicolas Leo
	CS 1501 Project 4
*/
public  class Add128 implements SymCipher 
{
	private byte [] key;
	
	/**
		Constructor used for Secure Chat Client. Creates a random 128-byte additive key 
		and stores it in an array of bytes.
	*/
	public Add128()
	{
		key = new byte[128];
		Random rand = new Random();
		rand.nextBytes(key); 
	}

	/**
		Constructor used for Secure Chat Server. Takes a 128-byte array which is used to 
		decode messages
		@param k The 128-byte key provided by the chat server
	*/	
	public Add128(byte [] k)
	{
		key = k;
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
			bytes[i] = (byte) (bytes[i]+key[i%128]);

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
			bytes[i] = (byte) (bytes[i]-key[i%128]);

		return new String(bytes);		
	}	
}