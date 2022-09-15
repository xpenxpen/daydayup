package org.xpen.bilibili;

public class Entry {
	
	private PageData pageData;

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

