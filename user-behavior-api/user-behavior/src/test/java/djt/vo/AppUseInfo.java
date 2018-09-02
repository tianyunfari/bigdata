package djt.vo;

import com.djt.model.SingleAppUseInfo;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class AppUseInfo {
	@JsonProperty("begintime")
	private Long beginTime;

	@JsonProperty("endtime")
	private Long endTime;

	@JsonProperty("data")
	private List<SingleAppUseInfo> singleAppUseInfoList;

	private long userId;

	private String day;

	public Long getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Long beginTime) {
		this.beginTime = beginTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public List<SingleAppUseInfo> getSingleAppUseInfoList() {
		return singleAppUseInfoList;
	}

	public void setSingleAppUseInfoList(List<SingleAppUseInfo> singleAppUseInfoList) {
		this.singleAppUseInfoList = singleAppUseInfoList;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
}
