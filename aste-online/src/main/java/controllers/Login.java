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
import javax.servlet.http.HttpSession;

import beans.User;
import dao.UserDao;

@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
    public Login() {
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
    
    private boolean checkUsernameRequirements(String username)
    {
    	// The username must contain only alphanumeric characters and should have a length between 3 and 51 characters.
    	// The username can't be empty and can't contain spaces.
    	if(username.matches("[a-zA-Z0-9]+") && username.length() > 3 && username.length() < 51)
    		return true;
    	return false;

    }
    
    private boolean checkPasswordRequirements(String password)
    {	// The password should contain at least one number
    	// .* accepts everything except \n, 0 or more characters "*"
    	// The password must contain at least 1 number and should have a length between 7 and 51 characters.
    	// The username can't be empty or contain only spaces.
    	if(password.matches(".*[0-9].*") && password.length() > 7 && password.length() < 51)
    		return true;
    	return false;
    }
    
    // This method returns true if the credentials of the user meet the requirements
    private boolean validateCredentials(String username, String password)
    {
    	boolean usrRes = checkUsernameRequirements(username);
    	boolean passRes = checkPasswordRequirements(password);
    	if(usrRes && passRes)
    		return true;
    	return false;
    }
    
    // This method creates the session for the logged user
    private void createSession(HttpServletRequest request, HttpServletResponse response, User user) throws IOException, ServletException
    {
		HttpSession session = request.getSession(true);
		if(session.isNew())
		{
			// This creates a session in which the currUser attribute contains the User object (Bean)
			session.setAttribute("currUser", user);
			// This creates another attribute to save the current localdatetime in 
			// order to compare it to each auction deadline later, and get the remaining time
			session.setAttribute("creationTime", LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
			// The session expires after 30 minutes of inactivity
			session.setMaxInactiveInterval(1800);
			// Once the Session has been created it checks which version of the web application the user has selected
			// and redirects him to the corresponding page
			String version = request.getParameter("version");
			if(version != null)
			{
				// Javascript
				if(version.equals("js"))
					response.sendRedirect("AsteOnline.jsp");
				// Pure Html
				else if(version.equals("html"))
		        	response.sendRedirect("home.jsp");					
				else
					response.sendError(400, "Errore, non è stata selezionata una versione valida!");
			}
			else
			{
				response.sendError(400, "Errore, devi selezionare una versione!");
			}			
		}
		// If there is already a jsessionid
		else
		{
			session.invalidate();
			createSession(request, response, user);
		}
    }
    
    // This manages the login process
    private void manageLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
    	String username = request.getParameter("username");
    	String password = request.getParameter("password");
    	User user = null;
    	
    	// Checks if the connection is active
    	if(checkConnection(connection))
    	{
    		// This validates the credentials, checks if the requirements are met
    		if(validateCredentials(username, password))
    		{
    			UserDao log = new UserDao(connection);
				try {
					user = log.getUser(username, password);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					response.sendError(500, "Errore, accesso al database fallito!");
					return;
				}
    			//The session is created only if the user exists in the db
    			if (user != null)
    			{
    				createSession(request, response, user);
    			}
    			else
    			{
    				response.sendError(400, "Errore, username o password errati!");
    			}
    		}
    		else
    		{
    			// This sends a warning to the user with the requirements for the username and the password
    			response.sendError(400, "Username e password possono contenere al massimo 50 caratteri. L' username deve contenere almeno 4 caratteri (alfanumerici). "
    					+ "La password ha una lunghezza minima di 8 caratteri e deve contenere almeno un numero!");
    		}
    	}
    	else
    	{
			// If the connection is null or closed, it is initialized 
			connectToDb();
			// Coming here means that the current method should have been executed, now that the connection is restored
			// It's possible to continue.
			manageLogin(request, response);
    	}
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// If the session doesn't exist or is expired, the user is prompted to log in.
		if(request.getSession(false) == null || request.getSession(false).getAttribute("currUser") == null)
		{
			// The username and the password are checked only if the user is trying to log in from the login.jsp page
			if(request.getParameter("login-submit") != null)
			{
				// This checks if the user has submitted both the password and the username.
				if(request.getParameter("username") == null || request.getParameter("password") == null)
				{
					response.sendError(505, "Devi inserire sia un username che una password!");
				}
				else
				{
					manageLogin(request, response);
				}
			}
			else
			{
				// The user has not submitted his credentials in the form and is trying to access this servlet directly
				response.sendRedirect("login.jsp");
			}
		}
		else
		{
			// If the user is already logged in, he is redirected to the home page
			response.sendRedirect("home.jsp");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}