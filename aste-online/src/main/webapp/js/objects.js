// This is constructor function
function DiffDAte(days, hours, minutes)
{
	this.days = days;
	this.hours = hours;
	this.minutes = minutes;
}


// This is a constructor function
function RowObj(type, hText, hId, rowClass, sectionClass, msg)
{
	// Type of the content to show e.g. offers, auctions or details
	this.type = type;
	// Row's heading text.
	this.hText = hText;
	// This is the id for the heading
	this.hId = hId;
	// Checks if a message as been passed as a parameter
	if(typeof(msg) === "undefined")
	{
		// Message set while the client waits for the server to return some data
		this.msg = "Caricamento in corso..";		
	}
	// This is the class of the row.
	this.rowClass = rowClass;
	// This is the class of the section
	this.sectionClass = sectionClass;
}

// This is a constructor function
function AucListObj(aucType, aucObj, rowClass, msg, key)
{
	// Type of auction e.g. opened, closed, filtered or visited
	this.aucType = aucType;
	// This is the text to write if the list of auctions is empty
	this.msg = msg;
	// Object that contains the auctions
	// returned by the server along with their
	// articles.
	this.aucObj = aucObj;
	// This is the class of the row.
	this.rowClass = rowClass;
	// This is the key the user has submitted
	// in order to filter the auctions.
	this.key = key;
}

// This is a constructor function
function OffrListObj(offrType, offrObj, thObj, rowClass, msg)
{
	// Type of offer e.g. awarded, details
	this.offrType = offrType;
	// This is the text to write if the list of offers is empty
	this.msg = msg;
	// Object that contains the artciles
	// returned by the server along with their
	// offers.
	this.offrObj = offrObj;
	// This contains all table's headings along with their classes.
	this.thObj = thObj;
	// This is the class of the row.
	this.rowClass = rowClass;
}

// This is a constructor function.
function DetailsObj(type, dtlsObj, rowClass)
{
	// This is used to distinguish between the offerta and dettagli pages.
	this.type = type;
	// Object that contains the auction,
	// it's related article, the formatted deadline,
	// the isExpired variable and the awarded user object
	// returned by the server.
	this.dtlsObj = dtlsObj;
	// This is the class of the row.
	this.rowClass = rowClass;
}

// This is a constructor function.
function CookieObj(lastAction, visitedAuctions)
{
	// lastAction is the latest action performed by the user
	this.lastAction = lastAction;
	// visitedAuctions is the list of all the auctions
	// the user has viewed.
	if(Array.isArray(visitedAuctions))
		this.visitedAuctions = visitedAuctions;
	else
		this.visitedAuctions = [visitedAuctions];
}