function checkName(name)
{
	// Ensures the string doesn't contains only spaces and checks
	// if it's length is between 3 and 51 characters	
	if(name.replaceAll(" ", "").length > 0 && name.length > 3 && name.length < 51)
		return true;
	return false;
}

function checkDescription(descr)
{
	// Ensures the string doesn't contains only spaces and checks
	// if it's length is between 3 and 51 characters	
	if(descr.replaceAll(" ", "").length > 0 && descr.length > 19 && descr.length < 3001)
		return true;
	return false;	
}

function validateStrings(name, description)
{
	if(typeof(name) !== "undefined" && typeof(description) !== "undefined")
		if(checkName(name) && checkDescription(description))
			return true;
	return false;	
}

function checkImage(image)
{
	// Checks if the uploaded file is an image
	if(typeof(image) !== "undefined" && image.type.indexOf("image/") !== -1)
		return true;
	return false;
}

function checkNumbers(initialPrice, minUpsideOffer)
{
  	if(initialPrice > 0 && initialPrice < 700000001 && minUpsideOffer > 49 && minUpsideOffer < 100001)
  		return true;
  	return false;
}

function checkDateTime(deadline)
{
	// Checks if the datetime is properly formatted
	// with a regex pattern
	if(deadline.match("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}"))
	{
		var remainingTime = getRemainingTime(deadline);
		// checks if the deadline is set in the future or to a past value
		if(remainingTime.days > 0 || remainingTime.hours > 0 || remainingTime.minutes > 0)
			return true;
	}
	return false;	
}

// This function checks all the user's input values
// in order to verify that they are correctly formatted
// and that they meet all the requirements
function checkNewAuctionForm(form)
{
	var err = "";
	if(validateStrings(form["name"].value, form["description"].value))
	{
		if(checkImage(form["image"].files[0]))
		{
			if(checkNumbers(form["initialPrice"].value, form["minUpsideOffer"].value))
			{
				if(checkDateTime(form["deadline"].value))
				{
					return true;
				}
				else
				{
					err += "la scadenza deve essere formattata in modo corretto!"
						+ "\nLa scadenza deve essere fissata nel futuro!";
				}
			}
			else
			{
				err += "il prezzo iniziale deve essere maggiore di 0, ma al massimo pari a 700 milioni!"
							+ "\nIl rialzo minimo deve essere almeno pari a 50, ma al massimo pari a 100 mila!";
			}
		}
		else
		{
			err += "il file caricato deve essere un' immagine!";
		}
	}
	else
	{
		err += "il nome dell' articolo non può contenere solo spazi e "
						+ "deve avere una lunghezza compresa tra 4 e 50 caratteri. "
						+ "\nLa descrizione deve avere una lunghezza compresa tra 20 e 3000 caratteri!";
	}
	// Returned only if one of the checks fails
	return err;
}

function checkKeyForm(key)
{
	// Regex pattern to check
	// [a-zA-Z] here allows also accented characters unlike java
	var pattern = /^[a-zA-Z]+$/
	if(pattern.test(key) && key.length > 2 && key.length < 21)
		return true;
	else
		return false;
}


function checkOfferForm(offerValue, minimumOffer)
{
	// Min sets the minum value the user can submit
	var min = minimumOffer;
	// 2 billions is the maximum value allowed
	var max = 2000000000;
	
	// The offer's value is correct if it stays in between
	if(min <= offerValue && offerValue <= max)
		return true;
	return false;
}