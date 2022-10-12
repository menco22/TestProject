package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
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
import utilities.DiffTime;


@WebServlet("/GoToVendo")
public class GoToVendo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	
    public GoToVendo() {
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
    
    private void setupPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	List<Auction> auctions = null;
    	Article article = null;
    	Offer maxOffer = null;
    	// Linked Hash Maps are used because they preserve the order of the elements
    	// All auctions with their articles are stored inside them
    	LinkedHashMap<Auction,Article> userOpenAuctions = new LinkedHashMap<>();
    	LinkedHashMap<Auction, Article> userClosedAuctions = new LinkedHashMap<>();
    	// The order here is not important
    	HashMap<Integer, DiffTime> remainingTimes = new HashMap<>();
    	HashMap<Integer, Offer> maxOffers = new HashMap<>();
    	// The cast is required to convert from Object to User
    	User user = (User) request.getSession(false).getAttribute("currUser");
    	
    	// Checks if the connection is active
    	if(checkConnection(connection))
    	{
    		// Here the AuctionDao is initialized
    		AuctionDao auc = new AuctionDao(connection);
    		try {
    			// This is used to retrieve all user's auctions
    			// the query returns the auction ordered by deadline, ascending
				auctions = auc.getAllUserAuctions(user.getUsername());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				response.sendError(500, "Errore, accesso al database fallito!");
				return;
			}
			// Checks if the list is null -->  && !auctions.isEmpty() ??
			if(auctions != null)
			{	
				// Here the ArticleDao is initialized
				ArticleDao art = new ArticleDao(connection);
				// Here the ArticleDao is initialized
				OfferDao off = new OfferDao(connection);
				
				// Iterates over the list of auctions
				for(Auction auction : auctions)
				{
		    		try {
						// This is used to retrieve the articles related to each auction
		    			// There is no reason to check if the article is null, because auctions and articles are created together
		    			// if the auction exists, the article exists too
						article = art.getArticleByCode(auction.getArticle());
						maxOffer = off.getMaxOfferByAuctionId(auction.getId());
					} catch (SQLException e) {
						e.printStackTrace();
						response.sendError(500, "Errore, accesso al database fallito!");
						return;
					}
		    		
						// This filters all auctions by their current state and adds them to the corresponding LinkedHashMap
			    		// along with their articles
						if(auction.isClosed() == false) {
							userOpenAuctions.put(auction, article);
						}else
						{
							userClosedAuctions.put(auction, article);
						}
						
						int aucId = auction.getId();
						//Get the remaining time before the expiration date of the auction
						//Calculated from the creation time of the session
						LocalDateTime logLdt = (LocalDateTime) request.getSession(false).getAttribute("creationTime");
						DiffTime diff = DiffTime.getRemainingTime(logLdt, auction.getDeadline());
						remainingTimes.put(aucId, diff);
						// Sets the maximum offer for eaxh auction if there is any
						if(maxOffer != null)
							maxOffers.put(aucId, maxOffer);
				}
			}		
			
    		// Javascript version
    		String jsParam = request.getParameter("js");
    		if(jsParam != null && jsParam.equals("setup"))
    		{
    			// Converts the awardedArticle object to json and returns it
    			// 'enableComplexMapKeySerialization()' is required in order to convert
    			// the HasMap to json, since it's keys are objects
    			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        		String json1 = gson.toJson(userOpenAuctions);
        		String json2 = gson.toJson(userClosedAuctions);
        		String json3 = gson.toJson(maxOffers);
        		// Creates a list of objects that will be returned
        		String json = "[" + json1 + "," + json2 + "," + json3 + "]";
    			response.setContentType("application/json");
    			response.setCharacterEncoding("UTF-8");
    			response.getWriter().write(json);
    		}
    		// Pure Html version
    		else
    		{
    			// Get the current localdatetime and creates the ldt variable
    			// It is used as initial value for the datetime-local tag in vendo.html
    			LocalDateTime ldt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    			
    			// Redirects to vendo.html and add missions to the parameters
    			String path = "/WEB-INF/vendo.html";
    			ServletContext servletContext = getServletContext();
    			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
    			// Creates and sets 4 variables to use inside the template page
    			ctx.setVariable("userOpenAuctions", userOpenAuctions);
    			ctx.setVariable("userClosedAuctions", userClosedAuctions);
    			ctx.setVariable("remainingTimes", remainingTimes);
    			ctx.setVariable("maxOffers", maxOffers);
    			ctx.setVariable("ldt", ldt);
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
			setupPage(request, response);
    	}
    	
    }
    
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// If the session doesn't exist or is expired, the user is prompted to log in.
		if(request.getSession(false) == null || request.getSession(false).getAttribute("currUser") == null)
		{
			response.sendRedirect("login.jsp");
		}
		else
		{
			setupPage(request, response);
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}