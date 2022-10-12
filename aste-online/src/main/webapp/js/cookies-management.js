function getCookieValue(name)
{
  // Creates an array of cookies
  // All the cookies are separated by a semicolon here
  var cookies = document.cookie.split(';');

  // This is used to find the right cookie by it's name
  var pattern = name + '=';

  // Iterates over the array of cookies
  for (var i = 0; i < cookies.length; i++)
  {
    //Takes the current cookie
    var currCookie = cookies[i];

	var patternIndex = currCookie.indexOf(pattern);
    // Checks if the current cookie(string) starts with the given name
    if (patternIndex !== -1)
	{
      // Returns the object by parsing it to JSON
	  // The substring is extracted by the cookie using the index of the pattern
	  // (it's position inside the string) and by adding to it the length of
	  // the pattern itself
      return JSON.parse(decodeURIComponent(currCookie.substring(patternIndex + pattern.length, currCookie.length)));		
	}
  }
  return "";
}

// This function checks if the cookie exists
function checkCookie(name)
{
  // This is the ternary operator
  // !== checks both values and types
  // indexOf returns the position of the substring if exists, otherwise returns -1
  return document.cookie.indexOf(name + '=') !== -1 ? true : false;
}

// Updates the list of visited auctions and the lastAction
function addCookieVisitedAuction(name, aucId)
{
	if(checkCookie(name))
	{
		var cookie = getCookieValue(name);
		console.log(name);
		
		// Only the auction's IDs that are not present in the list are added
		if(cookie.visitedAuctions.indexOf(aucId) === -1)
		{
			// Adds the visited auction to the list
			// with unshift the id is added at the beginning of the array
			cookie.visitedAuctions.unshift(aucId);
		}
	}
	// If for some reason the cookie doesn't exist yet
	// a new one is created
	else
	{
		var cookie = {
		'lastAction' : "firstAccess",
		'visitedAuctions' : [aucId]
		};
	}
	// Creates or updates the cookie
	setCookie(name, cookie);
}

function updateCookieLastAction(name, lastAction)
{
	if(checkCookie(name))
	{
		var cookie = getCookieValue(name);
		// Updates the cookie's lastAction
		cookie.lastAction = lastAction;
	}
	// If for some reason the cookie doesn't exist yet
	// a new one is created
	else
	{
		var cookie = {
			'lastAction' : "firstAccess",
			'visitedAuctions' : []	
		};	
	}
	// Creates or updates the cookie
	setCookie(name, cookie);
}

// This function can be used both to create and to update a cookie
// Creates a new cookie when it doesn't exist or it is expired
function setCookie(name, object)
{
	// This creates a date object
	var expiryDate = new Date();
	
	// Adds one month to the current date
	// getMonth() returns values between [0-11]
	expiryDate.setMonth(expiryDate.getMonth() + 1);
	
	// -- Creates the cookie with the given name, object and the expiry date
	// -- Converts the expiryDate to a string formatted properly
	// -- The time set for the expiration date of  the cookie is related to the
	//    UTC timezone, so it will seem wrong, because it is not GMT+2
	//    but the conversion is correct and the cookie will expire after
	//    one exact month if not modified.
	// -- sameSite is necessary, if not specified, it is automatically set to 'None'
	//    but sameSite set to 'None' requires the cookie to be 'Secure'
	//    and it could be send only over https
	//    'Strict' means that the cookie is tied to the current domain "first-party"
	// -- The cookie's path is set to all the web application's paths.
	//    It means that the cookie can be accessed everywhere
	// -- Semicolons are used to split the cookie's fields
	// -- JSON.stringify converts the goven object to a string
	// -- The url encoding is necessary, otherwise tomcat will not
	//    be able to parse the cookie
	// Creates or updates the cookie
	document.cookie = name + '=' + encodeURIComponent(JSON.stringify(object)) + ';expires=' + expiryDate.toUTCString() + ';sameSite=Strict;path=/';
}