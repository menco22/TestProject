// This function clears the content of
// the middle column
function clearPage()
{
	var midColumn = document.getElementById("midCol");
	midColumn.className = "col mid-col";
	midColumn.innerHTML = "";
}


// This function sets all the quick navigation links
function setupQuickNav(quickLinks)
{
	//lftCol is the left column that contains the quick-navigation
	// There is only one column with this className
	var lftCol = document.getElementsByClassName("lft-col")[0];
	var nav = document.getElementById("quick-nav");
	
	// quickLinks contains all the links and list items' text
	// Checks if there are links to set
	if(Object.keys(quickLinks).length > 0)
	{
		if(nav == null)
		{
			// Creates the div that will contain the quick navigation
			var navDiv = document.createElement("div");
			navDiv.className = "quick-nav";
			
			var h3 = document.createElement("h3");
			h3.innerText = "Navigazione Rapida";
			
			// Creates the quick-navigation list
			nav = document.createElement("ul");
			nav.id = "quick-nav";
			
			// Appends the quick navigation list to it's parent div
			navDiv.appendChild(h3);
			navDiv.appendChild(nav);
			// Appends the div containing the quick-navigation to the column
			lftCol.appendChild(navDiv);
		}
		// Deletes the content of the list before adding new elements
		nav.innerHTML = "";
		
		// Iterates over the quickLinks object
		for(var key in quickLinks)
		{
			var currA = document.createElement("a");
			var currLi = document.createElement("li");
			currLi.innerText = quickLinks[key];
			currA.appendChild(currLi);
			// The "#" is required because i'm creating some inpage jumps
			// by using the quick navigation
			currA.href = "#" + key;
			nav.appendChild(currA);
		}
	}
	else
	{
		// Removes all the content of the left column
		lftCol.innerHTML = "";
	}

}

// This function sets all the links required to import the
// proper css files inside each page
function setupCssLinks(cssLinks)
{
	// This returns all the links related to the stylesheet, currently located inside the head tag
	var currCssLinks = document.querySelectorAll('link[rel="stylesheet"]');
	
	// Deletes the current links before adding the new ones
	for(var i = 0; i < currCssLinks.length; i++)
	{
		document.head.removeChild(currCssLinks[i]);
		
	}
	
	// Iterates over the cssLinks
	for(var el in cssLinks)
	{
		// creates all the links used to import the css files
		var link = document.createElement("link");
		link.rel = "stylesheet";
		link.type = "text/css";
		link.href = cssLinks[el];
		// This is used to add the css links inside the head of the html file
		document.head.appendChild(link);
	}
}


// This function calculates the remaining time between the current date and time and
// the deadline of each auction
function getRemainingTime(deadline)
{
	if(typeof(deadline.date) !== "undefined")
	{
		// gson returns the LocalDateTime as date and time
		// -1 is necessary since Date represents month with indices from 0 to 11
		var deadlineDate = new Date(deadline.date.year, deadline.date.month - 1, deadline.date.day, deadline.time.hour, deadline.time.minute);
	}
	else
	{
		// Case in which the user has submitted the form to create
		// a new auction and the deadline is being checked
		// to see if it's valid
		var deadlineDate = new Date(deadline);
	}

  	// Gets the current date and time
  	var currDate = new Date();

	// Since the auctions' deadlines are truncated to the minutes
	// but dates are not, the currentDate should be truncated too
	currDate.setSeconds(0);
	currDate.setMilliseconds(0);

  	// Finds the milliseconds between the current date and the deadline
  	var millisecondsDiff = deadlineDate - currDate;

	// If the auction is expired, these are the returned values
	var days = 0, hours = 0, minutes = 0;
	
	// Checks if the auction is expired
	if(millisecondsDiff > 0)
	{		
		var millisecondsInADay = 1000 * 60 * 60 * 24;
		var millisecondsInAnHour = 1000 * 60 * 60;
		var millisecondsInAMinute = 1000 * 60;
		// Calculates the remaining days, hours and minutes
	  	days = Math.floor(millisecondsDiff / millisecondsInADay);
	  	hours = Math.floor((millisecondsDiff % millisecondsInADay) / millisecondsInAnHour);
	  	minutes = Math.floor((millisecondsDiff % millisecondsInAnHour) / millisecondsInAMinute);
	}
			
	// Returns a diffDAte object in order to set the remaining time for each auction
	return new DiffDAte(days, hours, minutes);
}

// Formats the number passed as parameter as currency
function formatNumber(num)
{
	return Intl.NumberFormat('it-IT', {style: 'decimal',
				minimumFractionDigits: 0}).format(num);
}

function createFilterAuctionsRow(quickLinks)
{
	// Creation of the row that will contain the form
	// used to filter the auctions by keyword.
	var row = document.createElement("div");
	// This function is called only by showAcquisto
	// so i know that it will be the page's first row
	row.className = "row first-row form";
	
	// Creates the form's heading
	var heading = document.createElement("h2");
	heading.innerText = "Ricerca tramite parola chiave";
	heading.id = Object.keys(quickLinks)[0];
	
	// Creates the form, but it has no action nor method
	// It is managed by a javascript event handler function
	var form = document.createElement("form");
	form.name = "keyForm";
	form.method="post";
	
	// Sets the handler function for the form.
	// The handler function will also validate the input
	// quickLinks[1], contain's the id for the
	// visited or filtered auctions list.
	setKeyFormHandler(form, Object.keys(quickLinks)[1]);
	
	// Creates the div that contains the input for the key
	var div = document.createElement("div");
	var keyLabel = document.createElement("label");
	keyLabel.innerText = "Chiave:";
	keyLabel.for = "key";
	var keyInput = document.createElement("input");
	keyInput.id = "key";
	keyInput.type = "text";
	keyInput.name ="key";
	keyInput.required = "true";
	
	// Appends keyLabel and keyInput to the div element
	div.appendChild(keyLabel);
	div.appendChild(keyInput);
	
	// Creates the button to submit the form data
	var submit = document.createElement("button");
	submit.name = "keyword-submit";
	submit.id = submit.name;
	submit.type = "submit";
	submit.innerText = "Cerca";
	
	// Appends the div which contains the keyInput and the submit button to the form
	form.appendChild(div);
	form.appendChild(submit);
	
	// Appends the h2 heading and the form to the row
	row.appendChild(heading);
	row.appendChild(form);
	
	// Returns the row
	return row;
}

// This function adds 1 zero in front of the month or day's values
// if they are less than 10, in order to properly format
// the datetime
function addLeadingZeros(value)
{
	if(value < 10)
		return "0" + value;
	return value;
}

function createNewAuctionRow(hId)
{
	// Creation of the row that will contain the form
	// used to filter the auctions by keyword.
	var row = document.createElement("div");
	// This function is called only by showAcquisto
	// so i know that it will be the page's first row
	row.className = "row third-row form";
	
	// Creates the form's heading
	var heading = document.createElement("h2");
	heading.innerText = "Creazione di una nuova asta";
	heading.id = hId;
	
	// Appends the heading to the row
	row.appendChild(heading);
	
	// Creates the form, but it has no action nor method
	// It is managed by a javascript event handler function
	var form = document.createElement("form");
	form.name = "aucForm";
	form.method="post";
	// Sets the encoding of the form's data in order
	// to properly submit and handle the image file
	form.enctype="multipart/form-data"

	// Sets the handler function for the form.
	// The handler function will also validate the input
	setAuctionFormHandler(form);
	
	// Creates the div that contains the input
	// for the article's name.
	var idName = "name";
	var div = document.createElement("div");
	var label = document.createElement("label");
	label.innerText = "Nome Articolo:";
	label.for = idName;
	var input = document.createElement("input");
	input.type = "text";
	input.id = idName;
	input.name = idName;
	input.min = "4";
	input.max = "50";
	input.required = "true";
	// Appends the label and the input to the div
	div.appendChild(label);
	div.appendChild(input);
	// Appends the div to the form
	form.appendChild(div);
	
	// Creates the div that contains the textarea
	// for the article's description.
	var idName = "description";
	var div = document.createElement("div");
	var label = document.createElement("label");
	label.innerText = "Descrizione:";
	label.for = idName;
	var textarea = document.createElement("textarea");
	textarea.type = "text";
	textarea.id = idName;
	textarea.name = idName;
	textarea.rows = "4";
	textarea.cols = "50";
	textarea.minlength = "20";
	textarea.maxLength = "3000";
	textarea.placeholder = " ...";
	textarea.spellcheck = false;
	textarea.required = "true";
	// Appends the label and the textarea to the div
	div.appendChild(label);
	div.appendChild(textarea);
	// Appends the div to the form
	form.appendChild(div);
	
	// Creates the div that contains the input
	// for the image.
	var idName = "image";
	var div = document.createElement("div");
	var label = document.createElement("label");
	label.innerText = "Immagine:";
	label.for = idName;
	var input = document.createElement("input");
	input.type = "file";
	input.id = idName;
	input.name = idName;
	input.accept = "image/*";
	input.required = "true";
	// Appends the label and the input to the div
	div.appendChild(label);
	div.appendChild(input);
	// Appends the div to the form
	form.appendChild(div);
	
	// Creates the div that contains the input
	// for the initial price of the article.
	var idName = "initialPrice";
	var div = document.createElement("div");
	var label = document.createElement("label");
	label.innerText = "Prezzo Iniziale:";
	label.for = idName;
	var input = document.createElement("input");
	input.type = "number";
	input.id = idName;
	input.name = idName;
	input.min = "1";
	input.step = "1";
	input.max = "700000000";
	input.required = "true";
	var span = document.createElement("span");
	// The text contains the euro symbol
	span.innerText = " \u20AC";
	// Appends the label and the input to the div
	div.appendChild(label);
	div.appendChild(input);
	div.appendChild(span);
	// Appends the div to the form
	form.appendChild(div);
	
	// Creates the div that contains the input
	// for the minimum upside offer for the auction.
	var idName = "minUpsideOffer";
	var div = document.createElement("div");
	var label = document.createElement("label");
	label.innerText = "Rialzo Minimo:";
	label.for = idName;
	var input = document.createElement("input");
	input.type = "number";
	input.id = idName;
	input.name = idName;
	input.min = "50";
	input.step = "1";
	input.max = "100000";
	input.required = "true";
	var span = document.createElement("span");
	// The text contains the euro symbol
	span.innerText = " \u20AC"
	// Appends the label and the input to the div
	div.appendChild(label);
	div.appendChild(input);
	div.appendChild(span);
	// Appends the div to the form
	form.appendChild(div);
	
	// Creates the div that contains the input
	// for the local datetime.
	var idName = "deadline";
	var date = new Date();
	var month = date.getUTCMonth();
	var day = date.getUTCDay();
	// This function adds 1 zero in front of the month or day's values
	// if they are less than 10, in order to properly format
	// the datetime
	// Increasing the month's value by 1 allows to represent
	// the correct value, visually.
	// Months are stored with indices from 0 to 11
	var month = addLeadingZeros(date.getMonth() + 1);
	var day = addLeadingZeros(date.getDate());
	var hours = addLeadingZeros(date.getHours());
	var minutes = addLeadingZeros(date.getMinutes());
	// Creates a fomatted string that represents the local datetime
	var ldt = date.getFullYear() + "-" + month + "-" +
		day + "T" + hours + ":" + minutes;

	var div = document.createElement("div");
	var label = document.createElement("label");
	label.innerText = "Scadenza (futura): ";
	label.for = idName;
	var input = document.createElement("input");
	input.type = "datetime-local";
	input.id = idName;
	input.name = idName;
	input.min = ldt;
	input.placeholder = "yyyy-MM-ddTHH:mm";
	input.value = ldt;
	input.required = "true";
	// Appends the label and the input to the div
	div.appendChild(label);
	div.appendChild(input);
	// Appends the div to the form
	form.appendChild(div);
	
	// Creates the button that will allow the user
	// to submit the data to the server
	var button = document.createElement("button");
	button.name = "auction-submit";
	button.type = "submit";
	button.innerText = "Crea Asta";
	// Appends the div to the form
	form.appendChild(button);
	
	// Appends the form to the row
	row.appendChild(form);
	
	// returns the row
	return row;
}

function createNewOfferFormRow(hId, minimumOffer, aucId)
{
	var midCol = document.getElementById("midCol");
	
	
	// Creation of the row that will contain the form
	// used to filter the auctions by keyword.
	var row = document.createElement("div");
	// This function is called only by showAcquisto
	// so i know that it will be the page's first row
	row.className = "row third-row form";
	
	// Creates the form's heading
	var heading = document.createElement("h2");
	heading.innerText = "Nuova offerta";
	heading.id = hId;
	
	// Appends the heading to the row
	row.appendChild(heading);
	
	// Creates the form, but it has no action nor method
	// It is managed by a javascript event handler function
	var form = document.createElement("form");
	form.name = "offrForm";
	form.method="post";

	// Sets the handler function for the form.
	// The handler function will also validate the input
	setOfferFormHandler(form, minimumOffer, aucId, hId);
	
	// Creates the div that contains the input for the key
	var div = document.createElement("div");
	var offrLabel = document.createElement("label");
	offrLabel.innerText = "Valore:";
	offrLabel.for = "offer";
	var offrInput = document.createElement("input");
	offrInput.id = "offer";
	offrInput.type = "number";
	offrInput.name ="offer";
	offrInput.value = minimumOffer;
	offrInput.min = minimumOffer;
	offrInput.max = "2000000000";
	offrInput.required = "true";
	// this is used to add the euro symbol on the right side of the input field
	var span = document.createElement("span");
	// The text contains the euro symbol
	span.innerText = " \u20AC";
	// Appends keyLabel and keyInput to the div element
	div.appendChild(offrLabel);
	div.appendChild(offrInput);
	div.appendChild(span);
	
	// Creates the button to submit the form data
	var submit = document.createElement("button");
	submit.name = "offer-submit";
	submit.id = submit.name;
	submit.type = "submit";
	submit.innerText = "Conferma";
	
	// Appends the div which contains the keyInput and the submit button to the form
	form.appendChild(div);
	form.appendChild(submit);
	
	// Appends the h2 heading and the form to the row
	row.appendChild(heading);
	row.appendChild(form);
	
	var thirdRow = midCol.getElementsByClassName("third-row")[0];
	// Removes the third row of the middle column if it exists already
	if(thirdRow !== null && typeof(thirdRow) !== "undefined")
		thirdRow.remove();
	
	// Appends the newly created form row to the middle column
	midCol.appendChild(row);	
}

function createRow(RowObj)
{
	// Creates the row that will contain the auctions' list
	var row = document.createElement("div");
	// rowClass is specified by the caller function
	row.className = "row " + RowObj.rowClass;
	
	// Creates the section that will contain all the offers
	// sectionClass is specified by the caller function
	var section = document.createElement("section");
	section.className = RowObj.sectionClass;
	
	// This is the heading that will contain the section title
	var heading = document.createElement("h2");
	heading.id = RowObj.hId;

	// hText is specified by the caller function
	heading.innerText = RowObj.hText;
	
	// Creates a paragraph that will be replaced when there
	// will be auctions to show
	var p = document.createElement("p");
	p.innerText = RowObj.msg;
	
	// Appends the heading and the paragraph to the row
	row.appendChild(heading);
	section.appendChild(p);
	
	// The section is appended to the row
	row.append(section);
	
	// Returns the row
	return row;
}

// this couldn't be put directly inside showAcquisto, because
// if the users logs in for the first time, the visited auctions row
// doesn't exist.
// this function is called if it's not the first user's login
// or if it's the first user's login and the user has just submitted
// a keyword in order to filter the available auctions.
function createVisitedAuctionsRow()
{
	// Sets the appropriate quick-navigation's links
	var quickLinks = {'filter-auc' : 'Ricerca Asta', 'available-auc' : 'Aste Visitate', 'awarded-art' : 'Aste Aggiudicate'}
	setupQuickNav(quickLinks);

	// Used to distinguish between the different types of auctions or offers.
	var type = "auctions";
	// This is the heading text.
	// It will be changed when the data will be obtained
	// from the server.
	var hText = "Aste visitate";
	// Object.keys returns a collection containing
	// the keys of quickLinks.
	// The quickLinks keys are the headings' IDs
	var hId = Object.keys(quickLinks)[1];
	var rowClass = "second-row auc-list";
	var sectionClass = "scrollable";
	// Creates an object to use as parameter
	// for the createRow function.
	// In order to dinamically build the page.
	// Useful because that function is used for other pages too.
	var rowObj = new RowObj(type, hText, hId, rowClass, sectionClass);

	// Creates the second row of the middle column and
	// adds it to the array of rows.
	// It will contain the visited or the filtered auctions
	// that are open and not expired.
	// The list will be updated once the data will be returned by
	// the server later.
	return createRow(rowObj);
}


// Creates the auctions' list and updates the page
// This is called after receiving data from the server.
function showAuctionsList(aucListObj, maxOffers)
{	
	// maxOffers is used only by the 'vendo' page
	// -- The index of 0 is safe to use, since there is only one row
	//    with the specified class inside each page.
	// -- Possible row's classes: first-row, second-row, third-row.
	var row = document.getElementsByClassName(aucListObj.rowClass)[0];
	// There is no row that contains more than one section.
	var section = row.getElementsByTagName("section")[0];
	
	// aucObj contains the list of auctions along with their articles.
	var aucObj = aucListObj.aucObj;
	// Checks if the aucObj object is empty; if the server has
	// returned some data or not.
	if(aucObj.length > 0)
	{
		// Removes the paragraph used when there are no auctions to show.
		section.innerHTML = "";
		
		// Removes the paragraph used when there are no auctions that contains
		// the key submitted by the user.
		if(aucListObj.aucType === "filtered")
		{
			
			// Sets the appropriate quick-navigation's links
			var quickLinks = {'filter-auc' : 'Ricerca Asta', 'available-auc' : 'Aste Disponibili', 'awarded-art' : 'Aste Aggiudicate'}
			setupQuickNav(quickLinks);
			
			// Changes the text of the heading to the proper value
			var heading = document.getElementById("available-auc");
			heading.innerText = "Aste disponibili per: \"" + aucListObj.key + "\"";
				
			// There is only one row with class first-row
			var firstRow = document.getElementsByClassName("first-row")[0];
			// Get the paragraph contained inside the first row.
			// There can be only one paragraph.
			var p = firstRow.getElementsByTagName("p")[0];
			// If there is a paragraph, it's removed
			// Since there are auctions to show this time.
			if(typeof(p) !== "undefined" && p !== null)
			{
				p.remove();
			}			
		}
		// aucListObj.aucType is "visited"
		else if(aucListObj.aucType === "visited")
		{
			// Sets the appropriate quick-navigation's links
			var quickLinks = {'filter-auc' : 'Ricerca Asta', 'available-auc' : 'Aste Visitate', 'awarded-art' : 'Aste Aggiudicate'}
			setupQuickNav(quickLinks);
			
			// Changes the text of the heading to the proper value
			var heading = document.getElementById("available-auc");
			heading.innerText = "Aste Visitate";
		}


		// Iterates over the aucObj
		for(var i in aucObj)
		{
			// Creates the link for each auction		
			var currA = document.createElement("a");
			// Event handler added below
			currA.href="";
			
			// Creates the articles that contain each action
			var currArticle = document.createElement("article");
			// Assigns the class by checking if the current iteration is even or odd
			currArticle.className = i % 2 == 0 ? "even" : "odd";
			
			var currH3 = document.createElement("h3");
			// Retrieves the article's name, the auction's id is the key
			currH3.innerText = aucObj[i][1].name;
			
			var currH4 = document.createElement("h4");
			// Retrieves the article's name; the auction's id is the key
			currH4.innerText = "Codice: " + aucObj[i][1].code;

			// Appends somne elements to the article
			currArticle.appendChild(currH3);
			currArticle.appendChild(currH4);
			
			// Checks the dlow of execution
			// If the list will be shown inside the vondo page
			// the maximum offer value for each page will be shown.
			if(aucListObj.aucType === "open" || aucListObj.aucType === "closed")
			{
				// Checks if there is a maximum offer for the current auction
				// The id of the auction is the key for the maximum offer
				if(typeof(maxOffers[aucObj[i][0].id]) !== "undefined")
				{
					// Intl.NumberFormat is used to properly format the offer's value
					var str = formatNumber(maxOffers[aucObj[i][0].id].value) + 
				" \u20AC";
				}
				else
				{
					var str = "Nessuna";
				}
				
				var currP1 = document.createElement("p");
				currP1.innerText = "Offerta massima: " + str;
				// Appends the paragraph to the article
				currArticle.appendChild(currP1);
			}
				
			// This is shown everytime except for the closed auctions list
			if(aucListObj.aucType !== "closed")
			{
				var currP = document.createElement("p");
				// Checks the difference between the currenty auction's deadline and the current date and time
				var currRemainingTime = getRemainingTime(aucObj[i][0].deadline);
				// This is a formatted string
				currP.innerText = `Tempo rimanente: ${currRemainingTime.days} giorni, ${currRemainingTime.hours} ore e ${currRemainingTime.minutes} minuti.`;
				// Appends the paragraph to the article
				currArticle.appendChild(currP);
			}
			
			// Appends the article to the link, as a child
			currA.appendChild(currArticle);
			
			// Adds the event handler for each auction's link
			// The aucType variable is used to distinguish between the acquisto and vendo pages
			// in order to set the proper handler for each auction
			setAuctionHandler(currA, aucObj[i][0].id, aucListObj.aucType);
			// Adds the current auction to the section		
			section.appendChild(currA);
		}
	}
	// The auctions list is empty
	else
	{
		// Checks the flow of execution
		// This is the only case in which the message is set inside another row.
		if(aucListObj.aucType === "filtered")
		{
			var rowClass = "first-row";
		}
		else
		{
			// The rowClass has been specified by the caller function
			var rowClass = aucListObj.rowClass;
		}

		// There is only 1 row for each rowClass
		var firstRow = document.getElementsByClassName(rowClass)[0];
		var p = firstRow.getElementsByTagName("p")[0];
		
		// If there is no paragraph
		if(typeof(p) === "undefined" || p === null)
		{
			// Creates and sets the text for the paragraph
			p = document.createElement("p");
			p.innerText = aucListObj.msg;
			firstRow.appendChild(p);
			// Takes the h2 element of the firstRow
			var h2 = firstRow.getElementsByTagName("h2")[0];
			// Adds the paragraph at the top, below the form heading
			firstRow.insertBefore(p, h2.nextSibling);
			
		}
		// The paragraph exists and must be updated
		else
		{
			// There can be only 1 paragraph inside the firstRow
			// Updates the paragraph
			p.innerText = aucListObj.msg;
		}
	}
}


function showOffersList(offrListObj)
{
	// -- The index of 0 is safe to use, since there is only one row
	//    with the specified class inside each page.
	// -- Possible row classes: first-row, second-row, third-row.
	var row = document.getElementsByClassName(offrListObj.rowClass)[0];
	// There is no row that contains more than one section.
	var section = row.getElementsByTagName("section")[0];

	// thObj contains all table headers classes and texts
	var thObj = offrListObj.thObj;
	// offrObj contains the list of offers.
	var offrObj = offrListObj.offrObj;

	// This is done to correctly iterate on the different
	// offers' lists
	if(offrListObj.offrType === "details")
		tableObj = offrObj[1];
	else
		tableObj = offrObj;

	// Checks if the offrObj object is empty; if the server has
	// returned some data or not.
	if(tableObj.length > 0)
	{
		// Removes the paragraph used when there are no offers to show.
		section.innerHTML = "";
		
		var table = document.createElement("table");
		var thead = document.createElement("thead");
		var tbody = document.createElement("tbody");
		var theadTr = document.createElement("tr");
		
		// Iterates over thObj to dynamically create the table headers
		for(var key in thObj)
		{
			var currTh = document.createElement("th");
			// Assigns the classes and the table's headers
			// The classes are the keys of thObj
			currTh.className = key;
			currTh.innerText = thObj[key];
			// Appends the th element to the thead's row
			theadTr.appendChild(currTh);			
		}
		
		// Appends the thead's row to the thead
		thead.appendChild(theadTr);
		// Appendsa the thead to the table
		table.appendChild(thead);
		
		// Iterates over offrObj to dynamically create
		// the tbody's rows
		for(var i in tableObj)
		{
			// Creates all the rows along with the data
			// returned by the server
			var currTr = document.createElement("tr");
			// Ternary operator to set the proper class
			currTr.className = i % 2 == 0 ? "even" : "odd";
			
			var currTd1 = document.createElement("td");
			var currTd2 = document.createElement("td");
			var currTd3 = document.createElement("td");
			
			// offrType can be 'awarded' or 'details'
			if(offrListObj.offrType === "details")
			{
				// Sets the user's name
				currTd1.innerText = tableObj[i][0].user;
				// Sets the offer's value
				// Intl.NumberFormat is used to properly format the offer's value
				currTd2.innerText = formatNumber(tableObj[i][0].value);
				// Sets the date of the offer
				currTd3.innerText = tableObj[i][1];
			}
			else if(offrListObj.offrType === "awarded")
			{
				// Sets the current article's name
				currTd1.innerText = tableObj[i][0].name;
				// Sets the article's value
				currTd2.innerText = tableObj[i][0].code;
				// Sets the offer's value
				// Intl.NumberFormat is used to properly format the offer's value
				currTd3.innerText = formatNumber(tableObj[i][1].value);		
			}
			
			// Appends the current TDs to the current table's row
			currTr.appendChild(currTd1);
			currTr.appendChild(currTd2);
			currTr.appendChild(currTd3);
			
			// Appends currTr to the table's body
			tbody.appendChild(currTr);
		}
		
		// Appends the tbody element to the table
		table.appendChild(tbody);
		// Appends the table to the section
		section.appendChild(table);
	}
	// The offers list is empty
	else
	{
		// The rowClass has been specified by the caller function
		// There is only 1 row for each rowClass
		var firstRow = document.getElementsByClassName(offrListObj.rowClass)[0];
		var p = firstRow.getElementsByTagName("p")[0];
		
		// If there is no paragraph
		if(typeof(p) === "undefined" || p === null)
		{
			// Creates and sets the text for the paragraph
			p = document.createElement("p");
			p.innerText = offrListObj.msg;
			firstRow.appendChild(p);
			// Takes the h2 element of the firstRow
			var h2 = firstRow.getElementsByTagName("h2")[0];
			// Adds the paragraph at the top, below the form heading
			firstRow.insertBefore(p, h2.nextSibling);
			
		}
		// The paragraph exists and must be updated
		else
		{
			// There can be only 1 paragraph inside the firstRow
			// Updates the paragraph
			p.innerText = offrListObj.msg;
		}
	}	
}


function addCloseButton(aucId)
{
	// There can be only 1 second-row div
	var row = document.getElementsByClassName("second-row")[0];
	
	var closeAucBtn = document.createElement("button");
	closeAucBtn.innerText = "Chiudi asta";
	closeAucBtn.id = "clsAucBtn";
	
	setCloseAuctionHandler(closeAucBtn, aucId);
	
	var oldBtn = document.getElementById("clsAucBtn");
	// Check if the button is already present
	// Removes it to ensure that the handler and the aucId are correct
	if(document.getElementById("clsAucBtn") !== null)
		oldBtn.remove();

	row.appendChild(closeAucBtn);
}


function showAcquisto()
{
	document.title = "Acquisto";
	
	// Sets the links inside the head tag to import the css files.
	var cssLinks = ["css/main.css", "css/auctions-offers.css", "css/acquisto.css"];
	setupCssLinks(cssLinks);
	
	// Sets the quick-navigation's links
	var quickLinks = {'filter-auc' : 'Ricerca Asta', 'awarded-art' : 'Aste Aggiudicate'}
	setupQuickNav(quickLinks);
	
	// Clears the content of the middle column
	clearPage();
	
	// This is the list that will contain all the rows.
	// It will be used to dinamically add the rows to the middle column.
	var rows = [];

	// -- Creates the first row of the middle column and
	//    adds it to the array of rows.
	// -- It contains the form to filter the auctions
	// -- Object.keys returns a collection containing
	//    the keys of quickLinks.
	//    The quickLinks keys are the headings' IDs
	rows.push(createFilterAuctionsRow(quickLinks));

	// Checks if it's the first access for the user.
	// Creates the secondRow only if he has logged in the past.
	if(checkCookie(window.cookieName) && getCookieValue(window.cookieName).lastAction !== "firstAccess")
	{
		// Creates the second row of the middle column and
		// adds it to the array of rows.
		// It will contain the visited or the filtered auctions
		// that are open and not expired.
		// The list will be updated once the data will be returned by
		// the server later.
		// The quickLinks keys are the headings' IDs
		rows.push(createVisitedAuctionsRow());
	}

	// Sets all the attributes that will be used to create the offers' list row.
	var type = "offers";
	var hText = "Aste aggiudicate";
	// Object.keys returns a collection containing
	// the keys of quickLinks.
	// The quickLinks keys are the headings' IDs
	var hId = Object.keys(quickLinks)[1];
	var rowClass = "third-row offers-list";
	var sectionClass = "scrollable scrll-table";
	// Creates an object to use as parameter
	// for the createRow function.
	// In order to dinamically build the page.
	// Useful because that function is used for other pages too.
	var offrRowObj = new RowObj(type, hText, hId, rowClass, sectionClass)

	// Creates the third row of the middle column and
	// adds it to the array of rows.
	// It will contain the awarded offers list.
	// The list will be updated once the data will be returned by
	// the server later.
	rows.push(createRow(offrRowObj));

	// This starts building up the page as it is in the pure html version
	// There is only one elemenmt with this class name
	var midColumn = document.getElementById("midCol");
	
	// Iterates over all rows and adds them to the middle column.
	for(var i in rows)
	{
		midColumn.appendChild(rows[i]);
	}
	
	// Now that the page has been completely created
	// requests the data to the server and updates it.
	// Both the functions used to get data below
	// automatically updates the page with the content received
	// by the server.
	
	// Checks if it's the first access for the user.
	if(checkCookie(window.cookieName) && getCookieValue(window.cookieName).lastAction !== "firstAccess")
	{
		// Requests the visited auctions to the server and updates the auctions' list
		getVisitedAuctions();
	}
	
	// Requests the awarded offers to the server and updates the offers' table/list
	getAwardedOffers();
}

function showVendo()
{
	document.title = "Vendita";
	
	// Sets the links inside the head tag to import the css files.
	var cssLinks = ["css/main.css", "css/auctions-offers.css", "css/vendo.css"];
	setupCssLinks(cssLinks);
	
	// Sets the quick-navigation's links
	var quickLinks = {'open-auc' : 'Aste Aperte', 'closed-auc' : 'Aste Chiuse', 'create-auc' : 'Crea Asta'}
	setupQuickNav(quickLinks);
	
	// Clears the content of the middle column
	clearPage();
	
	// This is the list that will contain all the rows.
	// It will be used to dinamically add the rows to the middle column.
	var rows = [];

	// Used to distinguish between the different types of auctions or offers.
	var type = "auctions";
	// This is the heading text.
	// It will be changed when the data will be obtained
	// from the server.
	var hText = "Aste Aperte";
	// Object.keys returns a collection containing
	// the keys of quickLinks.
	// The quickLinks keys are the headings' IDs
	var hId = Object.keys(quickLinks)[0];
	var rowClass = "first-row auc-list";
	var sectionClass = "scrollable";
	// Creates an object to use as parameter
	// for the createRow function.
	// In order to dinamically build the page.
	// Useful because that function is used for other pages too.
	var rowObj = new RowObj(type, hText, hId, rowClass, sectionClass);

	// Creates the first row of the middle column and
	// adds it to the array of rows.
	// It will contain the user's open auctions;
	// the auctions that are open and not expired.
	// The list will be updated once the data will be returned by
	// the server later.
	rows.push(createRow(rowObj));

	
	// Used to distinguish between the different types of auctions or offers.
	var type = "offers";
	// This is the heading text.
	// It will be changed when the data will be obtained
	// from the server.
	var hText = "Aste Chiuse";
	// Object.keys returns a collection containing
	// the keys of quickLinks.
	// The quickLinks keys are the headings' IDs
	var hId = Object.keys(quickLinks)[1];
	var rowClass = "second-row auc-list";
	var sectionClass = "scrollable";
	// Creates an object to use as parameter
	// for the createRow function.
	// In order to dinamically build the page.
	// Useful because that function is used for other pages too.
	var rowObj = new RowObj(type, hText, hId, rowClass, sectionClass);

	// Creates the second row of the middle column and
	// adds it to the array of rows.
	// It will contain the user's closed auctions;
	// The list will be updated once the data will be returned by
	// the server later.
	rows.push(createRow(rowObj));

	
	// -- Creates the third row of the middle column and
	//    adds it to the array of rows.
	// -- It contains the form to create a new auction
	// -- Object.keys returns a collection containing
	//    the keys of quickLinks.
	//    The quickLinks keys are the headings' IDs
	rows.push(createNewAuctionRow(Object.keys(quickLinks)[2]));
	
	// This starts building up the page as it is in the pure html version
	// There is only one elemenmt with this class name
	var midColumn = document.getElementById("midCol");
	
	// Iterates over all rows and adds them to the middle column.
	for(var i in rows)
	{
		midColumn.appendChild(rows[i]);
	}
	
	// now that the page has been created
	// requests the data to the server and updates
	// the auctions' lists
	getUserAuctions();
}

function createAuctionInfoRow(msg)
{
	// The first row is always shown
	// Used to distinguish between the different types of auctions, offers or details.
	var type = "details";
	// This is the heading text.
	// It will be changed when the data will be obtained
	// from the server.
	var hText = "Informazioni";
	// Object.keys returns a collection containing
	// the keys of quickLinks.
	// The quickLinks keys are the headings' IDs
	var hId = Object.keys(quickLinks)[0];
	var rowClass = "first-row details";
	// There is no class to add in this case
	var sectionClass = "";
	// Creates an object to use as parameter
	// for the createRow function.
	// In order to dinamically build the page.
	// Useful because that function is used for other pages too.
	if(typeof(msg) !== "undefined")
		var rowObj = new RowObj(type, hText, hId, rowClass, sectionClass, msg);
	else
		var rowObj = new RowObj(type, hText, hId, rowClass, sectionClass);
	
	// Creates the second row of the middle column and
	// adds it to the array of rows.
	// It will contain the user's closed auctions;
	// The list will be updated once the data will be returned by
	// the server later.
	return createRow(rowObj);	
}


function showAuctionDetails(detailsObj, hId)
{
	// Creates the first row of the offerta or dettagli pages.
	var row = document.createElement("div");
	row.className = "row " + detailsObj.rowClass;
	
	// Creates the section that will contain the divs
	// the one with the auction's details and eventually
	// the one with the awarded user's details
	var section = document.createElement("section");
	
	// var This is the div that will contain the auction's details
	var aucDiv = document.createElement("div");

	// dtlsObj contains the auction, article, isExpired variable and the
	// auction's formatted deadline.
	var dtlsObj = detailsObj.dtlsObj;
	
	// The heading contains the article's name.
	var heading2 = document.createElement("h2");
	heading2.id = hId;
	heading2.innerText = dtlsObj[1].name;
	
	// The heading contains the article's code.
	var heading4 = document.createElement("h4");
	heading4.id = hId;
	heading4.innerText = "Codice: " + dtlsObj[1].code;
	
	// This link allows to click onto the shown image
	var imgA = document.createElement("a");
	imgA.href = "data:image/jpeg;base64," + dtlsObj[1].image;
	
	// This is the article's image
	var img = document.createElement("img");
	img.src = "data:image/jpeg;base64," + dtlsObj[1].image;
	// Shown if the image can't be loaded for some reason
	img.alt = "Article picture";
	
	// Appends the image to it's link.
	imgA.appendChild(img);
	
	// This is the article's description
	var p = document.createElement("p");
	p.className = "description";
	p.innerText = dtlsObj[1].description;
	
	// Paragraph that shows the initial price of the auction
	var initPriceP = document.createElement("p");
	initPriceP.innerHTML = "<strong>Prezzo iniziale:</strong> " + formatNumber(dtlsObj[0].initialPrice) + " &euro;";
	
	// Paragraph that shows the minimum upside offer of the auction
	var minUpsideOfferP = document.createElement("p");
	minUpsideOfferP.innerHTML = "<strong>Rialzo minimo:</strong> " + formatNumber(dtlsObj[0].minUpsideOffer) + " &euro;";
	
	// Paragraph that shows the formatted deadline
	var deadlineP = document.createElement("p");
	deadlineP.innerHTML = "<strong>Scadenza:</strong> " + dtlsObj[2];
	
	// Appends all the elements above to the auction's
	// details div
	aucDiv.appendChild(heading2);
	aucDiv.appendChild(heading4);
	aucDiv.appendChild(imgA);
	aucDiv.appendChild(p);
	aucDiv.appendChild(initPriceP);
	aucDiv.appendChild(minUpsideOfferP);
	aucDiv.appendChild(deadlineP);
	
	// Appends aucDiv to the section element
	section.appendChild(aucDiv);
	
	// Check if the auction is closed
	if(dtlsObj[0].isClosed)
	{
		// the quickLinks must be updated
		var quickLinks = {'dtl-art' : 'Dettagli', 'dtl-info' : 'Informazioni'}
		
		// Sets the quick-navigation's links
		setupQuickNav(quickLinks);
		
		// This is the div that will contain the awarded user's details
		var awrdUserDiv = document.createElement("div");
		var hr = document.createElement("hr");
		
		// Appends hr to awrdUserDiv
		awrdUserDiv.appendChild(hr);

		// This is the awarded user heading
		var heading2 = document.createElement("h2");
		heading2.id = Object.keys(quickLinks)[1];

 		// If there is an awarded user
		// dtlsObj[4] contains the details of the awarded user
		if(dtlsObj[4] !== null && typeof(dtlsObj[4].id) !== "undefined")
		{
			heading2.innerText = "Informazioni aggiudicatario:";
			
			// Shows the username
			var usernameP = document.createElement("p");
			usernameP.innerHTML = "<strong>Nome:</strong> " + dtlsObj[4].username;

			// Shows the awarded user's offer
			// maximum auction's offer
			var offerP = document.createElement("p");
			offerP.innerHTML = "<strong>Offerta:</strong> " + formatNumber(dtlsObj[5].value) + " &euro;";

			// Shows the awarded user's shipping address
			var addressP = document.createElement("p");
			addressP.innerHTML = "<strong>Indirizzo:</strong> " + dtlsObj[4].address;
			
			// Appends all the elements to awrdUserDiv
			awrdUserDiv.appendChild(heading2);
			awrdUserDiv.appendChild(usernameP);
			awrdUserDiv.appendChild(offerP);
			awrdUserDiv.appendChild(addressP);
		}
		// There isn't an awarded user
		// no one has made an offer for the current article
		else
		{
			heading2.innerText = "Informazioni:";
			
			// Showed if the
			var p = document.createElement("p");
			p.innerText = "Purtroppo non è stata fatta alcuna offerta per questo articolo.";
			
			// Appends all the elements to awrdUserDiv
			awrdUserDiv.appendChild(heading2);
			awrdUserDiv.appendChild(p);		
		}
		// Appends the div to the section
		section.appendChild(awrdUserDiv);
	}
	// Appends the section to the row
	row.appendChild(section);
	
	return row;
}


// Used to create the dettagli and offerta pages
function setupAuctionDetailsPages(detailsObj)
{
	console.log(detailsObj);
	
	// dtlsObj contains the auction,
	// it's related article, the formatted deadline,
	// the isExpired variable and the awarded user object
	// returned by the server.
	var dtlsObj = detailsObj.dtlsObj;

	// Sets the proper css and quick navigation's links
	if(detailsObj.type === "offerta")
	{
		document.title = "Offerte";
		var cssLinks = ["css/main.css", "css/auctions-offers.css", "css/auction-details.css", "css/offerta.css"];
		var quickLinks = {'dtl-art' : 'Dettagli', 'dtl-offers' : 'Offerte', 'offr-new' : 'Nuova Offerta'}
		// Updates the visitedAuctions and the lastAction
		// dtlsObj[0] contains the auctions' details
		// The auction's id must be converted to a string,
		// since the cookie's visitedAuctions contain strings only.
		addCookieVisitedAuction(window.cookieName, dtlsObj[0].id.toString());
	}
	// dettagli
	else
	{
		document.title = "Dettagli";
		var cssLinks = ["css/main.css", "css/auctions-offers.css", "css/auction-details.css", "css/dettagli.css"];
		var quickLinks = {'dtl-art' : 'Dettagli', 'dtl-offers' : 'Offerte'}		
	}
	// Sets the links inside the head tag to import the css files.
	setupCssLinks(cssLinks);
	// Sets the quick-navigation's links
	setupQuickNav(quickLinks);	
	
	// Clears the content of the middle column
	clearPage();
	
	// This is the list that will contain all the rows.
	// It will be used to dinamically add the rows to the middle column.
	var rows = [];

	// Checks if the server has returned an auction, which is the page to show and if the
	// auction is expired
	if(typeof(dtlsObj[0].initialPrice) !== "undefined" && detailsObj.type === "offerta" && dtlsObj[3] === true)
	{
		var msg = "L' asta per l' articolo selezionato è scaduta!";
		rows.push(createAuctionInfoRow(msg));

	}
	// There is no auction to show
	else if(typeof(dtlsObj[0].initialPrice) === "undefined" && detailsObj.type === "offerta")
	{
		var msg = "L' asta selezionata non esiste!";
		rows.push(createAuctionInfoRow(msg));		
	}
	// There is no auction to show
	else if(typeof(dtlsObj[0].initialPrice) === "undefined" && detailsObj.type === "offerta")
	{
		var msg = "L' utente non ha creato l' asta oppure l' asta selezionata non esiste!";
		rows.push(createAuctionInfoRow(msg));	
	}
	// The auction exists
	// If the page is 'offerta' the auction is surely not expired,.
	// because of the if-else structure
	else
	{
		
		// Creates the first row of the middle column and
		// adds it to the array of rows.
		// It will contain auction's details.
		// Object.keys returns a collection containing
		// the keys of quickLinks.
		// The quickLinks keys are the headings' IDs
		rows.push(showAuctionDetails(detailsObj, Object.keys(quickLinks)[0]));
		
		// Checks if the auction is open
		if(dtlsObj[0].isClosed === false)
		{
			// Used to distinguish between the different types of auctions or offers.
			var type = "offers";
			// This is the heading text.
			// It will be changed when the data will be obtained
			// from the server.
			var hText = "Offerte pervenute";
			// Object.keys returns a collection containing
			// the keys of quickLinks.
			// The quickLinks keys are the headings' IDs
			var hId = Object.keys(quickLinks)[1];
			var rowClass = "second-row offers-list";
			// There is no class to add in this case
			var sectionClass = "scrollable mid-col";
			// Creates an object to use as parameter
			// for the createRow function.
			// In order to dinamically build the page.
			// Useful because that function is used for other pages too.
			var rowObj = new RowObj(type, hText, hId, rowClass, sectionClass);
		
			// Creates the second row of the middle column and
			// adds it to the array of rows.
			// It will contain all the offers received for the current article.
			// The list will be updated once the data will be returned by
			// the server later.
			rows.push(createRow(rowObj));			
		}
	}

	// This starts building up the page as it is in the pure html version
	// There is only one elemenmt with this class name
	var midColumn = document.getElementById("midCol");
	
	// Iterates over all rows and adds them to the middle column.
	for(var i in rows)
	{
		midColumn.appendChild(rows[i]);
	}
	
	// Checks if the server returned the auction details
	// and ensures the auction is not expired or closed
	if(typeof(dtlsObj[0].initialPrice !== "undefined" && dtlsObj[3] === false && dtlsObj[0].isClosed === false))
	{
		// now that the page has been created, requests the auction's offers
		// to the server and updates it
		// dtlsObj[0].id is the current auction's id
		// detailsObj.type is the current page (offerta or dettagli)
		getAuctionOffers(dtlsObj[0].id, Object.keys(quickLinks)[2], detailsObj.type);		
	}
}

function showHome()
{
	document.title = "Aste Online";
	
	setupQuickNav([]);

	// Sets the links inside the head tag to import the css files.	
	var cssLinks = ["css/main.css", "css/home.css"];
	setupCssLinks(cssLinks);

	// Clears the content of the middle column
	clearPage();
	
	var midCol = document.getElementById("midCol");
	midCol.className = midCol.className + " introduction";
	
	var section = document.createElement("section");
	section.className = "overview";
	
	// window.cookieName.substr(0, window.cookieName.indexOf("-")) sets the username
	// `` are required to create a multiline string
	// Appends the elements to the section
	section.innerHTML = `<h2>Aste Online!</h2>
						<p>Bentornato ` + window.cookieName.substr(0, window.cookieName.indexOf("-")) + `,<br><strong>Aste online</strong> non è un sito popolare, ma è un luogo in cui le persone possono
						ritrovarsi, acquistare a prezzi unici, oppure vendere un qualsiasi articolo. Ogni utente è libero di
						valutare il proprio usato o nuovi articoli in suo possesso e di creare un' asta per venderli. Non c' è alcuna limitazione
						 sul numero di aste a disposizione per ogni utente e non viene applicata alcuna commissione sugli articoli venduti.
						 Ci impegnamo per garantire che tutti i venditori e gli acquirenti siano tutelati durante l' utilizzo della nostra piattaforma 
						 al fine di fornire un' esperienza il più possibile piacevole ed efficiente.
						 <br>Aste online è quindi un sito nel quale è possibile gestire autonomamente acquisti e vendite, acquirente e venditore dovranno
						 accordarsi tra loro su come gestire la spedizione o effettuare eventuali cambi di proprietà. Verranno tuttavia effettuati controlli
						 per escludere la possibilità di azioni o vendite di articoli illegali. E' a discrezione di ogni utente il rispetto
						 di tutte le norme vigenti.<p>
						 <h2>Informativa sulla privacy</h2>
						 <p>I dati raccolti vengono utilizzati esclusivamente per garantire il corretto funzionamento
						 dei servizi messi a disposizione. I dati collezionati sono solamente quelli forniti dall' utente: credenziali e indirizzo di spedizione,
						 aste create/aggiudicate e offerte effettuate.<br>Non vengono utilizzati cookie ne vengono raccolte altre informazione sull' utente
						 e sulla sua attività online. I dati sono salvati all' interno di server proprietari e non vengono condivisi con terze parti<p>`;
				
	// Appends the section to the middle column
	midCol.appendChild(section);
}