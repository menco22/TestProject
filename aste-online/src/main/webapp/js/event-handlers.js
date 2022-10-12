// This function creates the paragraph with the error message
// inside a div, as first child of the form
function showMessage(msg, isErr, form)
{
	// Creates and sets the style of the div 
	// that will contain the paragraph
	var msgDiv = document.createElement("div");

	// Checks if the error should be placed inside a form
	if(typeof(form) !== "undefined" && form != null)
	{
		var inForm = true;
		msgDiv.id = "formMsg";
		msgDiv.style.backgroundColor = "rgba(22, 22, 22, 0.7)";		
	}
	// The message comes from the server
	else
	{
		var inForm = false;
		msgDiv.id = "errMsg";
		msgDiv.style.backgroundColor = "rgba(22, 22, 22, 0.9)";		
		// Takes the midcolumn, the div will be appened to it, at the top
		// there is only 1 div of class mid-col
		var midCol = document.getElementsByClassName("mid-col")[0];
		
		// Makes the div visible
		msgDiv.className = "row";

		// Creates an heading and an horizzontal rule
		var errH2 = document.createElement("h2");
		errH2.innerText = "Errore:";
		
		// Appends the elements to the div
		msgDiv.appendChild(errH2);
	}
	
	msgDiv.style.width = "90%";
	msgDiv.style.margin = "auto";
	msgDiv.style.marginBottom = "15px";
	// Checks if it's an error message
	if(isErr === true)
		msgDiv.style.color = "#ffcccc";
	else
		msgDiv.style.color = "#B6B6B6";
	msgDiv.style.border ="1px solid #4d4d4d";
	msgDiv.style.borderRadius = "3px";
	
	// Creates and sets the style of the paragraph
	var msgP = document.createElement("p");
	msgP.style.textAlign = "left";
	msgP.style.padding = "10px";
	msgP.style.margin ="auto";
	
	// This is done only if the message should be whowed inside a form
	if(inForm)
	{
		// Adds the message to the paragraph
		msgP.innerText = "Errore, " + msg;
		// Appends the paragraph to the div
		msgDiv.appendChild(msgP);
		// Appends the div to the proper element
		form.appendChild(msgDiv);
		// The div is placed at the top as first
		// child of the form
		form.insertBefore(msgDiv, form.firstChild);		
	}
	else
	{
		// Adds the message to the paragraph
		msgP.innerText = msg;
		// Appends the paragraph to the div
		msgDiv.appendChild(msgP);
		
		// Appends msgDiv to the mid-column div
		midCol.appendChild(msgDiv);
		midCol.insertBefore(msgDiv, midCol.firstChild);
		
		// Jumps to the error
		window.location.hash = msgDiv.id;
	}
}

// Deletes the messages if they are visible
function removeMessages()
{
		// Forms related errors
		var formErrMsg = document.getElementById("formMsg");
		if(formErrMsg !== null)
			formErrMsg.remove();

		// Server related errors
		var errMsg = document.getElementById("errMsg");
		if(errMsg !== null)
			errMsg.remove();
	
}

function setQuickMenuHandlers()
{
	// This object contains the name of the pages along with their handlers functions
	var pages = {"home" : showHome, "acquisto" : showAcquisto, "vendo" : showVendo};
	// iterates over the object's items
	for(var key in pages)
	{
		// The pages' names are used to complete the id below and to dinamically add the event handlers
		var el = document.getElementById("qck-" + key);
		el.addEventListener("click", pages[key]);
		// This shows the tiny hand while hovering a link in the menu
		el.addEventListener("mouseover", function (event) {
			// This avoids the links reload the page
			event.preventDefault();
		});
	}
}


function setAuctionHandler(currA, aucId, type)
{
	// showOfferta and showDettagli are located inside the views-management.js file
	// An anonymous function is used as handler in order to pass
	// the parameter to the 2 functions
	if(type === "filtered" || type === "visited")
		currA.addEventListener("click", function (e) {
				e.preventDefault();
				// Shows the offerta page for the given auction
				//showOfferta(aucId);
				getAuctionDetails(aucId, "offerta");
			});
	else if(type === "open" || type === "closed")
		currA.addEventListener("click", function (e) {
				e.preventDefault();
				// Shows the dettagli page for the given auction
				//showDettagli(aucId);
				getAuctionDetails(aucId, "dettagli");
			});
	else
		console.log("Errore, parametro non valido: " + type);
}

function setKeyFormHandler(form, hId)
{
	// Creates the heandler anonymous function inline
	form.addEventListener("submit", (event) => {
		
		// This interrupts the normal behaviour of the form
		// So it won't reload the page
		event.preventDefault();
		
		// Creates and shows a row that will be updated after receiving the data from the server.
		// The row can be created here because if it's the first user's login
		// it may not exist yet.
		var midColumn = document.getElementById("midCol");	
		// Ensures the row doesn't exist.
		if(typeof(midColumn.getElementsByClassName("second-row")[0]) === "undefined")
		{
			var row = createVisitedAuctionsRow(hId);
			midColumn.appendChild(row);
			// Takes the firstRow of the midColumn
			var firstRow = midColumn.getElementsByClassName("first-row")[0];
			// Adds the paragraph at the top, below the form heading
			midColumn.insertBefore(row, firstRow.nextSibling);			
		}
		
		// Looks into the keyForm and takes the value of the keyInput
		var key = form["key"].value;
		
		// Before submitting the data, the key is checked
		// The checkKeyForm function is located inside form-validation.js
		var result = checkKeyForm(key);
		
		
		// Deletes the error messages if they are visible
		removeMessages();
		
		if(result)
		{
			// Since the values submitted by the user are correctly formatted
			// all input fields can be cleared
			form.reset();
			// Submits the form's data if the key is formatted correctly
			getFilteredAuctions(key)
		}
		else
		{
			// Shows the error inside the form
			// true tells that the message is an error
			var errMsg = "La parola chiave deve avere una lunghezza compresa tra i 3 e i 20 caratteri e può contenere solo lettere non accentate!";
			showMessage(errMsg, true, form);
		}
	});
}

function setAuctionFormHandler(form)
{
	// Creates the heandler anonymous function inline
	form.addEventListener("submit", (event) => {
		
		// This interrupts the normal behaviour of the form
		// So it won't reload the page
		event.preventDefault();
		
		// Before submitting the data, the user's inputs are checked
		// The checkNewAuctionForm function is located inside form-validation.js
		var result = checkNewAuctionForm(form);
		
		
		// Deletes the error messages if they are visible
		removeMessages();
		
		if(result === true)
		{
			// Submits the form's data if the inputs are formatted correctly
			createAuction(form);
		}
		else
		{
			// Shows the error inside the form
			// true tells that the message is an error
			showMessage(result, true, form);
		}
	});	
}


function setOfferFormHandler(form, minimumOffer, aucId, hId)
{
	// Creates the heandler anonymous function inline
	form.addEventListener("submit", (event) => {
		
		// This interrupts the normal behaviour of the form
		// So it won't reload the page
		event.preventDefault();
		
		// Looks into the form and takes the value of the user's input
		var offer = form["offer"].value;
		
		// Before submitting the data, the offer's value is checked
		// The checkOfferForm function is located inside form-validation.js
		var result = checkOfferForm(offer, minimumOffer);	
		
		// Deletes the error messages if they are visible
		removeMessages();
		
		if(result)
		{
			// The offer's input inside the form is not cleared here
			// because it is automatically update later
			// and it the offer value is going to be submitted by makeOffer
			// Submits the form's data if the offer's value meets the requirements
			makeOffer(form, aucId, hId);
		}
		else
		{
			// Shows the error inside the form
			// true tells that the message is an error
			var errMsg = "il valore specificato deve essere superiore all' offerta massima attuale di un ammontare"
        					+ " pari almeno al rialzo minimo!\nSe non ci sono offerte al momento, il valore minimo deve essere pari"
        					+ " o superiore al valore iniziale dell' asta! Il valore massimo è 2 miliardi!";
			showMessage(errMsg, true, form);
		}
	});	
}


function setCloseAuctionHandler(closeAucBtn, aucId)
{
	closeAucBtn.addEventListener("click", (e) => {
		e.preventDefault();
		// this function is inside data-management
		closeAuction(aucId);
	});
}