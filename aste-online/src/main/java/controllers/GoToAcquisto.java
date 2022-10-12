package controllers;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.Article;
import beans.Auction;
import beans.Offer;
import beans.User;
import dao.ArticleDao;
import dao.AuctionDao;
import dao.OfferDao;
import utilities.DiffTime;


@WebServlet("/GoToAcquisto")
public class GoToAcquisto extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
//	private TemplateEngine templateEngine;

    public GoToAcquisto() {
        super();
        // TODO Auto-generated constructor stub
    }

    // Method used for the configuration of the servlet
    // Here the connection to the database is initialized
    // Executed only once
    public void init()
    {
//    	initializeEngine();
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
    
    private boolean validateKey(String key)
    {
    	// Checks if the key contains only letters and is longer than 2 characters, but less than 21
    	if(key.matches("[a-zA-Z]+") && key.length() > 2 && key.length() < 21)
    		return true;
    	return false;
    }
    
    // This method filters all auctions by looking inside the relative articles' names and descriptions and checking if the keyword is present
    private boolean filterAuctions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	List<Auction> auctions = new ArrayList<>();
    	Article article = null;
    	// Used to calculate the remaining time before the expiration of 
		LocalDateTime logLdt = (LocalDateTime) request.getSession(false).getAttribute("creationTime");
    	// Used to check if the deadline of each auction is after the current datetime
    	LocalDateTime currLdt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    	// The Linked Hash Maps is used because it preserves the order of the elements
    	// All auctions with their articles are stored inside them
    	LinkedHashMap<Auction,Article> filteredOpenAuctions = new LinkedHashMap<>();
    	// The order here is not important
    	HashMap<Integer, DiffTime> remainingTimes = new HashMap<>();
    	String key = request.getParameter("key");
    	if(!validateKey(key))
    	{
    		response.sendError(400, "Errore, la chiave può contenere solo lettere non accentate!"
    				+ " Inoltre deve avere una lunghezza compresa tra i 3 e 20 caratteri.");
    		return false;
    	}
    	// Proceeds only if the key is valid
    	else
    	{
        	// Checks if the connection is active
        	if(checkConnection(connection))
        	{
        		// Here the AuctionDao is initialized
        		AuctionDao auc = new AuctionDao(connection);
        		try {
        			// This returns all the auctions related to the articles that contain the specified keyword
    				auctions = auc.getAuctionsByKeyword(key);
    			} catch (SQLException e) {
    				e.printStackTrace();
    				response.sendError(500, "Errore, accesso al database fallito!");
    				return false;
    			}
        		
        		// This means that the there is at least one auction for the given keyword
        		if(auctions != null)
        		{
    				// Here the ArticleDao is initialized
    				ArticleDao art = new ArticleDao(connection);
    				
    				// Iterates over the list of auctions
    				for(Auction auction : auctions)
    				{
    					// This filters the auctions by their current state and checks if their deadlines are after the datetime related
    					// to the submit of the keyword. After that it adds the auctions to the LinkedHashMap, along with their articles.
    					if(!auction.isClosed() && auction.getDeadline().isAfter(currLdt))
    					{
    			    		try {
    							// This is used to retrieve the articles related to each auction
    			    			// There is no reason to check if the article is null, because auctions and articles are created together
    			    			// See CreateAuction, all changes to the db are committed only if there are no errors
    			    			// So, if the auction exists, the article exists too
    							article = art.getArticleByCode(auction.getArticle());
    						} catch (SQLException e) {
    							e.printStackTrace();
    							response.sendError(500, "Errore, accesso al database fallito!");
    							return false;
    						}
    			    			// Adds the auction to the LinkedHashMap along with it's article
    							filteredOpenAuctions.put(auction, article);
    							//Get the remaining time before the expiration date of the auction
    							//Calculated from the creation time of the session
    							DiffTime diff = DiffTime.getRemainingTime(logLdt, auction.getDeadline());
    							remainingTimes.put(auction.getId(), diff);
    					}
    	    		}
        		}
        		// Here some attributes are set, but the request is always forwarded by setupPage.
        		
        		// Sets the LinkedHashMap, containing the auctions and the articles as an attribute of the request
        		request.setAttribute("auctions", filteredOpenAuctions);
        		// Sets the HashMap, containing the auctions and the remaing time till the expiration for each of them
        		request.setAttribute("remainingTimes", remainingTimes);
        		// Sets the key as attribute in order to use it inside the jsp page
        		request.setAttribute("key", key);
        	}
        	else
        	{
    			// If the connection is null or closed, it is initialized 
    			connectToDb();
    			// Coming here means that the current method should have been executed, now that the connection is restored
    			// It's possible to continue.
    			filterAuctions(request, response);
        	}
        	
    	}
    	// Every time there is an error, the method returns false
    	// so it's possible to execute this line only if there are no errors
    	return true;
    }
    
    // This method retrieves all maximum offer for each closed auction and checks if they belongs to the user
    // Then forwards the request with the required objects to the acquisto.jsp page
    // If the key parameter has been set, the request will include also the objects set by filterAuctions above.
    private void setupPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	Offer maxOffer = null;
    	Article article = null;
    	List<Auction> auctions = null;
    	//HashMap that contains all user's awarded articles along with the winning offers
    	HashMap<Article, Offer> awardedArticles = new HashMap<>();
    	User user = (User) request.getSession(false).getAttribute("currUser");
    	String username = user.getUsername();
        
    	
    	// Checks if the connection is active
    	if(checkConnection(connection))
    	{
    		// Here the AuctionDao is initialized
    		AuctionDao auc = new AuctionDao(connection);
    		try {
    			// This returns all the closed auctions
				auctions = auc.getAllClosedAuctions();
			} catch (SQLException e) {
				e.printStackTrace();
				response.sendError(500, "Errore, accesso al database fallito!");
				return;
			}
    		
    		// This means that the there is at least one closed auction
    		if(auctions != null)
    		{
				// Here the ArticleDao is initialized
				ArticleDao art = new ArticleDao(connection);
				// Here the OfferDao is initialized
				OfferDao off = new OfferDao(connection);
				
				// Iterates over the list of closed auctions
				for(Auction auction : auctions)
				{
		    		try {
						// This is used to retrieve the articles related to each auction
		    			// There is no reason to check if the article is null, because auctions and articles are created together
		    			// See CreateAuction, all changes to the db are committed only if there are no errors
		    			// So, if the auction exists, the article exists too
		    			article = art.getArticleByCode(auction.getArticle());
		    			// This returns the maximum offer for the current auction if there is one
						maxOffer = off.getMaxOfferByAuctionId(auction.getId());
					} catch (SQLException e) {
						e.printStackTrace();
						response.sendError(500, "Errore, accesso al database fallito!");
						return;
					}
		    		
		    		// This checks if there is a maximum offer for the current auction and
		    		// if it belongs to the logged user
		    		// If the maximum offer of a closed auction belongs to the user, it means the user has
		    		// won the auction
		    		if(maxOffer != null && maxOffer.getUser().equals(username))
		    		{
		    			awardedArticles.put(article, maxOffer);
		    		}
				}
			}
    		
    		String jsParam = request.getParameter("js");

    		// For the javascript version
    		// Used to return only the filtrered open auctions
    		if(request.getParameter("key") != null && jsParam != null && jsParam.equals("filtered"))
    		{
    			// Converts all objects to json and returns them as a list
    			// 'enableComplexMapKeySerialization()' is required in order to convert
    			// the HasMap to json, since it's keys are objects
    			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
    			// The auctions are retrieved by the filterAuctions method above
    			// that sets them as a request attribute
    			// auctions contains all the filteredOpenAuctions
    			String json = gson.toJson(request.getAttribute("auctions"));
    			response.setContentType("application/json");
    			response.setCharacterEncoding("UTF-8");
    			response.getWriter().write(json);
    		}
    		// For the javascript version
    		// Used to return only the awarded auctions
    		else if(jsParam != null && jsParam.equals("awarded"))
    		{
    			// Converts the awardedArticle object to json and returns it
    			// 'enableComplexMapKeySerialization()' is required in order to convert
    			// the HasMap to json, since it's keys are objects
    			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        		String json = gson.toJson(awardedArticles);
    			response.setContentType("application/json");
    			response.setCharacterEncoding("UTF-8");
    			response.getWriter().write(json);
    		}
    		// Pure Html version
    		else
    		{
        		// Adding an attribute to the request
        		// If the user has submitted a key, there will be more attributes set by filterAuctions
        		request.setAttribute("awardedArticles", awardedArticles);
        		
            	// Forwards the request with the attributes to the acquisto.jsp page
        		// There can be also other attributes set in the request by the filterAuctions method
        		// if it has been called before the current one.
            	request.getRequestDispatcher("/WEB-INF/acquisto.jsp").forward(request, response);
    		}
		}
    	else
    	{
			// If the connection is null or closed, it is initialized 
			connectToDb();
			// Coming here means that the current method should have been executed, now that the connection is restored
			// It's possible to continue.
			setupPage(request, response);
    	}
    }
    
    // Method required for the javascript version
    private void getVisitedAuctions(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
    	Auction auction = null;
    	Article article = null;
    	LinkedHashMap<Auction, Article> visitedOpenAuctions = new LinkedHashMap<>();
    	Cookie[] cookies = request.getCookies();
    	String[] visitedAuctions = {};
    	// Used to check if the auction is expired
    	LocalDateTime currLdt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    	// The username is used to access the correct cookie
    	User user = (User)request.getSession(false).getAttribute("currUser");
    	String username = user.getUsername();
    	
		for(Cookie cookie : cookies)
		{
			// This is how the cookies' names are set in the javascript version
			if(cookie.getName().equals(username + "-AsteOnline"))
			{
				String cookieValue = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);

				// Parsing the string to string array, manually
				String aucIdsStr = cookieValue.substring(cookieValue.indexOf("["), cookieValue.indexOf("]")).replace(" ", "")
						.replace("\"", "").replace("[", "");
				// Creates the string array if the cookie contains some auctions
				if(aucIdsStr != "")
				{
					visitedAuctions = aucIdsStr.split(",");
				}
    		}
    	}
		
    	// Checks if the connection is active
    	if(checkConnection(connection))
    	{
    		// If the cookie doesn't contain auctions' IDs, there is no need
    		// to try to get data from the database.
    		// An empty HashMap will be converted to json and
    		// will be properly handled from javascript.
    		if(visitedAuctions.length > 0)
    		{
	    		AuctionDao auc = new AuctionDao(connection);
	    		
				// Iterates over all auctions IDs
				for(String str : visitedAuctions)
				{
					// Converts the string Id to an int
					int aucId = Integer.parseInt(str);

					try {
		    			// This return the Auction object related to the specified auction if it exists or null
						auction = auc.getAuctionById(aucId);
					} catch (SQLException e) {
						// sends the error back
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						response.getWriter().println("Errore, impossibile reperire i dettagli delle aste!");
						return;
					}
					
					// Checks if the auction is open and not expired
					if(auction != null && auction.getDeadline().isAfter(currLdt) && !auction.isClosed())
					{	
						ArticleDao art = new ArticleDao(connection);
						
						try {
							// This is used to retrieve the articles related to each auction
			    			// There is no reason to check if the article is null, because auctions and articles are created together
			    			// See CreateAuction, all changes to the db are committed only if there are no errors
			    			// So, if the auction exists, the article exists too
							article = art.getArticleByCode(auction.getArticle());
						} catch (SQLException e) {
							// sends the error back
							response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
							response.getWriter().println("Errore, impossibile reperire i dettagli delle aste!");
							return;
						}

						// Adds the auction and it's related article.
						visitedOpenAuctions.put(auction, article);
					}
				}
    		}
			// Converts the List of auctions into a json object and sends it back
			// 'enableComplexMapKeySerialization()' is required in order to convert
			// the HasMap to json, since it's keys are objects
			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
			String json = gson.toJson(visitedOpenAuctions);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
    	}
    	else
    	{
			// If the connection is null or closed, it is initialized 
			connectToDb();
			// Coming here means that the current method should have been executed, now that the connection is restored
			// It's possible to continue.
			getVisitedAuctions(request, response);
    	}
    }
    
    // This is the only controller that manages a jsp page (acquisto.jsp)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// If the session doesn't exist or is expired, the user is prompted to log in.
		if(request.getSession(false) == null || request.getSession(false).getAttribute("currUser") == null)
		{
			response.sendRedirect("login.jsp");
		}
		// The 'key' is the keyword submitted by the user in the form contained inside the page
		else if(request.getParameter("key") != null)
		{
			if(filterAuctions(request, response))
				setupPage(request, response);
		}
		else if(request.getParameter("js") != null && request.getParameter("js").equals("visited"))
		{
			// This method is executed only for the javascript version
			getVisitedAuctions(request, response);
		}
		// If there is no key parameter, proceeds only whit the setup
		else
		{
			setupPage(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}