package beans;

import java.time.LocalDateTime;

public class Offer {
	private int id;
	private int auction;
	private String user;
	private int value;
	private LocalDateTime time;
	
	// If there are no declared constructors, the default one is automatically created
	// This is important because beans require it to be used properly
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAuction() {
		return auction;
	}
	public void setAuction(int auction) {
		this.auction = auction;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public LocalDateTime getTime() {
		return time;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
}