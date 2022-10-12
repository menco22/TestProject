// Executed everitime the page is entirely loaded
window.onload = function ()
{	
	// IMPORTANT:
	// In javascript all files' functions can be accessed everywhere.
	// The important thing is to include all of the files inside the html page's head tag.
	
	// Sets a global variable, the cookie's name
	// that can be used from other javascript files
	// Sets it only if there ia a user parameter in the url and there are no hashes
	var hiddInput = document.getElementById("logUser");
	if(typeof(hiddInput) !== "undefined" && hiddInput !== null)
		window.cookieName = hiddInput.value + "-AsteOnline";
	else
	{
		alert("Spiacente, devi rieffettuare l'accesso per proseguire..");
		window.location.href = "Logout";
	}
	
	// window.location.href = url.substr(0, url.indexOf("?"));
	
	// Sets the event handlers of the quick-menu
	// The function is inside the event-handlers.js file
	setQuickMenuHandlers();

	// Checks if the cookie exists
	var cookieExist = checkCookie(window.cookieName);
	
	// If the cookie exists and the last action of the user was to create an auction
	// shows the 'vendo' page.
	if(cookieExist && getCookieValue(window.cookieName).lastAction === "createAuction")
	{
		// This function is located inside views-management.js
		showVendo();
	}
	else
	{
		// If it's the first access for the user
		if(!cookieExist)
		{
			// The last action set to firstAccess
			// will be used by the showAcquisto function.
			// CookieObj is a constructor and it's located inside objects.js
			var cookie = new CookieObj("firstAccess", [])
			
			// Creates the cookie
			setCookie(window.cookieName, cookie);			
		}
		
		// This function is located inside views-management.js
		showAcquisto();
	}
	// Updates the cookie's lastAction
	// This is done here because the last action is used inside
	// showAcquisto to check if this was the first user access.
	updateCookieLastAction(window.cookieName, "access");
}