package org.xpen.bilibili;

public class Entry {
	
	private int videoQuality;
	
	private PageData pageData;

	public int getVideoQuality() {
		return videoQuality;
	}

	public void setVideoQuality(int videoQuality) {
		this.videoQuality = videoQuality;
	}

	public PageData getPageData() {
		return pageData;
	}

	public void setPageData(PageData pageData) {
		this.pageData = pageData;
	}

	public class PageData {
		
		private String part;

		public String getPart() {
			return part;
		}

		public void setPart(String part) {
			this.part = part;
		}
		
		
	}

}

