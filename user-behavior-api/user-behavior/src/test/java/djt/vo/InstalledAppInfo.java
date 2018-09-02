package djt.vo;

import java.util.List;

public class InstalledAppInfo {

	private Integer count;
	private List<InstalledApp> apps;

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public List<InstalledApp> getApps() {
		return apps;
	}

	public void setApps(List<InstalledApp> apps) {
		this.apps = apps;
	}

}
