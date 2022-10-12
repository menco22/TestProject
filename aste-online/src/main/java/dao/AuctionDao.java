package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import beans.Auction;

public class AuctionDao {
	private Connection connection = null;
	String query = "";
	PreparedStatement statement = null;
	ResultSet result = null;
	
	public AuctionDao(Connection connection)
	{
		this.connection = connection;
	}
	
	// Here List is used instead of using directly ArrayList, because if we need to change the type of list in the future
	// We need to modify only this method and not the ones that call it
	public List<Auction> getAllClosedAuctions() throws SQLException
	{
		// There is no need to specify the type parameter twice
		// An array list is used because the order is preserved
		List<Auction> auctions = new ArrayList<>();
		Auction auction = null;
		
		// Query with parameters, all auctions are ordered by deadlines, ascending
		query = "SELECT * FROM auction WHERE closed = 1";
		
		try {
			statement = connection.prepareStatement(query);
			result = statement.executeQuery();
			// Result contains all closed auctions in the database
			while(result.next()) {
				// Here an Auction object is initialized and the attributes obtained from the database are set
				auction = new Auction();
				auction.setId(result.getInt("id"));
				auction.setArticle(result.getInt("article"));
				auction.setCreator(result.getString("creator"));
				auction.setInitialPrice(result.getInt("initialPrice"));
				auction.setMinUpsideOffer(result.getInt("minUpsideOffer"));
				// This converts the timestamp saved in the database to a LocalDateTime object before assigning it
				auction.setDeadline(result.getTimestamp("deadline").toLocalDateTime());
				auction.setClosed(result.getBoolean("closed"));
				// This adds the current auction to the list
				auctions.add(auction);
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
		return auctions;
	}
	
	// Here List is used instead of using directly ArrayList, because if we need to change the type of list in the future
	// We need to modify only this method and not the ones that call it
	public List<Auction> getAllUserAuctions(String username) throws SQLException
	{
		// There is no need to specify the type parameter twice
		// An array list is used because the order is preserved
		List<Auction> auctions = new ArrayList<>();
		Auction auction = null;
		
		// Query with parameters, all auctions are ordered by deadlines, ascending
		query = "SELECT * FROM auction WHERE creator = ? ORDER BY deadline ASC";
		
		try {
			// A prepared statement is used here because the query contains parameters
			statement = connection.prepareStatement(query);
			// This sets the username as first parameter of the query
			statement.setString(1, username);
			result = statement.executeQuery();
			// Result contains all auctions in the database
			while(result.next()) {
				// Here an Auction object is initialized and the attributes obtained from the database are set
				auction = new Auction();
				auction.setId(result.getInt("id"));
				auction.setArticle(result.getInt("article"));
				auction.setCreator(result.getString("creator"));
				auction.setInitialPrice(result.getInt("initialPrice"));
				auction.setMinUpsideOffer(result.getInt("minUpsideOffer"));
				// This converts the timestamp saved in the database to a LocalDateTime object before assigning it
				auction.setDeadline(result.getTimestamp("deadline").toLocalDateTime());
				auction.setClosed(result.getBoolean("closed"));
				// This adds the current auction to the list
				auctions.add(auction);
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
		return auctions;
	}
	
	public Auction getUserAuctionById(String username, int id) throws SQLException
	{
		Auction auction = null;
		// Query with parameters
		query = "SELECT * FROM auction WHERE id = ? AND creator = ?";
		
		try {
			// A prepared statement is used here because the query contains parameters
			statement = connection.prepareStatement(query);
			// This sets the id as first parameter of the query
			statement.setInt(1, id);
			// This sets the username as second parameter of the query
			statement.setString(2, username);
			result = statement.executeQuery();
			// Result contains the auction if it exists
			if(result.next()) {
				// Here an Auction object is initialized and the attributes obtained from the database are set
				auction = new Auction();
				auction.setId(result.getInt("id"));
				auction.setArticle(result.getInt("article"));
				auction.setCreator(result.getString("creator"));
				auction.setInitialPrice(result.getInt("initialPrice"));
				auction.setMinUpsideOffer(result.getInt("minUpsideOffer"));
				// This converts the timestamp saved in the database to a LocalDateTime object before assigning it
				auction.setDeadline(result.getTimestamp("deadline").toLocalDateTime());
				auction.setClosed(result.getBoolean("closed"));
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
		return auction;
	}
	
	public Auction getAuctionById(int id) throws SQLException
	{
		Auction auction = null;
		// Query with one parameter
		query = "SELECT * FROM auction WHERE id = ?";
		
		try {
			// A prepared statement is used here because the query contains parameters
			statement = connection.prepareStatement(query);
			// This sets the id as first parameter of the query
			statement.setInt(1, id);
			result = statement.executeQuery();
			// Result contains the auction if it exists
			if(result.next()) {
				// Here an Auction object is initialized and the attributes obtained from the database are set
				auction = new Auction();
				auction.setId(result.getInt("id"));
				auction.setArticle(result.getInt("article"));
				auction.setCreator(result.getString("creator"));
				auction.setInitialPrice(result.getInt("initialPrice"));
				auction.setMinUpsideOffer(result.getInt("minUpsideOffer"));
				// This converts the timestamp saved in the database to a LocalDateTime object before assigning it
				auction.setDeadline(result.getTimestamp("deadline").toLocalDateTime());
				auction.setClosed(result.getBoolean("closed"));
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
		return auction;
	}
	
	// Here List is used instead of using directly ArrayList, because if we need to change the type of list in the future
	// We need to modify only this method and not the ones that call it
	public List<Auction> getAuctionsByKeyword(String keyword) throws SQLException
	{
		// There is no need to specify the type parameter twice
		// An array list is used because the order is preserved
		List<Auction> auctions = new ArrayList<>();
		Auction auction = null;
		
		// Query with parameters, all auctions are ordered by deadlines, descending
		query = "SELECT * FROM auction au, article ar WHERE au.article = ar.code AND (ar.name LIKE ? OR ar.name LIKE ? OR ar.name LIKE ? OR ar.name LIKE ? OR  ar.description LIKE ? OR ar.description LIKE ? OR ar.description LIKE ? OR ar.description LIKE ?) ORDER BY au.deadline DESC";
		
		try {
			String one = "% " + keyword + "%";
			String two = "% " + keyword + " %";
			String three = "%" + keyword + " %";
			String four = "%" + keyword + "%";
			// A prepared statement is used here because the query contains parameters
			statement = connection.prepareStatement(query);	
			statement.setString(1, one);
			statement.setString(2, two);
			statement.setString(3, three);
			statement.setString(4, four);
			statement.setString(5, one);
			statement.setString(6, two);
			statement.setString(7, three);
			statement.setString(8, four);
			result = statement.executeQuery();
			// Result contains all auctions in the database
			while(result.next()) {
				// Here an Auction object is initialized and the attributes obtained from the database are set
				auction = new Auction();
				auction.setId(result.getInt("au.id"));
				auction.setArticle(result.getInt("au.article"));
				auction.setCreator(result.getString("au.creator"));
				auction.setInitialPrice(result.getInt("au.initialPrice"));
				auction.setMinUpsideOffer(result.getInt("au.minUpsideOffer"));
				// This converts the timestamp saved in the database to a LocalDateTime object before assigning it
				auction.setDeadline(result.getTimestamp("au.deadline").toLocalDateTime());
				auction.setClosed(result.getBoolean("au.closed"));
				// This adds the current auction to the list
				auctions.add(auction);
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
		return auctions;
	}

	
	public boolean addAuction(int article, String creator, int initialPrice, int minUpsideOffer, LocalDateTime deadline) throws SQLException
	{
			String query = "INSERT INTO auction (article, creator, initialPrice, minUpsideOffer, deadline) VALUES(?, ?, ?, ?, ?)";
			int result = 0;
			try {
				statement = connection.prepareStatement(query);
				statement.setInt(1, article);
				statement.setString(2, creator);
				statement.setInt(3, initialPrice);
				statement.setInt(4, minUpsideOffer);
				// This is used to add a LocalDAetTime to the database
				statement.setObject(5, deadline);
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
	
	public boolean closeAuction(int id) throws SQLException
	{
		// Query with one parameter used to update the current auction's record and set it closed
		query = "UPDATE auction SET closed = 1 WHERE id = ?";
		int result = 0;		
		try {
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			result = statement.executeUpdate();
			// If there is an affected row, it means that the auction has been closed
			if(result > 0)
				return true;
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new SQLException(e);
		} finally {
			try {
				statement.close();
			} catch (Exception e1) {}
		}		
		return false;
	}
}