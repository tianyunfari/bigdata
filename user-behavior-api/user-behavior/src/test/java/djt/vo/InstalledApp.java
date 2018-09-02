package djt.vo;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class InstalledApp {

	@JsonIgnore
	private String title;
	private String packageName;
	private String vc;
	private String md5;
	@JsonIgnore
	private int type;
	@JsonIgnore
	private String imageUrl;
	@JsonIgnore
	private String description;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@JsonProperty("pn")
	public String getPackageName() {
		return packageName;
	}

	@JsonProperty("pn")
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVc() {
		return vc;
	}

	public void setVc(String vc) {
		this.vc = vc;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
