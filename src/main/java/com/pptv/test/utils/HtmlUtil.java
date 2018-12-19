package com.pptv.test.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtil {
	
	public static String replaceHtmlTag(String str, String tag, String tagAttrib, String startTag, String endTag) {
		String regxpForTag = "<\\s*" + tag + "\\s+([^>]*)\\s*" ;
		String regxpForTagAttrib = tagAttrib + "=\\s*\"([^\"]+)\"" ;
		Pattern patternForTag = Pattern.compile (regxpForTag,Pattern. CASE_INSENSITIVE );
		Pattern patternForAttrib = Pattern.compile (regxpForTagAttrib,Pattern. CASE_INSENSITIVE );   
		Matcher matcherForTag = patternForTag.matcher(str);
		StringBuffer sb = new StringBuffer();
		boolean result = matcherForTag.find();
		while (result) {
			StringBuffer sbreplace = new StringBuffer( "<"+tag+" ");
		    Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag.group(1));
        	if (matcherForAttrib.find()) {
        		String attributeStr = matcherForAttrib.group(1);
        		matcherForAttrib.appendReplacement(sbreplace, startTag + attributeStr + endTag);
        	}
        	matcherForAttrib.appendTail(sbreplace);
        	matcherForTag.appendReplacement(sb, sbreplace.toString());
        	result = matcherForTag.find();
		}
		matcherForTag.appendTail(sb);         
		return sb.toString();
	}
    
	public static String p1(String content, String pattern) {
		Pattern p  = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher m = p.matcher(content);
        if (m.find())
        	return m.group(1);
        return null;     
	}
	
    public static void main(String[] args) {
//    	StringBuffer content = new StringBuffer();
//    	content.append("<video poster=\"http://vimg.nosdn.127.net/snapshot/20180919/MSvj22997_0.jpg\" webkit-playsinline=\"\" controls=\"true\" src=\"//flv3.bn.netease.com/videolib1/1809/19/FYHyh997L/SD/FYHyh997L-mobile.mp4\"></video>");
//    	content.append("<video poster=\"http://vimg.nosdn.127.net/snapshot/20180919/MSvj22997_0.jpg\" webkit-playsinline=\"\" controls=\"true\" src=\"//flv3.bn.netease.com/videolib1/1809/19/FYHyh997L/SD/FYHyh997L-mobile.mp4\"></video>");
//
//    	System.out.println("原始字符串为:"+content.toString());
//        String newStr = replaceHtmlTag(content.toString(), "video", "src", "src=\"http:", "\"");
//        System.out.println("替换后为:"+newStr);
//    	
//    	String testStr = "<img alt=\"\" data-src=\"//cms-bucket.nosdn.127.net/catchpic/f/fd/fd0b7c1df9ecbdad42e2cfb9a6d6f215.jpg\" src=\"http\">";
//    	testStr = testStr.replaceAll("(<img[^>]*)data-src=\"[^\"']*\"\\s*", "$1");
//    	System.out.println(testStr);
    	String str = "<article id=\"article-DTKH9VVV0001875O\"> \n <div class=\"head\"> \n  <h1 class=\"title\">经济学诺奖得主把获奖通知当骚扰电话 连挂两次</h1> \n  <div class=\"info\"> \n   <span class=\"time js-time\">2018-10-08 21:12:33</span> \n   <span class=\"source js-source\">澎湃新闻</span> \n  </div> \n </div> \n <div class=\"content\"> \n  <div class=\"page js-page on\"> \n   <p></p>\n   <div class=\"video\"> \n    <video src=\"//flv3.bn.netease.com/videolib1/1810/08/aNSco733i/SD/aNSco733i-mobile.mp4\" poster=\"http://vimg.nosdn.127.net/snapshot/20181008/RfSt03733_2.jpg\" webkit-playsinline controls=\"true\"> \n    </video> \n    <a class=\"bot_word more-client iconfont\" href=\"\"> 精彩弹幕，尽在客户端 <span><span> </span></span></a> \n   </div>\n   <p></p>\n   <p>10月8日，2018年度诺贝尔经济学奖公布，第19次由两位获奖者分享，本轮诺奖得主均为美国经济学家，其中保罗·罗默接到获奖消息前，曾以为是骚扰电话连挂两次来电。</p> \n   <div class=\"otitle_editor\"> \n    <p class=\"otitle\"> (原标题：连线经济学诺奖得主：以为骚扰电话连挂俩) </p> \n    <p class=\"editor\"> (责任编辑：吉国杰_NBJ11143) </p> \n   </div> \n  </div> \n </div> \n  \n</article>";
        Pattern p  = Pattern.compile("<div class=\"footer\">(.*)</div>", Pattern.DOTALL);
        Pattern p2 = Pattern.compile("<a class=\"bot_word(.*?)</a>", Pattern.DOTALL);
        Matcher m = p.matcher(str);
        Matcher m2 = p2.matcher(str);
        if (m.find()) {
        	str = str.replace(m.group(0), "");
        }
        if (m2.find()) {
        	str = str.replace(m2.group(0), "");
        }
        str = str.replace("data-src", "src");
        System.out.println(str);
////        content = content.replaceAll("(<video[^>]*)data-src=\"[^\"']*\"\\s*", "$1");
//        content = content.replace(footer, "").replace("data-src", "src");
//        
//    	System.out.println(testStr);
    	
    }
}
