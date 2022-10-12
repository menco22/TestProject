
package beans;

import java.time.LocalDateTime;

public class Auction {
	private int id;
	private int article;
	private String creator;
	private int initialPrice;
	private int minUpsideOffer;
	private LocalDateTime deadline;
	private boolean isClosed;
	
	// If there are no declared constructors, the default one is automatically created
	// This is important because beans require it to be used properly
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getArticle() {
		return article;
	}
	public void setArticle(int article) {
		this.article = article;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public int getInitialPrice() {
		return initialPrice;
	}
	public void setInitialPrice(int initialPrice) {
		this.initialPrice = initialPrice;
	}
	public int getMinUpsideOffer() {
		return minUpsideOffer;
	}
	public void setMinUpsideOffer(int minUpsideOffer) {
		this.minUpsideOffer = minUpsideOffer;
	}
	public LocalDateTime getDeadline() {
		return deadline;
	}
	public void setDeadline(LocalDateTime deadline) {
		this.deadline = deadline;
	}
	public boolean isClosed() {
		return isClosed;
	}
	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}
}