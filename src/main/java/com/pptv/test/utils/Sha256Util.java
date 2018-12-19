package com.pptv.test.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException; 
/**
 * SHA256 单向散列函数 指纹 消息摘要算法 哈希函数
 * 值为32个字节
 * @author NP0612
 */
public class Sha256Util {
	
	/**
　　 * 利用java原生的摘要实现SHA256加密
　　 * @param bytes 加密后的报文
　　 * @return
　　 */
	public static String sha256Hex(byte[] bytes){
		MessageDigest messageDigest;
		String encodeStr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(bytes);
			encodeStr = byte2Hex(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return encodeStr;
	}
	
	/**
	 * 字符串 SHA 加密
	 * @param strSourceText
	 * @return
	 */	
	public static String hash(final String strText) {
		String strResult = null;
		
		if (strText != null && strText.length() > 0) {
			try	{
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
				messageDigest.update(strText.getBytes());
				byte byteBuffer[] = messageDigest.digest();
				StringBuffer strHexString = new StringBuffer();
				for (int i = 0; i < byteBuffer.length; i++)	{
					String hex = Integer.toHexString(0xff & byteBuffer[i]);
					if (hex.length() == 1)	{
						strHexString.append('0');
					}
					strHexString.append(hex);
				}
				strResult = strHexString.toString();
			}
			catch (NoSuchAlgorithmException e)	{
				e.printStackTrace();
			}
		}
		return strResult;
	}
	
	/**
　　 * 将byte转为16进制
　　 * @param bytes
　　 * @return
　　 */
	private static String byte2Hex(byte[] bytes){
		StringBuffer stringBuffer = new StringBuffer();
		String temp = null;
		for (int i=0;i<bytes.length;i++){
			temp = Integer.toHexString(bytes[i] & 0xFF);
			if (temp.length()==1){
				//1得到一位的进行补0操作
				stringBuffer.append("0");
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}
}