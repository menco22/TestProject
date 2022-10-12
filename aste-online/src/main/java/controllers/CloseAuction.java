package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Auction;
import beans.User;
import dao.AuctionDao;


@WebServlet("/CloseAuction")
public class CloseAuction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
    public CloseAuction() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    // Method used for the configuration of the servlet
    // Here the connection to the database is initialized
    // Executed only once
    public void init()
    {
    	connection = connectToDb();
    }
    
    // Called once when the servlet gets destroyed
    // Automatically called, not suggested to directly call it
    public void destroy()
    {
    	try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    // This method initializes the connection to the database
    private Connection connectToDb()
    {
		try {
			// The following lines takes the parameters used to log into the database
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			// This initializes the driver used to interact with the database
			Class.forName(driver);
			// This returns the connection
			return DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			return null;
		}
    }
    
    // This method returns true if the connection is active
    private boolean checkConnection(Connection connection)
    {
		try {
			// If the connection is null it means there is a problem connecting to the db
			// If the connection is closed
			if(connection != null && !connection.isClosed())
				return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// This is executed only if the connection is not active
		return false;
    }
    
    // This method checks if the logged user is the creator of the specified auction before closing it
    // If not, he doesn't have the permission to close the auction!
    // Only the owner is allowed to close it.
    // This is done by retrieving the auction with both the Id and the Username and checking if it's null
    private void closeAuction(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
    	User user = (User) request.getSession(false).getAttribute("currUser");
    	// Used at the bottom of this method with sendRedirect
    	String strAucId = request.getParameter("auctionId");
    	Auction auction = null;
    	int aucId;
    	
    	
    	try {
    		aucId = Integer.parseInt(strAucId);
		}
		catch(Exception e)
		{
			// If the value can't be parsed it's not formatted correctly
			e.printStackTrace();
			response.sendError(400, "Errore, l'id dell' asta deve essere un numero intero!");
			return;
		}

    	
    	// Checks if the connection is active
    	if(checkConnection(connection))
    	{
    		// Here the AuctionDao is initialized
    		AuctionDao auc = new AuctionDao(connection);
    		try {
    			// This return the Auction object related to the specific auction if it exists or null
				auction = auc.getUserAuctionById(user.getUsername(), aucId);
			} catch (SQLException e) {
				e.printStackTrace();
				response.sendError(500, "Errore, accesso al database fallito!");
				return;
			}
    		
    		if(auction == null)
    		{
    			response.sendError(400, "Errore, l' utente non ha creato l'asta oppure l'asta non esiste! Solo chi ha creato l'asta può decidere di chiuderla.");
    			return;
    		}
    		else
    		{
    			// Checks if the auction is expired
    			boolean isExpired = LocalDateTime.now().isAfter(auction.getDeadline());
    			// Closes the auction only if it is still open and if it's expired
    			if(isExpired && !auction.isClosed())
    			{
	    			boolean closed = false;
					try {
						closed = auc.closeAuction(aucId);
					} catch (SQLException e) {
						e.printStackTrace();
						response.sendError(500, "Errore, accesso al database fallito!");
						return;
					}
    			}
    			else if(!isExpired)
    			{
    				response.sendError(400, "Errore, non puoi chiudere un' asta che non è ancora scaduta!");
    				return;
    			}
    		}
			// Redirects back to dettagli.html
    		String path = "GetAuctionDetails?auctionId=" + strAucId + "&page=dettagli.html";
    		response.sendRedirect(path);
    	}
    	else
    	{
			// If the connection is null or closed, it is initialized 
			connectToDb();
			// Coming here means that the current method should have been executed, now that the connection is restored
			// It's possible to continue.
			closeAuction(request, response);
    	}
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// If the session doesn't exist or is expired, the user is prompted to log in.
		if(request.getSession(false) == null || request.getSession(false).getAttribute("currUser") == null)
		{
			response.sendRedirect("login.jsp");
		}
		else if(request.getParameter("auctionId") != null)
		{
			closeAuction(request, response);
		}
		else
		{
			// The auctionId parameter is missing
			response.sendError(400, "Errore, parametro mancante nella richiesta!");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}