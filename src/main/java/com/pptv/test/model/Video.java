package com.pptv.test.model;

import java.util.List;

public class Video {
	private String name;
	private String pic;
	private List<Dt> dts;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public List<Dt> getDts() {
		return dts;
	}
	public void setDts(List<Dt> dts) {
		this.dts = dts;
	}
}
