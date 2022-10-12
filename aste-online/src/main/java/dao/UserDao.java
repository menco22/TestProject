package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.User;


public class UserDao {
	private Connection connection = null;
	String query = "";
	PreparedStatement statement = null;
	ResultSet result = null;
	
	public UserDao(Connection connection)
	{
		this.connection = connection;
	}

	// This method checks if the given username and password match a record in the database
	// If there is a match it means the user exists and it's returned
	public User getUser(String username, String password) throws SQLException {
		User user = null;
		// Query with parameters
		query = "SELECT * FROM user WHERE username = ? and password = ?";
		
		try {
			// A prepared statement is used here because the query contains parameters
			statement = connection.prepareStatement(query);
			// This sets the username as first parameter of the query
			statement.setString(1, username);
			// This sets the username as second parameter of the query
			statement.setString(2, password);
			result = statement.executeQuery();
			// If there is a match the entire row is returned here as a result
			if(result.next()) {
				// Here a User object is initialized and the attributes obtained from the database are set
				user = new User();
				user.setId(result.getInt("id"));
				user.setUsername(result.getString("username"));
				user.setPassword(result.getString("password"));
				user.setAddress(result.getString("address"));
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
		return user;
	}
	
	// This method checks if the given username and password match a record in the database
	// If there is a match it means the user exists and it's returned
	public User getUserByUsername(String username) throws SQLException {
		User user = null;
		// Query with parameters
		query = "SELECT * FROM user WHERE username = ?";
		
		try {
			// A prepared statement is used here because the query contains parameters
			statement = connection.prepareStatement(query);
			// This sets the username as first parameter of the query
			statement.setString(1, username);
			result = statement.executeQuery();
			// If there is a match the entire row is returned here as a result
			if(result.next()) {
				// Here a User object is initialized and the attributes obtained from the database are set
				user = new User();
				user.setId(result.getInt("id"));
				user.setUsername(result.getString("username"));
				user.setPassword(result.getString("password"));
				user.setAddress(result.getString("address"));
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
		return user;
	}
}