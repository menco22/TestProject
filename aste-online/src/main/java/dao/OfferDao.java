package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import beans.Offer;

public class OfferDao {
	private Connection connection = null;
	String query = "";
	PreparedStatement statement = null;
	ResultSet result = null;
	
	public OfferDao(Connection connection)
	{
		this.connection = connection;
	}
	
	// Here List is used instead of using directly ArrayList, because if we need to change the type of list in the future
	// We need to modify only this method and not the ones that call it
	public List<Offer> getOffersByAuctionId(int auctionId) throws SQLException
	{
		// There is no need to specify the type parameter twice
		// An array list is used because the order is preserved
		List<Offer> offers = new ArrayList<>();
		Offer offer = null;
		
		// Query with parameters, all offers are ordered by times, descending
		// If there are more offers with the same datetime, orders them by value descending
		query = "SELECT * FROM offer WHERE auction = ? ORDER BY time DESC, value DESC";
		
		try {
			// A prepared statement is used here because the query contains parameters
			statement = connection.prepareStatement(query);
			// This sets the auctionId as first parameter of the query
			statement.setInt(1, auctionId);
			result = statement.executeQuery();
			// Result contains all offers related to the specified auction
			while(result.next()) {
				// Here an Offer object is initialized and the attributes obtained from the database are set
				offer = new Offer();
				offer.setId(result.getInt("id"));
				offer.setAuction(result.getInt("auction"));
				offer.setUser(result.getString("user"));
				offer.setValue(result.getInt("value"));
				// This converts the timestamp saved in the database to a LocalDateTime object before assigning it
				offer.setTime(result.getTimestamp("time").toLocalDateTime());
				// This adds the current offer to the list
				offers.add(offer);
			}
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new SQLException(e);

		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				statement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}	
		return offers;
	}
	
	// This method retrieves the offer with the maximum value for the specified Auction
	public Offer getMaxOfferByAuctionId(int auctionId) throws SQLException
	{
		Offer offer = null;
		
		// Query with one parameter
		query = "SELECT * FROM offer WHERE value=(SELECT max(value) FROM offer WHERE auction= ?)";
		
		try {
			// A prepared statement is used here because the query contains parameters
			statement = connection.prepareStatement(query);
			// This sets the auctionId as first parameter of the query
			statement.setInt(1, auctionId);
			result = statement.executeQuery();
			// Result contains the maximum offer related to the specified auction, if there is one
			if(result.next()) {
				// Here an Offer object is initialized and the attributes obtained from the database are set
				offer = new Offer();
				offer.setId(result.getInt("id"));
				offer.setAuction(result.getInt("auction"));
				offer.setUser(result.getString("user"));
				offer.setValue(result.getInt("value"));
				// This converts the timestamp saved in the database to a LocalDateTime object before assigning it
				offer.setTime(result.getTimestamp("time").toLocalDateTime());
			}
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new SQLException(e);

		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				statement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}	
		return offer;
	}
	
	public boolean addOffer(int auction, String username, int value, LocalDateTime time) throws SQLException
	{
		String query = "INSERT INTO offer (auction, user, value, time) VALUES(?, ?, ?, ?)";
		int result = 0;
		try {
			statement = connection.prepareStatement(query);
			statement.setInt(1, auction);
			statement.setString(2, username);
			statement.setInt(3, value);
			// This is used to add a LocalDAetTime to the database
			statement.setObject(4, time);
			result = statement.executeUpdate();
			// If there is an affected row, it means the user has been added
			if(result > 0)
				return true;
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				statement.close();
			} catch (Exception e1) {}
		}
		return false;
	}

}