package com.pptv.test.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="news_item")
public class Item {
    @Id
    private String docid;
    private String title;
    @Lob
    @Column(length = 16777216)  
    private String content;
    private String url;
    private String source;
    private String imgsrc;
    private int priority;
    @Column(columnDefinition="bit default 0", nullable=true)
    private int hasImg;
    private String digest;
    private int commentCount;
    private Date ptime;
    @OneToOne
    private Category categoryCode;
	public String getDocid() {
		return docid;
	}
	public void setDocid(String docid) {
		this.docid = docid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getImgsrc() {
		return imgsrc;
	}
	public void setImgsrc(String imgsrc) {
		this.imgsrc = imgsrc;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public int getHasImg() {
		return hasImg;
	}
	public void setHasImg(int hasImg) {
		this.hasImg = hasImg;
	}
	public String getDigest() {
		return digest;
	}
	public void setDigest(String digest) {
		this.digest = digest;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public Date getPtime() {
		return ptime;
	}
	public void setPtime(Date ptime) {
		this.ptime = ptime;
	}
	public Category getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(Category categoryCode) {
		this.categoryCode = categoryCode;
	}
}
