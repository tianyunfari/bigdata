package djt.vo;

import org.codehaus.jackson.annotate.JsonProperty;

public class AppUse {
	
	@JsonProperty("package")
	private String packageName;
	
	@JsonProperty("activetime")
	private Long activeTime;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Long getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(Long activeTime) {
		this.activeTime = activeTime;
	}
}
