// This function returns the error received from the server as a string
// by extracting it from the server's response
function extractError(responseText)
{
	// Takes a substring of the response in between 2 indexes
	// then replaces the unwanted content in order to get only the message
	// Since the response is html encoded, it must be decoded first
	var htmlTmpP = document.createElement("p");
	htmlTmpP.innerHTML = responseText.substring(responseText.indexOf("<p><b>Message</b>"), 
		responseText.indexOf("<p><b>Description</b>"))
		.replace("<p><b>Message</b>", "").replace("</p>", "");
	// Sets the decoded string
	var strErr = htmlTmpP.innerText;
	// Returns the error message
	return strErr;
}

// This function is used in order to be able
// to send a form through a 'post' XMLHttpRequest
// It fetches all form's elements' names and values
// and creates a string
function formDataToString(form)
{
	var formStr = "";
	var elements = form.elements;
	// Iterates over all the form's elements
	for(var i in elements)
	{
		// The form.elements collection indexes the elements both
		// with strings representing numbers and other strings
		// parseInt is used to parse the numbers from the string
		// this is done to verify if the current string represents a number.
		// i'm interested only in these type of indexes
		// !isNaN checks if parseInt returns a number
		if(!isNaN(parseInt(i)))
		{
			formStr += elements[i].name + "=" + elements[i].value + "&";					
		}		
	}
	// removes the last '&' before returning the string
	return formStr.substring(0, formStr.lastIndexOf("&"));
}


function getFilteredAuctions(key)
{
	// Creates a new XMLHttpRequest object
	var request = new XMLHttpRequest();
	// Declares the anonymous function that must be called once a response
	// has been received from the server
	request.onreadystatechange = function() {
		if (request.readyState == XMLHttpRequest.DONE && request.status == 200){
			// Tries to parse the json data, if the response doesn't contain it,
			// it means that the controller on the servlet has sent a redirect
			// So the session is expired
			try{
				// The response is parsed to a json list
				var filteredAuctions = JSON.parse(request.responseText);
			}
			catch(e)
			{
				// Shows the login page, since the session is null
				document.write(request.responseText);
				return;
			}	

			// This is used to check the flow of execution
			var aucType = "filtered";
			// This is the text to write if the auctions list is empty
			// In this case nothing
			var hText = "";
			// This specifies which is the row to update
			var rowClass = "second-row";
			// Message to show if the auctions list is empty
			// This is a formatted string
			var msg = `Nessun' asta contiene la parola chiave: \"${key}\"`;
			// Creates a new aucList object used as parameter for the function below
			// in order to dynamically build and update the page.
			var aucListObj = new AucListObj(aucType, filteredAuctions, rowClass, msg, key);
			
			// Creates the auctions' list and updates the page
			showAuctionsList(aucListObj);
		} else if (request.readyState == XMLHttpRequest.DONE){
			console.log(request.responseText);
			// Extracts the error's description from the response
			var errMsg = extractError(request.responseText);
			// Shows the error message from the server
			showMessage(errMsg, true);
			/*document.write(request.responseText);*/
		}
	};
	
	// Sends the request with 1 parameter
	// True breaks the bubbling up and avoids the submit event to proceed
	request.open("GET", "GoToAcquisto?js=filtered&key=" + key);
	request.send();
}

function getVisitedAuctions()
{		
	// Creates a new XMLHttpRequest object
	var request = new XMLHttpRequest();
	// Declares the anonymous function that will be called once a response
	// will have been received from the server
	request.onreadystatechange = function() {
		if (request.readyState == XMLHttpRequest.DONE && request.status == 200){
			// Tries to parse the json data, if the response doesn't contain it,
			// it means that the controller on the servlet has sent a redirect
			// So the session is expired
			try{
				// The response is parsed to a json list
				var visitedAuctions = JSON.parse(request.responseText);
			}
			catch(e)
			{
				// Shows the login page, since the session is null
				document.write(request.responseText);
				return;
			}
					
			// This is used to check the flow of execution
			var aucType = "visited";
			// This is the text to write if the auctions list is empty
			var hText = "";
			// This is the message shown if the auctions list is empty
			var msg = "Non hai visitato nessun' asta per ora!";
			// This specifies which is the row to update
			var rowClass = "second-row";
			// Creates a new aucList object used as parameter for the function below
			// in order to dynamically build and update the page.
			// The key is not specified here because it's not necessary.
			var aucListObj = new AucListObj(aucType, visitedAuctions, rowClass, msg);
			
			// Creates the auctions' list and updates the page
			showAuctionsList(aucListObj);
		} else if (request.readyState == XMLHttpRequest.DONE){
			console.log(request.responseText);
			// Extracts the error's description from the response
			var errMsg = extractError(request.responseText);
			// Shows the error message from the server
			showMessage(errMsg, true);
			/*document.write(request.responseText);*/
		}
	};
	// Sends the request with 1 parameter
	request.open("GET", "GoToAcquisto?js=visited");
	request.send();
}

function getAwardedOffers()
{		
	// Creates a new XMLHttpRequest object
	var request = new XMLHttpRequest();
	// Declares the anonymous function that must be called once a response
	// has been received from the server
	request.onreadystatechange = function() {
		if (request.readyState == XMLHttpRequest.DONE && request.status == 200){			
			// Tries to parse the json data, if the response doesn't contain it,
			// it means that the controller on the servlet has sent a redirect
			// So the session is expired
			try{
				// The response is parsed to a json list
				var offers = JSON.parse(request.responseText);
			}
			catch(e)
			{
				// Shows the login page, since the session is null
				document.write(request.responseText);
				return;
			}
			
			// This is used to check the flow of execution
			var offrType = "awarded";
			// This is the message shown if the offers list is empty
			var msg = "Al momento non è stata aggiudicata nessun' asta all' utente.";
			// '\u20AC' represents the euro symbol.
			var thObj = {'tbl-name': 'Nome Articolo', 'tbl-code' : 'Codice', 'tbl-price' : 'Prezzo (\u20AC)'};
			// This specifies which is the row to update
			var rowClass = "third-row";
			// Creates a new offrList object used as parameter for the function below
			// in order to dynamically build and update the page.
			var offrListObj = new OffrListObj(offrType, offers, thObj, rowClass, msg);		
			
			showOffersList(offrListObj);
		} else if (request.readyState == XMLHttpRequest.DONE){
			console.log(request.responseText);
			// Extracts the error's description from the response
			var errMsg = extractError(request.responseText);
			// Shows the error message from the server
			showMessage(errMsg, true);
			/*document.write(request.responseText);*/
		}
	};
	// Sends the request with 1 parameter
	request.open("GET", "GoToAcquisto?js=awarded");
	request.send();
}


function getUserAuctions()
{
	// Creates a new XMLHttpRequest object
	var request = new XMLHttpRequest();
	// Declares the anonymous function that will be called once a response
	// will have been received from the server
	request.onreadystatechange = function() {
		if (request.readyState == XMLHttpRequest.DONE && request.status == 200){
			// Tries to parse the json data, if the response doesn't contain it,
			// it means that the controller on the servlet has sent a redirect
			// So the session is expired
			try{
				// The response is parsed to a json list
				var userAuctions = JSON.parse(request.responseText);
			}
			catch(e)
			{
				// Shows the login page, since the session is null
				document.write(request.responseText);
				return;
			}		

			// This is used to check the flow of execution
			var aucType = "open";
			// This specifies which is the row to update
			var rowClass = "first-row";
			// This is the message shown if the auctions list is empty			
			var msg = "Non hai nessun' asta aperta al momento!";
			// Creates a new aucList object used as parameter for the function below
			// in order to dynamically build and update the page.
			// The key is not specified here because it's not necessary.
			var aucListObj = new AucListObj(aucType, userAuctions[0], rowClass, msg);
			// Creates the user's open auctions' list and updates the page
			// userAuctions[2] contains the maximum offer received for each auction
			showAuctionsList(aucListObj, userAuctions[2]);
			
			// This is used to check the flow of execution
			var aucType = "closed";
			// This specifies which is the row to update
			var rowClass = "second-row";
			// This is the message shown if the auctions list is empty
			var msg = "Non hai ancora chiuso nessun' asta!";
			// Creates a new aucList object used as parameter for the function below
			// in order to dynamically build and update the page.
			// The key is not specified here because it's not necessary.
			var aucListObj = new AucListObj(aucType, userAuctions[1], rowClass, msg);
			// Creates the user's closed auctions' list and updates the page
			// userAuctions[2] contains the maximum offer received for each auction
			showAuctionsList(aucListObj, userAuctions[2]);
		} else if (request.readyState == XMLHttpRequest.DONE){
			console.log(request.responseText);
			// Extracts the error's description from the response
			var errMsg = extractError(request.responseText);
			// Shows the error message from the server
			showMessage(errMsg, true);
		}
	};
	// Sends the request with 1 parameter
	request.open("GET", "GoToVendo?js=setup");
	request.send();	
}


function createAuction(form)
{
	// Creates a new XMLHttpRequest object
	var request = new XMLHttpRequest();
	// Declares the anonymous function that must be called once a response
	// has been received from the server
	request.onreadystatechange = function() {
		if (request.readyState == XMLHttpRequest.DONE && request.status == 200){
			// Tries to parse the json data, if the response doesn't contain it,
			// it means that the controller on the servlet has sent a redirect
			// So the session is expired
			try{
				// The response is saved into a variable			
				var result = request.responseText;
			}
			catch(e)
			{
				// Shows the login page, since the session is null
				document.write(request.responseText);
				return;
			}
			
			// After submitting the auction, updates the
			// user's open and closed auctions' lists
			// but only if the auction has been created successfully
			if(result === "1")
			{
				getUserAuctions();
				// Updates the cookie's last action
				updateCookieLastAction(window.cookieName, "createAuction");
				// Since the has been creatred successfully, all the 
				// input fields can be cleared
				form.reset();		
			}
			else
			{
				var errMsg = "Errore, impossibile creare l'asta. Riprovare piu tardi.."
				showMessage(errMsg, true, form);
			}
		} else if (request.readyState == XMLHttpRequest.DONE){
			console.log(request.responseText);
			// Extracts the error's description from the response
			var errMsg = extractError(request.responseText);
			// Shows the error message from the server
			showMessage(errMsg, true);
		}
	};
	// The form data must be converted to a proper object
	// in order to be sent
	request.open("POST", "CreateAuction?js=create&auction-submit=create");
/*	// This is required to properly encode the strings
	request.setRequestHeader('Content-type', 'multipart/form-data; charset=ISO-8859-1', boundary="This is a boundary used to parse the date from the request.");*/
	var formData = new FormData(form);
	for(var val of formData.values())
	{
		console.log(val);
	}
	request.send(formData);
}


function getAuctionDetails(aucId, page)
{
	// Creates a new XMLHttpRequest object
	var request = new XMLHttpRequest();
	// Declares the anonymous function that will be called once a response
	// will have been received from the server
	request.onreadystatechange = function() {
		if (request.readyState == XMLHttpRequest.DONE && request.status == 200){			
			// Tries to parse the json data, if the response doesn't contain it,
			// it means that the controller on the servlet has sent a redirect
			// So the session is expired
			try{
				// The response is parsed to a json list
				var dtlsObj = JSON.parse(request.responseText);
			}
			catch(e)
			{
				// Shows the login page, since the session is null
				document.write(request.responseText);
				return;
			}
			// This specifies which is the row to update
			var rowClass = "first-row details";
				
			// Creates a new aucList object used as parameter for the function below
			// in order to dynamically build and update the page.
			// The key is not specified here because it's not necessary.
			var detailsObj = new DetailsObj(page, dtlsObj, rowClass);
			
			
			// The following function will perform some checks in order to 
			// dynamically create the pages with the proper elements.
			// (offerta and dettagli)
			setupAuctionDetailsPages(detailsObj);			

		} else if (request.readyState == XMLHttpRequest.DONE){
			console.log(request.responseText);
			// Extracts the error's description from the response
			var errMsg = extractError(request.responseText);
			// Shows the error message from the server
			showMessage(errMsg, true);
			/*document.write(request.responseText);*/
		}
	};
	// Sends the request with 1 parameter
	// page is specified by the caller function
	// it is used to distinguish between the offerta and dettagli pages
	request.open("GET", "GetAuctionDetails?auctionId=" + aucId + "&page=" + page + ".html&js=details");
	request.send();	
}

function getAuctionOffers(aucId, hId, page)
{
	// Creates a new XMLHttpRequest object
	var request = new XMLHttpRequest();
	// Declares the anonymous function that will be called once a response
	// will have been received from the server
	request.onreadystatechange = function() {
		if (request.readyState == XMLHttpRequest.DONE && request.status == 200){
			// Tries to parse the json data, if the response doesn't contain it,
			// it means that the controller on the servlet has sent a redirect
			// So the session is expired
			try{
				// The response is parsed to a json list
				var dtlsObj = JSON.parse(request.responseText);
			}
			catch(e)
			{
				// Shows the login page, since the session is null
				document.write(request.responseText);
				return;
			}
			
			// If the auction is expired and the page to display is offerta
			if(dtlsObj[0] && page === "offerta")
			{
				// This specifies which is the row to update
				var rowClass = "first-row details";
					
				// Creates a new aucList object used as parameter for the function below
				// in order to dynamically build and update the page.
				// The key is not specified here because it's not necessary.
				var detailsObj = new DetailsObj(page, dtlsObj, rowClass);
				
				// The following function will perform some checks in order to 
				// dynamically create the pages with the proper elements.
				// (offerta and dettagli)
				setupAuctionDetailsPages(detailsObj);	
			}
			// Ensures that the auction is not closed
			else if(!dtlsObj[3].isClosed)
			{
				// This is used to check the flow of execution
				var offrType = "details";
				// This is the message shown if the offers list is empty
				var msg = "Non esiste alcuna offerta al momento!";
				// '\u20AC' represents the euro symbol.
				var thObj = {'tbl-name': 'Nome', 'tbl-offer' : 'Offerta (\u20AC)', 'tbl-date' : 'Data'};
				// This specifies which is the row to update
				var rowClass = "second-row";
				// Creates a new offrList object used as parameter for the function below
				// in order to dynamically build and update the page.
				var offrListObj = new OffrListObj(offrType, dtlsObj, thObj, rowClass, msg);		
				// Updates and shows the offers' list
				showOffersList(offrListObj);
				// since the offers list has been updated
				// if the page to display is dettagli and the auction is expired
				// the auction's owner has the possibility to close the auction
				if(page === "dettagli" && dtlsObj[0])
				{
					// This function is inside views-management.js
					// dtlsObj[3].id is the current auction's id
					addCloseButton(dtlsObj[3].id);
				}
				else if(page === "offerta")
				{
					// dtlsObj[2] refers to the maxAuctionOffer
					// dtlsObj[3] refers to the current auction
					if(dtlsObj[2] !== null && typeof(dtlsObj[2].value) !== "undefined")
					{
						var minimumOffer = dtlsObj[2].value + dtlsObj[3].minUpsideOffer;						
					}
					else
					{
						var minimumOffer = dtlsObj[3].initialPrice;
					}
					// createNewOfferFormRow is inside viwes-management.js
					// it creates the form that can be used to make an offer for the current auction
					// dtlsObj[3].id is the current auction's id
					createNewOfferFormRow(hId, minimumOffer, dtlsObj[3].id);				
				}				
			}
					

		} else if (request.readyState == XMLHttpRequest.DONE){
			console.log(request.responseText);
			// Extracts the error's description from the response
			var errMsg = extractError(request.responseText);
			// Shows the error message from the server
			showMessage(errMsg, true);
			/*document.write(request.responseText);*/
		}
	};
	// Sends the request with 1 parameter
	// page is specified by the caller function
	// it is used to distinguish between the offerta and dettagli pages
	request.open("GET", "GetAuctionDetails?auctionId=" + aucId + "&page=" + page + ".html&js=offers");
	request.send();	
}

function makeOffer(form, aucId, hId)
{
	// Creates a new XMLHttpRequest object
	var request = new XMLHttpRequest();
	// Declares the anonymous function that must be called once a response
	// has been received from the server
	request.onreadystatechange = function() {
		if (request.readyState == XMLHttpRequest.DONE && request.status == 200){
			// Tries to parse the json data, if the response doesn't contain it,
			// it means that the controller on the servlet has sent a redirect
			// So the session is expired
			try{
				// The response is saved into a variable			
				var result = request.responseText;
			}
			catch(e)
			{
				// Shows the login page, since the session is null
				document.write(request.responseText);
				return;
			}
			
			// After submitting the auction, updates the
			// user's open and closed auctions' lists
			// but only if the auction has been created successfully
			if(result === "1")
			{
				// MakeOffer can be reached only by submitting an offer in the form
				// the form is visible only in the offerta page
				getAuctionOffers(aucId, hId, "offerta");
				// Updates the cookie's last action
				updateCookieLastAction(window.cookieName, "makeOffer");
				// Since the offer has been created successfully all the
				// input fields can be cleared
				form.reset();	
			}
			else
			{
				var errMsg = "Errore, impossibile salvare l'offerta. Riprovare piu tardi.."
				showMessage(errMsg, true, form);
			}
		} else if (request.readyState == XMLHttpRequest.DONE){
			console.log(request.responseText);
			// Extracts the error's description from the response
			var errMsg = extractError(request.responseText);
			// Shows the error message from the server
			showMessage(errMsg, true);
			/*document.write(request.responseText);*/
		}
	};
	// The form data must be converted to a proper string
	// in order to be sent
	request.open("GET", "MakeOffer?auctionId="+ aucId +"&js=make&" + formDataToString(form));
	request.send();	
}

function closeAuction(aucId)
{
	// Creates a new XMLHttpRequest object
	var request = new XMLHttpRequest();
	// Declares the anonymous function that must be called once a response
	// has been received from the server
	request.onreadystatechange = function() {
		if (request.readyState == XMLHttpRequest.DONE && request.status == 200){
			// Tries to parse the json data, if the response doesn't contain it,
			// it means that the controller on the servlet has sent a redirect
			// So the session is expired
			try{
				// The response is saved into a variable			
				var result = request.responseText;
			}
			catch(e)
			{
				// Shows the login page, since the session is null
				document.write(request.responseText);
				return;
			}
			
			// updates the content of the 'dettagli' page.
			// The page is surely "dettagli", because the close auction button
			// is shown only inside it and not inside 'offerta'.
			getAuctionDetails(aucId, "dettagli");
			// Updates the cookie's last action
			updateCookieLastAction(window.cookieName, "closeAuction");
						
		} else if (request.readyState == XMLHttpRequest.DONE){
			console.log(request.responseText);
			// Extracts the error's description from the response
			var errMsg = extractError(request.responseText);
			// Shows the error message from the server
			showMessage(errMsg, true);
			/*document.write(request.responseText);*/
		}
	};
	// The form data must be converted to a proper object
	// in order to be sent
	request.open("GET", "CloseAuction?js=close&auctionId=" + aucId);
	request.send();	
}