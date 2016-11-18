package ${package}.grid;

import javax.persistence.Id;

public class User {

	@Id
	private String userName;
	
	private String hitTime;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getHitTime() {
		return hitTime;
	}
	public void setHitTime(String hitTime) {
		this.hitTime = hitTime;
	}
	
	

}
