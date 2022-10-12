package controllers;

import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.Article;
import beans.Auction;
import beans.Offer;
import beans.User;
import dao.ArticleDao;
import dao.AuctionDao;
import dao.OfferDao;
import dao.UserDao;


@WebServlet("/GetAuctionDetails")
public class GetAuctionDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	
    public GetAuctionDetails() {
        super();
        // TODO Auto-generated constructor stub
    }

    // Method used for the configuration of the servlet
    // Here the connection to the database is initialized
    // Executed only once
    public void init()
    {
    	initializeEngine();
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
    
    // This initializes the thymeleaf engine
    private void initializeEngine()
    {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding("ISO-8859-1");
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
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

    private void setupPage(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException
    {
    	Auction auction = null;
    	Article article = null;
    	Offer maxAuctionOffer = null;
    	List<Offer> auctionOffers = null;
//    	This HashMap contains all the offers with their creationTimes, formatted properly
    	LinkedHashMap<Offer, String> frmtAuctionOffers = null;
    	String frmtDeadline = null;
    	// Used to check if the auction is expired
    	LocalDateTime currLdt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    	boolean isExpired = false;
    	int aucId = 0;  	
    	User user = (User)request.getSession(false).getAttribute("currUser");
    	// This is the user who has won the auction, if it has been closed already
    	User awardedUser = null;
    	
    	try {
    		// Tries to parse the auction id from the request
    		aucId = Integer.parseInt(request.getParameter("auctionId"));
    	}catch(NumberFormatException e)
    	{
    		response.sendError(400, "Errore, l' id deve essere un numero intero!");
    		return;
    	}

    	
    	// Checks if the connection is active
    	if(checkConnection(connection))
    	{
    		// Here the AuctionDao is initialized
    		AuctionDao auc = new AuctionDao(connection);
    		try {
    			
    			if(page.equals("dettagli.html"))
    			{
        			// This return the Auction object related to the specified user auction if it exists or null
    				auction = auc.getUserAuctionById(user.getUsername(), aucId);
    			}
    			else
    			{
	    			// This return the Auction object related to the specified auction if it exists or null
					auction = auc.getAuctionById(aucId);
    			}
    			
			} catch (SQLException e) {
				e.printStackTrace();
				response.sendError(500, "Errore, accesso al database fallito!");
				return;
			}
    		
    		// This means that the specified auction exists
    		if(auction != null)
    		{
    			
				// Here the ArticleDao, the OfferDao and then UserDao are initialized
				ArticleDao art = new ArticleDao(connection);
				OfferDao off = new OfferDao(connection);
				UserDao usr = new UserDao(connection);
				
	    		try {
					// This is used to retrieve the articles related to each auction
	    			// There is no reason to check if the article is null, because auctions and articles are created together
	    			// if the auction exists, the article exists too
					article = art.getArticleByCode(auction.getArticle());
					// There is no need to check if the offers' list is null or empty
					// The template page will check this condition
					auctionOffers = off.getOffersByAuctionId(aucId);
					// This retrieves the maximum offer for the specified auction
					maxAuctionOffer = off.getMaxOfferByAuctionId(aucId);
					
					if(maxAuctionOffer != null)
					{
						// Since there is a maximum offer that belongs to the user, the user surely exists
						awardedUser = usr.getUserByUsername(maxAuctionOffer.getUser());
						
						if(awardedUser != null)
						{
							// Removes the password from the object for security purposes
							awardedUser.setPassword("");							
						}
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
					response.sendError(500, "Errore, accesso al database fallito!");
					return;
				}
	    		
	    		// Proceeds only if there is at least 1 offer for the auction
	    		if(auctionOffers != null)
	    		{	// Initializes the HashMap
	    			frmtAuctionOffers = new LinkedHashMap<>();
	    			// Iterates over the list of offers and formats the datetime properly
		    		for(Offer offer : auctionOffers)
		    		{
		    			String frmtOfferTime = offer.getTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy 'alle' HH:mm"));
		    			// Adds both the offer and it's formatted datetime
		    			frmtAuctionOffers.put(offer, frmtOfferTime);
		    		}
	    		}

	    		
	    		// This changes the deadline format in order to be more readable when showed in the html page
	    		frmtDeadline = auction.getDeadline().format(DateTimeFormatter.ofPattern("dd MMM yyyy 'ore' HH:mm"));
				// Checks if the auction is expired, the variable is used inside the dettagli.html and offerta.html pages
				isExpired = currLdt.isAfter(auction.getDeadline());
    		}
    		
    		// Javascript version
    		String jsParam = request.getParameter("js");
    		if(jsParam != null && jsParam.equals("details"))
    		{
    			// Converts all objects to json and returns them as a list
    			// 'enableComplexMapKeySerialization()' is required in order to convert
    			// the HasMap to json, since it's keys are objects
    			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
    			// The auctions are retrieved by the filterAuctions method above
    			// that sets them as a request attribute
    			// auctions contains all the filteredOpenAuctions
    			String json1 = gson.toJson(auction);
    			String json2 = gson.toJson(article);
    			String json3 = gson.toJson(frmtDeadline);
    			String json4 = gson.toJson(isExpired);
    			String json5 = gson.toJson(awardedUser);
    			String json6 = gson.toJson(maxAuctionOffer);
    			// Creating a list makes it easier to access
    			// each element from javascript
    			String json = "[" + json1 +
    					"," + json2 +
    					"," + json3 +
    					"," + json4 +
    					"," + json5 +
    					"," + json6 + "]";
    			response.setContentType("application/json");
    			response.setCharacterEncoding("UTF-8");
    			response.getWriter().write(json);
    		}
    		else if(jsParam != null && jsParam.equals("offers"))
    		{
    			// Converts all objects to json and returns them as a list
    			// 'enableComplexMapKeySerialization()' is required in order to convert
    			// the HasMap to json, since it's keys are objects
    			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
    			String json1 = gson.toJson(isExpired);
    			String json2 = gson.toJson(frmtAuctionOffers);
    			String json3 = gson.toJson(maxAuctionOffer);
    			String json4 = gson.toJson(auction);
    			// Creating an object makes it easier to access
    			// each element from javascript
    			String json = "[" + json1 +
    					"," + json2 +
    					"," + json3 + 
    					"," + json4 + "]";
    			response.setContentType("application/json");
    			response.setCharacterEncoding("UTF-8");
    			response.getWriter().write(json);
    		}
    		// Pure html version
    		else
    		{
    			// Redirects to dettagli.html or to offerta.html and adds missions to the parameters
    			String path = "/WEB-INF/" + page;
    			ServletContext servletContext = getServletContext();
    			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
    			// Creates and sets 7 variables to use inside the 2 template pages
    			ctx.setVariable("auction", auction);
    			ctx.setVariable("article", article);
    			ctx.setVariable("frmtDeadline", frmtDeadline);
    			ctx.setVariable("isExpired", isExpired);
    			ctx.setVariable("offers", frmtAuctionOffers);
    			ctx.setVariable("maxAuctionOffer", maxAuctionOffer);
    			ctx.setVariable("awardedUser", awardedUser);
    			// This actually processes the template page
    			templateEngine.process(path, ctx, response.getWriter());   			
    		}
    	}
    	else
    	{
			// If the connection is null or closed, it is initialized 
			connectToDb();
			// Coming here means that the current method should have been executed, now that the connection is restored
			// It's possible to continue.
			setupPage(request, response, page);
    	}

    }
    
    // This controller manages 2 thymeleaf pages
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String page = request.getParameter("page");
		// If the session doesn't exist or is expired, the user is prompted to log in.
		if(request.getSession(false) == null || request.getSession(false).getAttribute("currUser") == null)
		{
			response.sendRedirect("login.jsp");
		}
		// Page is a parameter that allows to distinguish between the dettagli.html and offerta.html pages
		else if(request.getParameter("auctionId") != null && (page.equals("dettagli.html") || page.equals("offerta.html")))
		{
			setupPage(request, response, page);
		}
		else
		{
			// The auctionId parameter is missing
			response.sendError(400, "Errore, parametri mancanti o errati nella richiesta!");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}