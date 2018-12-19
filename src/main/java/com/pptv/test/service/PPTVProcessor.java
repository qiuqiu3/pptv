package com.pptv.test.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.pptv.test.model.Dt;
import com.pptv.test.model.Video;
import com.pptv.test.utils.FileUtil;
import com.pptv.test.utils.HtmlUtil;
import com.pptv.test.utils.PPTVUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

@Service
public class PPTVProcessor implements PageProcessor {
	
	public static final String URL = "http://v.pptv.com/show/qk3vbLiahSojradE.html";	
	private Site site = Site.me().setRetryTimes(3).setSleepTime(5000).setTimeOut(5000);
	
	@Override
	public void process(Page page) {
		String content = page.getRawText();
		// 获取 cid
		String cid = HtmlUtil.p1(content, "\"cid\":(.*?),");
		// 获取视频分段信息url
		String url = PPTVUtil.getUrl(cid);
		printUrl(PPTVUtil.getVideo(url));
	}

	public Site getSite() {
		return site;
	}
	
	public void runSpider() {
		Spider.create(this)
			.thread(1)
			.addUrl(URL)
			.run();
	}
	
	private static void printUrl(Video video) {
		List<Dt> dts = video.getDts();
		System.out.println("标题:" + video.getName());
		System.out.println("缩略图: " + "http://v.img.pplive.cn/cs128x72/" + video.getPic());
		System.out.println();
		
		for (int i=0; i<dts.size(); i++) {
			Dt dt = dts.get(i);
			System.out.println((i == 0 ? "[标清" : i==1 ? "[高清" : "[超清") + "-" + FileUtil.getPrintSize(dt.getFs()) + "]");	
			// 计算 k 值
			String k = PPTVUtil.calcK(dt.getKey(), dt.getFlag(), dt.getSh(), dt.getSt(), dt.getId(), dt.getBh(), dt.getIv());
			// 计算 key 值
			String key = PPTVUtil.calcKey(dt.getSt());
			
			for (int ii=0; ii<dt.getSgms(); ii++) {
				String url = "http://" + dt.getSh() + "/" + ii + "/" + dt.getRid();
				String params = "?fpp.ver=1.3.0.24&key=" + key + "&k=" + k + "&type=web.fpp";
				System.out.println(url + params);
			}
			
			System.out.println();
		}
	}
	
	public static void main(String[] args) throws IOException, ParseException {		
		// 输出地址
		Resource resource = new ClassPathResource("test.xml");		
		printUrl(PPTVUtil.getVideo(resource.getFile()));
	}
}