package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

import beans.Article;

public class ArticleDao {
	private Connection connection = null;
	String query = "";
	PreparedStatement statement = null;
	ResultSet result = null;
	
	public ArticleDao(Connection connection)
	{
		this.connection = connection;
	}
	
	// If there is a match it means the article exists and it is returned
	public Article getArticleByCode(int code) throws SQLException
	{
		Article article = null;
		// Query with parameters
		query = "SELECT * FROM article WHERE code = ?";
		
		try {
			// A prepared statement is used here because the query contains parameters
			statement = connection.prepareStatement(query);
			// This sets the article's code as first parameter of the query
			statement.setInt(1, code);
			result = statement.executeQuery();
			// If there is a match the entire row is returned here as a result
			if(result.next()) {
				// Here an Article object is initialized and the attributes obtained from the database are set
				article = new Article();
				article.setId(result.getInt("id"));
				article.setCode(result.getInt("code"));
				article.setName(result.getString("name"));
				article.setDescription(result.getString("description"));
				// The following lines are required to convert the image stream, saved as blob inside the db, to Base64 string
				byte[] imgBytes = result.getBytes("image");
				String encodedImg = Base64.getEncoder().encodeToString(imgBytes);
				article.setImage(encodedImg);
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
		return article;
	}
	
	// This method checks if the given code matches a record in the database
	public boolean isCodeAvailable(int code) throws SQLException
	{
		// Query with parameters
		query = "SELECT code FROM article WHERE code = ?";
		
		try {
			// A prepared statement is used here because the query contains parameters
			statement = connection.prepareStatement(query);
			// This sets the article's code as first parameter of the query
			statement.setInt(1, code);
			result = statement.executeQuery();
			// If there is a match the code is returned by result
			if(result.next()) {
				// If there is a match, it means the code is already in use, so it's not available
				return false;
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
		// The code is available
		return true;
	}
	
	public boolean addArticle(int code, String name, String description, InputStream image) throws SQLException
	{
			String query = "INSERT INTO article (code, name, description, image) VALUES(?, ?, ?, ?)";
			int result = 0;
			try {
				statement = connection.prepareStatement(query);
				statement.setInt(1, code);
				statement.setString(2, name);
				statement.setString(3, description);
				statement.setBlob(4, image);
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