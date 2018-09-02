package com.djt.format;

import com.djt.dao.AbstractDMDAO;
import com.djt.dao.DMIPRuleDAO;
import com.djt.dao.DMInterURLDAO;
import com.djt.dao.DMOuterURLRuleDAO;
import com.djt.enums.ConstantEnum;
import com.djt.enums.PvEnum;
import com.djt.enums.UtilComstrantsEnum;
import com.djt.utils.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class PvFormatMR extends Configured implements Tool {

	private static final String DEFAULT_NEGATIVE_NUM = "-999";

	public static class PvFormatMap extends Mapper<LongWritable, Text, Text, NullWritable> {
		private static final String urlSeparator = "http://";
		private AbstractDMDAO<Long, Map<ConstantEnum, String>> dmIPRuleDAO = null;
		private AbstractDMDAO<String, Map<ConstantEnum, String>> dmOuterURLRuleDAO = null;
		private DMInterURLDAO dmInterURLRuleDAO = null;
		private Text keyText = null;

		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			this.dmIPRuleDAO = new DMIPRuleDAO();
			this.dmOuterURLRuleDAO = new DMOuterURLRuleDAO();
			this.dmInterURLRuleDAO = new DMInterURLDAO();

			this.dmIPRuleDAO.parseDMObj(new File(ConstantEnum.IP_TABLE.name().toLowerCase()));
			this.dmOuterURLRuleDAO.parseDMObj(new File(ConstantEnum.DM_OUTER_URL.name().toLowerCase()));
			this.dmInterURLRuleDAO.parseDMObj(new File(ConstantEnum.DM_INTER_URL.name().toLowerCase()));

			keyText = new Text();
		}

		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();

			String pvFormatStr;
			try {
				pvFormatStr = this.getPVFormatStr(line);
				keyText.set(pvFormatStr);
				context.write(keyText, NullWritable.get());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public String getPVFormatStr(String originalData) throws Exception {
			StringBuilder pvFormatStr = new StringBuilder();
			String[] rawsplitStr = originalData.split("\t", -1);

			String buildData = originalData;
			if (rawsplitStr.length < PvEnum.CHANNEL_ID.ordinal() + 1) {
				StringBuilder rawData = new StringBuilder(originalData);
				for (int i = rawsplitStr.length; i < PvEnum.CHANNEL_ID.ordinal() + 1; i++) {
					rawData.append("" + StringFormatUtil.TAB_SEPARATOR + DEFAULT_NEGATIVE_NUM);
				}
				buildData = rawData.toString();
			}

			String[] splitStr = buildData.split("\t", -1);
			try {
				//根据IP地址获取地域信息
				String ipInfoStr = WebCheckUtil.checkField(splitStr[PvEnum.IP.ordinal()], "0.0.0.0");
				long ipLong = IPFormatUtil.ip2long(ipInfoStr);
				Map<ConstantEnum, String> ipRuleMap = this.dmIPRuleDAO.getDMOjb(ipLong);
				String provinceId = ipRuleMap.get(ConstantEnum.PROVINCE_ID);
				String cityId = ipRuleMap.get(ConstantEnum.CITY_ID);
				String ispId = ipRuleMap.get(ConstantEnum.ISP_ID);

				//获取浏览url
				String urlInfoRegion = splitStr[PvEnum.URL.ordinal()];
				String charset = "UTF-8";
				String urlInfo = StringDecodeFormatUtil.decodeCodedStr(urlInfoRegion, charset).replaceAll("\\s+", "");
				if (null == urlInfo || urlInfo.isEmpty()) {
					urlInfo = DEFAULT_NEGATIVE_NUM;
				}

				//根据浏览网页所属的频道
				Map<ConstantEnum, String> URLTypeMap = this.dmInterURLRuleDAO.getDMOjb(urlInfo);
				String urlFirstId = URLTypeMap.get(ConstantEnum.URL_FIRST_ID).trim();
				String urlSecondId = URLTypeMap.get(ConstantEnum.URL_SECOND_ID).trim();
				String urlThirdId = URLTypeMap.get(ConstantEnum.URL_THIRD_ID).trim();

				//获取浏览来源信息
				String referUrlInfoRegion = splitStr[PvEnum.REFERURL.ordinal()];
				String referUrlInfoStr = StringDecodeFormatUtil.decodeCodedStr(referUrlInfoRegion, charset).replaceAll("\\s+", "");
				String referUrlFirstId = DEFAULT_NEGATIVE_NUM;
				String referUrlSecondId = DEFAULT_NEGATIVE_NUM;
				String referUrlThirdId = DEFAULT_NEGATIVE_NUM;
				String referUrlInfo = referUrlInfoStr;

				if (referUrlInfoStr.contains(urlSeparator)) {
					String referUrlInfoArr[] = referUrlInfoStr.split(urlSeparator);
					referUrlInfo = urlSeparator.concat(referUrlInfoArr[1]);
				}

				String referUrl = referUrlInfo;
				Map<ConstantEnum, String> referUrlRuleMap = this.dmOuterURLRuleDAO.getDMOjb(referUrl);
				referUrlFirstId = referUrlRuleMap.get(ConstantEnum.URL_FIRST_ID).trim();
				referUrlSecondId = referUrlRuleMap.get(ConstantEnum.URL_SECOND_ID).trim();
				referUrlThirdId = referUrlRuleMap.get(ConstantEnum.URL_THIRD_ID).trim();

				// mac地址
				String macInfoStr = WebCheckUtil.checkField(splitStr[PvEnum.MAC.ordinal()], "0");
				String macInfo = MACFormatUtil.getCorrectMac(macInfoStr);

				String sessionId = WebCheckUtil.checkField(splitStr[PvEnum.SESSION_ID.ordinal()].replaceAll("\\s+", ""), DEFAULT_NEGATIVE_NUM);

				String userId = splitStr[PvEnum.USER_ID.ordinal()];
				if (WebCheckUtil.checkIsNum(userId) == false) {
					userId = "0";
				}

				String pvId = splitStr[PvEnum.PV_ID.ordinal()];
				if (WebCheckUtil.checkIsNum(pvId) == false) {
					pvId = DEFAULT_NEGATIVE_NUM;
				}

				String proID = splitStr[PvEnum.PROTOCOL.ordinal()];
				if (WebCheckUtil.checkIsNum(proID) == false) {
					proID = "0";
				}

				String rproID = splitStr[PvEnum.RPROTOCOL.ordinal()];
				if (WebCheckUtil.checkIsNum(rproID) == false) {
					rproID = "0";
				}

				//开始拼接输出
				pvFormatStr.append(splitStr[PvEnum.URL_TYPE.ordinal()] + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(splitStr[PvEnum.REQUEST_TIME.ordinal()] + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(splitStr[PvEnum.IP.ordinal()] + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(proID + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(rproID + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(provinceId + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(cityId + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(ispId + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(splitStr[PvEnum.CLIENT_FLAG.ordinal()] + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(splitStr[PvEnum.FCK.ordinal()] + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(macInfo + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(userId + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(sessionId + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(pvId + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(splitStr[PvEnum.CONFIG.ordinal()] + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(urlInfo + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(urlFirstId + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(urlSecondId + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(urlThirdId + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(referUrlInfoStr + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(referUrlFirstId + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(referUrlSecondId + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(referUrlThirdId + UtilComstrantsEnum.tabSeparator.getValueStr());
				pvFormatStr.append(splitStr[PvEnum.CHANNEL_ID.ordinal()] + UtilComstrantsEnum.tabSeparator.getValueStr());
			} catch (Exception e) {
				throw e;
			}
			return pvFormatStr.toString();
		}
	}

	public static void main(String[] args) throws Exception {
		int nRet = ToolRunner.run(new Configuration(), new PvFormatMR(), args);
		System.out.println(nRet);
	}

	public int run(String[] args) throws Exception {

		Configuration conf = getConf();
		Job job = Job.getInstance(conf, "PvFormatMR");
		job.setJarByClass(PvFormatMR.class);

		job.setMapperClass(PvFormatMap.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(NullWritable.class);

		String inputPathStr = conf.get("input");
		String outputPathStr = conf.get("output");

		FileInputFormat.addInputPaths(job, inputPathStr);
		FileOutputFormat.setOutputPath(job, new Path(outputPathStr));

		return job.waitForCompletion(true) ? 0 : 1;
	}
}
