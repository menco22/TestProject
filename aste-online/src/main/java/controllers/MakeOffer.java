package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Auction;
import beans.Offer;
import beans.User;
import dao.AuctionDao;
import dao.OfferDao;

@WebServlet("/MakeOffer")
public class MakeOffer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public MakeOffer() {
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
    
    // This method checks if the value meets the requirements
    private boolean checkValue(int offerValue, Auction auction, Offer maxAuctionOffer)
    {
    	// Min sets the minum value the user can submit
    	int min = 0;
    	// 2 billions is the maximum value allowed
    	int max = 2000000000;
    	
    	// If there is at least an offer for the specified auction
    	// the minimum value is equal to the minimum upside offer plus
    	// the value of the maximum offer
    	if(maxAuctionOffer != null)
    		min = auction.getMinUpsideOffer() + maxAuctionOffer.getValue();
    	// If there are no offers for the specified auction
    	// the minimum value is equal to the initial price of the auction
    	else
    		min = auction.getInitialPrice();
    	
    	// The offer's value is correct if it stays in between
    	if(min <= offerValue && offerValue <= max)
    		return true;
    	return false;
    }
    
    private void makeOffer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	Auction auction = null;
    	Offer maxAuctionOffer = null;
    	LocalDateTime currLdt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    	User user = (User) request.getSession(false).getAttribute("currUser");
    	String username = user.getUsername();
    	// Used once the offer has been created to redirect to GetAuctionsDetails
    	// In order to work, the controller needs 2 parameters, the auctionId and
    	// the page to process
    	String strAucId = request.getParameter("auctionId");
    	int aucId, offerValue;
    	// Used to check if the offer has been added correctly
    	boolean result = false;
    	
    	try {
    		// These retrieve the auctionId and the value of the offer
        	aucId = Integer.parseInt(strAucId);
        	offerValue = Integer.parseInt(request.getParameter("offer"));
    	}
		catch(Exception e)
		{
			// If the values can't be parsed they are not formatted correctly
			e.printStackTrace();
			response.sendError(400, "Errore, l'id dell' asta e il valore dell' offerta devono essere numeri 'interi'!");
			return;
		}
    	
		// Checks if the connection is active
		if(checkConnection(connection))
		{
    		// Here the AuctionDao is initialized
    		AuctionDao auc = new AuctionDao(connection);
    		try {
	    		// This return the Auction object related to the specified auction if it exists or null
				auction = auc.getAuctionById(aucId);	
			} catch (SQLException e) {
				e.printStackTrace();
				response.sendError(500, "Errore, accesso al database fallito!");
				return;
			}
    		
    		// This means that the specified auction exists
    		if(auction != null)
    		{
    			
    			// Checks if the logged user has created the auction
    			if(auction.getCreator().equals(username))
    			{
    				response.sendError(400, "Errore, non è concesso fare offerte sulle proprie aste!");
    				return;
    			}
    			
    			
        		// Here the AuctionDao is initialized
        		OfferDao off = new OfferDao(connection);
        		
        		try {
	    			// This returns the Offer object related to the maximum offer for specified auction if it exists or null
    				maxAuctionOffer = off.getMaxOfferByAuctionId(aucId);	
    			} catch (SQLException e) {
    				e.printStackTrace();
    				response.sendError(500, "Errore, accesso al database fallito!");
    				return;
    			}
        		
        		
        		// This checks, if there is a maximum offer, if it belongs to the logged user
        		// If so, the user is not allowed to make another offer
        		if(maxAuctionOffer != null && username.equals(maxAuctionOffer.getUser()))
        		{
        			response.sendError(400, "Errore, è necessario attendere che qualcun altro faccia un' offerta, prima di poterne fare una nuova!");
        			return;
        		}
        		
        		
        		// Check if the offer's value submitted for the auction meets the requirements
        		if(checkValue(offerValue, auction, maxAuctionOffer))
        		{
            		try {
    	    			// This returns true if the Offer has been added to the database
        				result = off.addOffer(aucId, username, offerValue, currLdt);	
	    			} catch (SQLException e) {
	    				e.printStackTrace();
	    				response.sendError(500, "Errore, accesso al database fallito!");
	    				return;
	    			}
            		
            		String jsParam = request.getParameter("js");
            		
            		// If the offer has been added redirects to the offerta.html page
            		if(result)
            		{
            			// javascript version
            			if(jsParam != null && jsParam.equals("make"))
            				response.getWriter().write("1");
            			// Pure html version
            			else
            			{
                			// Redirects to the controller that manages both dettagli.html and offerta.html
                			// specifying that the page to process is offerta.html
                			String path = "GetAuctionDetails?auctionId=" + strAucId + "&page=offerta.html";
                			response.sendRedirect(path);           				
            			}
            		}
            		else
            		{
            			// javascript version
            			if(jsParam != null && jsParam.equals("create"))
            				response.getWriter().write("0");
            			// Pure html version
            			else
            			{
            				response.sendError(500, "Errore imprevisto, impossibile salvare l'offerta!");
            			}
            		}
            		
        		}
        		else
        		{
        			response.sendError(400, "Errore, il valore specificato deve essere superiore all' offerta massima attuale di un ammontare"
        					+ " pari almeno al rialzo minimo! Se non ci sono offerte al momento, il valore minimo deve essere pari"
        					+ " o superiore al valore iniziale dell' asta! Il valore massimo è 2 miliardi!");
        		}
        		
    		}
    		else
    		{
    			// The given Id doesn't belong to any of the auctions
    			response.sendError(400, "Errore, l'asta specificata non esiste!");
    		}

		}
		else
		{
			// If the connection is null or closed, it is initialized 
			connectToDb();
			// Coming here means that the current method should have been executed, now that the connection is restored
			// It's possible to continue.
			makeOffer(request, response);
		}
    	
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// If the session doesn't exist or is expired, the user is prompted to log in.
		if(request.getSession(false) == null || request.getSession(false).getAttribute("currUser") == null)
		{
			response.sendRedirect("login.jsp");
		}
		// Page is a parameter that allows to distinguish between the dettagli.html and offerta.html pages
		else if(request.getParameter("auctionId") != null && request.getParameter("offer-submit") != null)
		{
			makeOffer(request, response);
		}
		else
		{
			// The auctionId parameter is missing
			response.sendError(400, "Errore, parametri mancanti nella richiesta!");
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}