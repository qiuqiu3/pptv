package com.pptv.test.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.pptv.test.model.Dt;
import com.pptv.test.model.Video;

public class PPTVUtil {
  
	private static final String APP_KEY = "V8oo0Or1f047NaiMTxK123LMFuINTNeI";
	private static final String SERVER_KEY = "qqqqqww";
	private static final long DELTA = 2654435769l;
	private static final String HEX = "0123456789abcdef";
	private static final String URL = "http://web-play.pptv.com";
	
	public static Video getVideo(String url) {
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(url);
			return parseXml(doc);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Video getVideo(File file) {
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(file);;			
			return parseXml(doc);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static Video parseXml(Document doc) {
		Video video = new Video();
		Element eleRoot = doc.getRootElement();
		Element channel = eleRoot.element("channel");
		video.setName(channel.attribute("nm").getText());
		video.setPic(channel.attribute("pic").getText());
		
		List<Dt> dts = new ArrayList<>();
		List<Element> elDts = eleRoot.elements("dt");
		List<Element> elSgms = eleRoot.elements("dragdata");
		
		for (int i=0; i<elDts.size(); i++) {
			Element eleDt = elDts.get(i);
			Element eleSgm = elSgms.get(i);
			
			Dt dt = new Dt();
			dt.setRid(eleDt.attributeValue("rid"));
			dt.setSh(eleDt.element("sh").getText());
			dt.setSt(eleDt.element("st").getText());
			dt.setId(eleDt.element("id").getText());
			dt.setBh(eleDt.element("bh").getText());
			dt.setIv(eleDt.element("iv").getText());
			dt.setKey(eleDt.element("key").getText());
			dt.setSgms(eleSgm.elements("sgm").size());
			dt.setFs(Integer.valueOf(eleSgm.attributeValue("fs")));
			dt.setVw(Integer.valueOf(eleSgm.attributeValue("vw")));
			dt.setVh(Integer.valueOf(eleSgm.attributeValue("vh")));
			dt.setDu(Double.valueOf(eleSgm.attributeValue("du")));
			dt.setFlag(Integer.valueOf(eleDt.element("flag").getText()));
			
			dts.add(dt);
		}
		
		video.setDts(dts);
		return video;
	}
	
	public static String getUrl(String cid) {								
		String url = URL + "/" + "webplay3-0-" + cid + ".xml";
		String params = "?appplt=flp&appid=pptv.flashplayer.vod&appver=3.4.3.27&type=web.fpp&version=4";
		String rndStr = getRndStr();
		String str = rndStr + "web.fpp" + "-" + "1" + APP_KEY;
		String sign = Sha256Util.hash(str);
		String random = HexUtil.strToHex(rndStr);
		String param = "&param=ahl_ver%3D1%26ahl_random%3D" + random + "%26ahl_signa%3D" + sign;
		return url + params + param;
	}
	
	private static String getRndStr() {
		// 获取 16 位随机字符
		int i = 0, rnd = 0;
		StringBuffer sb = new StringBuffer();
		
		while (i < 16) {
			rnd = (int) (Math.random() * 93) + 33;
			sb.append((char) rnd);
			i++;
		}
		return sb.toString();
	}
	
	public static String calcK(String key, int flag, String sh, String st, String id, String bh, String iv) {
		
		String s1 = key.substring(0, 32);
		String s2 = getKey(flag, sh, st, id, bh, APP_KEY, iv);
		
		Aes aes = new Aes(HexUtil.hexToBytes((s2)));
		Cipher ecbMode = new ECBMode(aes, new ZeroPadding());
		byte[] b1 = HexUtil.hexToBytes(s1);
		try {
			byte[] b2 = ecbMode.decrypt().startSync(b1);
			return HexUtil.bytesToHex(b2) + key.substring(32);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String calcKey(String arg) {
		
		// 这里没有找到好的方法能转 UTC 时间
		@SuppressWarnings("deprecation")
		Date date = new Date(arg);
		int time = (int) ((date.getTime() - (60 * 1000)) / 1000);
		
    	String s1 = time2String(time), s2, s3 = SERVER_KEY;
    	String[] ss1 = deleteFirst(s1.split(""));
    	
        if (ss1.length < 16) {
        	s1 = add(s1, (16 - ss1.length));
        }
        
        if (s3.length() < 16) {
        	s3 = add(s3, (16 - s3.length()));
        }
        
        s2 = encrypt(s1, s3);
        
        if (s2.length() < 16) {
        	s2 = add(s2, (16 - s2.length()));
        }
        
        return str2Hex(s2);
    }
    
	private static boolean getAS3(int arg1, int arg2) {
		String s1 = Integer.toBinaryString(arg1);
		String s2 = Integer.toBinaryString(arg2);
		int tmp3 = Math.min(s1.length(), s2.length());
		int i=0;
		while (i < tmp3) {
			if (s1.substring(i, 1).equals("1") && s2.substring(i, 1).equals("1"))
				return true;
			i++;
		}
		return false;
	}
	
	private static String getKey(int arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer;
		
		if (getAS3(arg1, 1)) {
			buffer = arg2.getBytes();
			output.write(buffer, 0, buffer.length);
		}
		if (getAS3(arg1, 2)) {
			buffer = arg3.getBytes();
			output.write(buffer, 0, buffer.length);
		}
		if (getAS3(arg1, 4)) {
			buffer = arg4.getBytes();
			output.write(buffer, 0, buffer.length);
		}
		if (getAS3(arg1, 8)) {
			buffer = arg5.getBytes();
			output.write(buffer, 0, buffer.length);
		}
		buffer = arg7.getBytes();
		output.write(buffer, 0, buffer.length);
		
		buffer = arg6.getBytes();
		output.write(buffer, 0, buffer.length);
				
		return Sha256Util.sha256Hex(output.toByteArray());
	}
	
    private static String add(String arg1, int arg2) {
        int i = 0;
        while (i < arg2) {
        	arg1 = arg1 + (char) 0;
            i++;
        }
        return arg1;
    }
    
    private static String[] deleteFirst(String[] arr) {
        String[] temp = new String[arr.length - 1];
        System.arraycopy(arr, 1, temp, 0, temp.length);
        return temp;
    }
    
    private static int getKey(String arg) {
    	char[] cs1 = arg.toCharArray();
    	char c1;
    	int i1, i2 = 0;
    	int i = 0;
        while (i < cs1.length) {
        	c1 = cs1[i];
        	i1 = c1 << ((i % 4) * 8);
        	i2 = i2 ^ i1;
            i++;
        }
        return i2;
    }
    
    private static String encrypt(String arg1, String arg2) {
    	long l1, l2, l3, l4, l5, l6, l7, l8, l9, l10, l11;
    	long l12, l13, l14 = 0;
    	long l23, l24, l25, l26, l27, l28, l29, l30, l31;
    	long l32 = getKey(arg2);
    	char[] cs1 = arg1.toCharArray();
        String s1 = "";
        
        l1 = getUnsignedInt((l32 << 8) | (l32 >>> 24));
        l2 = getUnsignedInt((l32 << 16) | (l32 >>> 16));
        l3 = getUnsignedInt((l32 << 24) | (l32 >>> 8));
        
        l4 = cs1[0] << 0;
        l5 = cs1[1] << 8;
        l6 = cs1[2] << 16;
        l7 = cs1[3] << 24;
        l8 = cs1[4] << 0;
        l9 = cs1[5] << 8;
        l10 = cs1[6] << 16;
        l11 = cs1[7] << 24;
        
        l12 = (((0x00 | l4) | l5) | l6) | l7;
        l13 = (((0x00 | l8) | l9) | l10) | l11;
        
        int i = 0;
        
        while (i < 32) {
        	l14 = getUnsignedInt(l14 + DELTA);
        	l23 = getUnsignedInt((l13 << 4) + l32);
        	l24 = getUnsignedInt((int)l13 + (int)l14);
        	l25 = (l13 >>> 5) + l1;
        	l26 = getUnsignedInt((l23 ^ l24) ^ l25);
        	l12 = getUnsignedInt(l12 + l26);
        	l27 = getUnsignedInt(l12 << 4) + l2;
        	l28 = getUnsignedInt((int)l12 + l14);
        	l29 = l12 >>> 5;
        	l30 = l29 + l3;
        	l31 = (l27 ^ l28) ^ l30;
            l13 = getUnsignedInt(l13 + l31);
            i++;
        }
        
        s1 = s1 + (char) ((l12 >>> 0) & 0xFF);
        s1 = s1 + (char) ((l12 >>> 8) & 0xFF);
        s1 = s1 + (char) ((l12 >>> 16) & 0xFF);
        s1 = s1 + (char) ((l12 >>> 24) & 0xFF);
        s1 = s1 + (char) ((l13 >>> 0) & 0xFF);
        s1 = s1 + (char) ((l13 >>> 8) & 0xFF);
        s1 = s1 + (char) ((l13 >>> 16) & 0xFF);
        s1 = s1 + (char) ((l13 >>> 24) & 0xFF);
        
        return s1;
    }
    
	private static long getUnsignedInt(long arg) {
		return arg & 0xFFFFFFFFl;
	}
	
    private static String time2String(int arg) {
    	StringBuffer sb = new StringBuffer();
    	int i1;
        int i=0;
        String[] ss1 = deleteFirst(HEX.split(""));
        
        while (i < 8) {
        	i1 = (arg >>> (28 - ((i % 8) * 4))) & 0x0F;
            sb.append(ss1[i1]);
            i++;
        };
        return sb.toString();
    }
    
    private static String str2Hex(String arg) {
    	char[] cs1 = HEX.toCharArray();
    	char[] cs2 = arg.toCharArray();
    	char[] cs3 = new char[(2 * cs2.length) + 1];
    	int i1 = cs2.length;
    	int i=0;
        while (i < i1) {
            if (i < 8) {
            	cs3[(2 * i)] = cs1[(cs2[i] & 0x0F)];
            	cs3[((2 * i) + 1)] = cs1[((cs2[i] >>> 4) & 0x0F)];
            } else {
            	cs3[(2 * i)] = cs1[(int) (Math.random() * 15)];
            	cs3[((2 * i) + 1)] = cs1[(int) (Math.random() * 15)];
            }
            i++;
        }
        return new String(cs3);
    }
    
}