package com.bigdata.tv_project;

import java.net.URLDecoder;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DataUtil {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void transData(String text, Context context, String fixedDate) {
		try {
			//通过Jsoup解析每行数据
			Document doc = Jsoup.parse(text);
			
			//获取WIC标签，每行数据只有一个WIC标签
			Elements content = doc.getElementsByTag("WIC");
			
			//解析出机顶盒sn号还有日期
			String stbNum = content.get(0).attr("stbNum");
			if (stbNum == null || "".equals(stbNum)) {
				return;
			}
			String date =content.get(0).attr("date");
			if (date == null || "".equals(date)) {
				return;
			} else if (!(date.replaceAll("-", "").equals(fixedDate))) {
				return;
			}
			
			
			//解析A标签
			Elements els = doc.getElementsByTag("A");
			
			for (Element el : els) {
				//解析结束时间
				String e = el.attr("e");
				if (e == null || "".equals(e)) {
					break;
				}
				
				//解析开始时间
				String s = el.attr("s");
				if (s == null || "".equals(s)) {
					break;
				} 
				
				//解析节目内容
				String p = el.attr("p");
				if (p == null || "".equals(p)) {
					break;
				}
				
				//解析频道
				String sn = el.attr("sn");
				if (sn == null || "".equals(sn)) {
					break;
				}
				
				//解码节目名称
				p = URLDecoder.decode(p, "utf-8");
				//因为电视剧集数的问题，解析出统一的节目名称xxx(1)
				int index = p.indexOf("(");
				if (index != -1) {
					p = p.substring(0, index);
				}
				
				int startSecond = TimeUtil.TimeToSecond(s);
				int endSecond = TimeUtil.TimeToSecond(e);
				
				if (startSecond > endSecond) {
					endSecond = endSecond + 24 * 3600;
				}
				//收看的时长
				int duration = endSecond - startSecond;
				
				context.write(new Text(stbNum + "@" + date),
					new Text(sn + "@" + p + "@" + s + "@" + e
							+ "@" + duration));
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
