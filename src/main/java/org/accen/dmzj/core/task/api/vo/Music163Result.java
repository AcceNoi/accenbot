package org.accen.dmzj.core.task.api.vo;

public class Music163Result {
	private Music163Ctt result;
	private String code;
	public Music163Ctt getResult() {
		return result;
	}
	public void setResult(Music163Ctt result) {
		this.result = result;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	class Music163Ctt{
		class Music163{
			private long id;
			private String name;
			private int position;
			private Object alias;
			private int status;
			private int fee;
			private long copyrightId;
			private String disc;
			private int no;
			private String mp3Url;
			//TODO 还有更多属性
			/**实际去用{@link org.accen.dmzj.core.task.api.MusicApiClient#music163Search(String, String, String, String)}请求下看看
			 */
			public long getId() {
				return id;
			}
			public void setId(long id) {
				this.id = id;
			}
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public int getPosition() {
				return position;
			}
			public void setPosition(int position) {
				this.position = position;
			}
			public Object getAlias() {
				return alias;
			}
			public void setAlias(Object alias) {
				this.alias = alias;
			}
			public int getStatus() {
				return status;
			}
			public void setStatus(int status) {
				this.status = status;
			}
			public int getFee() {
				return fee;
			}
			public void setFee(int fee) {
				this.fee = fee;
			}
			public long getCopyrightId() {
				return copyrightId;
			}
			public void setCopyrightId(long copyrightId) {
				this.copyrightId = copyrightId;
			}
			public String getDisc() {
				return disc;
			}
			public void setDisc(String disc) {
				this.disc = disc;
			}
			public int getNo() {
				return no;
			}
			public void setNo(int no) {
				this.no = no;
			}
			public String getMp3Url() {
				return mp3Url;
			}
			public void setMp3Url(String mp3Url) {
				this.mp3Url = mp3Url;
			}
			
		}
		private Music163[] songs;
		private int songCount;
		public Music163[] getSongs() {
			return songs;
		}
		public void setSongs(Music163[] songs) {
			this.songs = songs;
		}
		public int getSongCount() {
			return songCount;
		}
		public void setSongCount(int songCount) {
			this.songCount = songCount;
		}
		
	}
	
}
