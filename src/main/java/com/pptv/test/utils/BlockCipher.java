package com.pptv.test.utils;

public abstract class BlockCipher {

	public abstract void removeKey();
	public abstract boolean hasKey();
	public abstract void setKey(byte[] key);
	
	public abstract int getBlockBytes();

	public abstract byte[] encryptBlock(byte[] input, int srcPos, byte[] output, int destPos) throws CryptoException;

	public abstract byte[] decryptBlock(byte[] input, int srcPos, byte[] output, int destPos) throws CryptoException;

	public byte[] encryptBlock(byte[] input) throws CryptoException {
		byte[] output = new byte[getBlockBytes()];
		return encryptBlock(input, 0, output, 0);
	}

	public byte[] decryptBlock(byte[] input) throws CryptoException {
		byte[] output = new byte[getBlockBytes()];
		return decryptBlock(input, 0, output, 0);
	}
	
	public byte[] encryptBlock(byte[] input, byte[] output) throws CryptoException {
		return encryptBlock(input, 0, output, 0);
	}
	public byte[] decryptBlock(byte[] input, byte[] output) throws CryptoException {
		return decryptBlock(input, 0, output, 0);
	}
}