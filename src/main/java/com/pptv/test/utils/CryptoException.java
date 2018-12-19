package com.pptv.test.utils;

public class CryptoException extends Exception {
	private static final long serialVersionUID = 8615144681824758259L;
	public static final String INVALID_LENGTH = "Source length is not a multiple of the block size" 
			+ "therefore cannot it be properly processed.";
	
	public static final String NO_KEY = "No key has been provided.";
	
	public static final String NO_BLOCK_SIZE = "No block size has been provided.";
	
	public static final String NO_IV = "No IV has been provided.";
	
	public static final String MAC_DOES_NOT_MATCH = "The provided MAC does not match the provided message.";
	
	public static final String NO_MAC = "No MAC was provided.";

	
	public CryptoException(String error) {
	
		super(error);
	}
	
	public CryptoException(Exception e) {
		super(e);
	}
}