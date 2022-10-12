package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import beans.User;
import dao.ArticleDao;
import dao.AuctionDao;



@WebServlet("/CreateAuction")
// @MultipartConfig is needed otherwise it will be impossible to parse the parameters as parts from a multipart form
@MultipartConfig
public class CreateAuction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public CreateAuction() {
        super();
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
    
    private boolean checkName(String name)
    {
    	// Ensures the string doesn't contains only spaces and checks if it's length is between 3 and 51 characters
    	if(!name.isBlank() && name.length() > 3 && name.length() < 51)
    		return true;
    	return false;
    }
    
    private boolean checkDescription(String descr)
    {
    	// Checks if the string's length is between 19 and 3001 characters
    	if(!descr.isBlank() && descr.length() > 19 && descr.length() < 3001)
    		return true;
    	return false;
    }
    
    // returns true if the strings meet the requirements
    private boolean validateStrings(String name, String description)
    {
    	if(name != null && description != null)
	    	if(checkName(name) && checkDescription(description))
	    		return true;
    	return false;
    }
    
    // This returns true only if the uploaded file is an image
    private InputStream checkImage(Part image) throws IOException
    {
    	if(image != null)
    	{
        	InputStream imgStream = null;
    		String mimeType = null;
    		imgStream = image.getInputStream();
    		String filename = image.getSubmittedFileName();
    		mimeType = getServletContext().getMimeType(filename);
    		// Since the user could edit the html page, he could change the input type of the image
    		// And if he doesn't upload a file, mimeType is null and this would result in an unexpected server error
    		if(mimeType != null)
	    		// Checks if the uploaded file is an image and if it has been parsed correclty
	    		if(imgStream != null && imgStream.available() > 0 && mimeType.startsWith("image/") )
	    			return imgStream;
    	}
    	return null;
    }
    
    private boolean checkNumbers(int initialPrice, int minUpsideOffer)
    {
      	if(initialPrice > 0 && initialPrice < 700000001 && minUpsideOffer > 49 && minUpsideOffer < 100001)
      		return true;
      	return false;
    }
    
    private boolean checkDatetime(LocalDateTime deadline)
    {
    	// Checks if the datetime provided by the user is after the current one
    	if(deadline.isAfter(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)))
    		return true;
    	return false;
    }
    
    // This method creates a new article and adds it to the db using the articleDao
    private boolean createArticle(int code, String name, String description, InputStream imageStream, HttpServletResponse response) throws IOException, SQLException
    {
		ArticleDao art = new ArticleDao(connection);
		boolean result = false;

		result = art.addArticle(code, name, description, imageStream);

		return result;	
    }
    
    // This method creates a new auction and adds it to the db using the auctionDao
    private boolean createAuction(int article, String creator, int initialPrice, int minUpsideOffer, LocalDateTime deadline, HttpServletResponse response) throws IOException, SQLException
    {
		AuctionDao auc = new AuctionDao(connection);
		boolean result = false;

		result = auc.addAuction(article, creator, initialPrice, minUpsideOffer, deadline);
		
		return result;	
    }
    
    
    private int generateCode(String creator, HttpServletResponse response) throws IOException, SQLException
    {
     	// Generates a random number between 0 and the maximum INTEGER value
    	// The constructor ensures the seeds are different every time
    	Random rand = new Random();
    	int code = rand.nextInt(Integer.MAX_VALUE);
		ArticleDao art = new ArticleDao(connection);
		boolean result = false;
		
		// this checks if the generated code is available for a new article
		result = art.isCodeAvailable(code);

		if(!result)
		{
			// Generates a new code, if the previous one was already in use
			// generateCode is a recursive function
			code = generateCode(creator, response);
		}
		//The code is available, so it can be returned
		return code;
    }
    
    // This methods evaluates if the submitted values meet the requirements and then creates the article and the auction
    private boolean create(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    { 	
    	String name = request.getParameter("name");
    	String description = request.getParameter("description");
    	Part image = request.getPart("image");
    	LocalDateTime deadline = LocalDateTime.now().minusYears(1);
    	int initialPrice, minUpsideOffer;
    	
    	try {
    		// These retrieve the localdatetime value related to the expiration date
    		deadline = LocalDateTime.parse(request.getParameter("deadline")).truncatedTo(ChronoUnit.MINUTES);
        	initialPrice = Integer.parseInt(request.getParameter("initialPrice"));
        	minUpsideOffer = Integer.parseInt(request.getParameter("minUpsideOffer"));
    	}
		catch(Exception e)
		{
			// If the values can't be parsed they are not formatted correctly
			e.printStackTrace();
			response.sendError(400, "Errore, il prezzo iniziale e il rialzo minimo devono essere numeri interi! La scadenza deve essere formattata correttamente e la data scelta deve essere valida!");
			return false;
		}
    	
    	// This retrieves the image with an InputStream
    	InputStream imgStream = checkImage(image);
    	
    	if(request.getParameter("js") != null)
    	{
    		// This encoding conversion is required since XMLHttpRequest changes
    		// the encoding of the strings submitted by the user in the form.
    		// It's not possible to specify a request header from javascript with the charset
    		// because the browser can't handle boundaries and multipart/form-data
    		// that way. The request header is generated automatically by the browser
    		byte[] nameBytes = name.getBytes();
    		byte[] descrBytes = description.getBytes();
    		name = new String(nameBytes, StandardCharsets.UTF_8);
    		description = new String(descrBytes, StandardCharsets.UTF_8);
    	}
    	
    	// The following instructions performs all the validations of the inputs
    	// The two numbers and the datetime are surely correctly parsed if the flow of execution reaches these point,
    	// there is no need to check if the value have been parsed or not, if they weren't there would have been some exceptions	
		if(validateStrings(name, description))
		{
			if(imgStream != null)
			{
				if(checkNumbers(initialPrice, minUpsideOffer))
				{
					if(checkDatetime(deadline))
					{
			    		// Checks if the connection is active
			    		if(checkConnection(connection))
			    		{
			    			// False ensures no new session is created
				        	HttpSession s = request.getSession(false);
				    		String creator = ((User) s.getAttribute("currUser")).getUsername();
				    		int code;
							try {
								// This generates the code for the article
								code = generateCode(creator, response);
								// Manually commits the queries only if both
								// the auction and the article can be created
					    		// It might happen that the article can be added to the database, but the auction can't for some reason 
					    		// In case there's something wrong it's possible to roll back
					    		// Since articles and auctions are closely related, it's better to ensure they are added correctly
								connection.setAutoCommit(false);
								
					    		if(createArticle(code, name, description, imgStream, response))
					    		{
						    		if(createAuction(code, creator, initialPrice, minUpsideOffer, deadline, response))
						    		{
						    			// Arriving here means that both the article and the auction can be created correctly
						    			// At this point, if there are no errors it's possible to proceed by committing the queries to the database
						    			connection.commit();
						    			
						    			// For the javascript version
						    			// Tells that the auction has been created successfully
						    			if(request.getParameter("js") != null && request.getParameter("js").equals("create"))
						    			{
						    				response.getWriter().write("1");
						    			}
						    			// Html version
							    		return true;
						    		}
					    		}
					    	// Catches 2 exceptions
							} catch (IOException | SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								try {
									// Deletes all changes made till now, ensuring they won't be committed to the database
									// in order to avoid creating the article, but not the aution related to it
									connection.rollback();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								response.sendError(500, "Errore, accesso al database fallito!");
								return false;
							}
			    		}
			    		else
			    		{
			    			// If the connection is null or closed, it is initialized 
			    			connectToDb();
			    			// Coming here means that the current method should have been executed, now that the connection is restored
			    			// It's possible to continue.
			    			create(request, response);
			    		}
					}
					else
					{
						response.sendError(400, "Errore, la scadenza deve esser fissata nel futuro, non può essere impostata in un momento passato!");
					}
				}
				else
				{
					response.sendError(400, "Errore, il prezzo iniziale deve essere maggiore di 0, ma al massimo pari a 700 milioni,"
							+ " Il rialzo minimo deve essere almeno pari a 50, ma al massimo pari a 100 mila");
				}
			}
			else
			{
				response.sendError(400, "Errore, il file caricato deve essere un' immagine!");
			}
    	}
		else
		{
			// Wrong name or description
			response.sendError(400, "Errore, il nome dell' articolo non può contenere solo spazi! "
						+ "Inoltre deve avere una lunghezza compresa tra 4 e 50 caratteri. "
						+ "La descrizione deve avere una lunghezza compresa tra 20 e 3000 caratteri!");
		}

		// Html version
		return false;
    }
    
    private void manageAuction(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
		// -1- Checks if the user has submitted the data in the proper form
    	// Since this is a multipart form, the servlet needs the @MultipartConfig annotation at the beginning
    	// in order to parse the parameters.
		// -2- The auction is created only if the values provided by the user meet all the requirements
		// If create returns false, the user will have received an error message, because there is something wrong
		// There is no need to send another error in that case.
		if(request.getParameter("auction-submit") != null)
		{
			// For the pure html version
			if(create(request, response) && (request.getParameter("js") == null || !request.getParameter("js").equals("create")))
			{
				// Redirects to the page where the user will be able to see the new auction he has just created
				response.sendRedirect("GoToVendo");				
			}
		}
		// The user has not submitted the values in the form and is trying to access this controller directly
		else if(request.getParameter("auction-submit") == null)
		{
			response.sendError(400, "Errore, l'accesso diretto non è consentito!");
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
			manageAuction(request, response);
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}