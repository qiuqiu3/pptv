package com.pptv.test.utils;

public interface Cipher {
	
	public StreamRunnable encrypt();
	public StreamRunnable decrypt();
	
	public Cipher setKey(byte[] key) throws CryptoException;
	public boolean hasKey();
	public Cipher removeKey();
	
	public Cipher setIV(byte[] initVector);
	public byte[] getIV();
	public boolean hasIV();
	
	public boolean isIVPrepending();
	public Cipher setIVPrepending(boolean ivPrepending);
}