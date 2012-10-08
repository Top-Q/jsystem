/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.report;

/**
 * An object that represent report element It's used to store reports
 * 
 * @author guy.arieli
 * 
 */
public class ReportElement { 
	private String title = null;

	private String message = null;

	private int status;

	private boolean bold = false;

	private boolean step = false;

	private boolean link = false;

	private boolean html = false;
	
	private boolean properties = false;

	private String originator = null;

	private long time = 0;
	
	private boolean startLevel = false;
	
	private boolean stopLevel = false;
	
	private int levelPlace = -1;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getOriginator() {
		return originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	public boolean isLink() {
		return link;
	}

	public void setLink(boolean link) {
		this.link = link;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isStep() {
		return step;
	}

	public void setStep(boolean step) {
		this.step = step;
	}

	public boolean isProperties() {
		return properties;
	}
	
	public void setProperties(boolean properties) {
		this.properties = properties;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public boolean isStartLevel() {
		return startLevel;
	}

	public void setStartLevel(boolean startLevel) {
		this.startLevel = startLevel;
	}

	public boolean isStopLevel() {
		return stopLevel;
	}

	public void setStopLevel(boolean stopLevel) {
		this.stopLevel = stopLevel;
	}

	public int getLevelPlace() {
		return levelPlace;
	}

	public void setLevelPlace(int levelPlace) {
		this.levelPlace = levelPlace;
	}
}
