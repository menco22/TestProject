<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%-- Specify the language of the page and the direction, left-to-right --%>
<html lang="it" dir="ltr">
  <head>
  	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

    <title>Acquisto</title>
    
    <link rel="icon" href="${pageContext.request.contextPath}/img/icon.png">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/auctions-offers.css">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/acquisto.css">
	 
  </head>
  <body>
    <div class="col lft-col">
      <div class="quick-nav">
        <h3>Navigazione Rapida</h3>
        <ul>
          <a href="#filter-auc"><li>Ricerca Asta</li></a>
          <a href="#available-auc"><li>Aste Disponibili</li></a>
          <a href="#awarded-art"><li>Aste Aggiudicate</li></a>
        </ul>
      </div>
    </div>
    <div class="col mid-col">
        <%-- This checks if the user is logged in --%>
        <%-- choose is like a switch --%>
        <c:choose>
          <%-- This actually checks if the session's attribute is set (if the user is logged in) --%>
          <c:when test="${sessionScope.currUser == null}">
            <%-- Defines a url to use with redirect --%>
            <c:url var="loginUrl" value="login.jsp"/>
            <%-- Redirects the client to the given url(the login page) --%>
            <c:redirect url="${loginUrl}"/>
          </c:when>

          <%-- This  checks if the list of offers that the user has won is null
          If so, the user is redirected to GoToAcquisto
          that sets the required objects to properly display the page --%>
          <c:when test="${awardedArticles == null}">
            <%-- Defines a url to use with redirect
            The redirect is performed without the key parameter
            Only the list of awarded articles will be returned this way --%>
            <c:url var="acquistoUrl" value="GoToAcquisto" />
            <%-- Redirects the client to the given url --%>
            <c:redirect url="${acquistoUrl}"/>
          </c:when>
          <c:when test="${sessionScope.currUser != null}">
	          <%-- This following case is executed only if the session's attribute is set and it's shown everytime --%>
	          <div class="row first-row form">
	          	  <h2 id="filter-auc">Ricerca tramite parola chiave</h2>
	          	  
	          	  <%-- If the auction list is empty, it means that no auction that contains the key
	           has been returned by FilterAuction --%>
	           <c:if test="${auctions != null && auctions.isEmpty()}">
	             <p>Nessun' asta contiene la parola chiave: "${key}"</p>
	           </c:if>
	           
	           <%-- The user can submit the keyword in the form only if logged in! --%>
	           <form action="GoToAcquisto" method="post">
	               <div>
	                 <label for="key">Chiave:</label>
	                 <input type="text" name="key" id="key" required>
	               </div>
	               <button name="keyword-submit" type="submit">Cerca</button>
	           </form>
	          </div>
		      <c:choose>
		          <%-- If the session's attribute is set, and there is at least an auction that contains the key --%>
		          <c:when test="${auctions != null && not auctions.isEmpty()}">
		          	<div class="row second-row auc-list">
				         <%-- The key is the one sent with the form --%>
				         <h2 id="available-auc">Aste disponibili per: "${key}"</h2>
				         <section class="scrollable">
				          <%-- This forEach takes also the varStatus and sets it's value inside the variable row
				          That variable is used to determine if the current iteration is even or odd in order
				          to set the correct class for each article --%>
				          <c:forEach var="entry" items="${auctions}" varStatus="row">
				
				            <%-- Sets the auction and artcile variables that will be used below --%>
				            <c:set var="auction" value="${entry.key}" scope="page"/>
				            <c:set var="article" value="${entry.value}" scope="page"/>
				
				            <%-- The following "choose tag" checks if the current iteration is even or odd and sets
				            the "Style" variable to use as class later--%>
				            <c:choose>
				                <c:when test="${row.count % 2 == 0}">
				                    <c:set var="Style" value="odd"/>
				                </c:when>
				                <c:otherwise>
				                    <c:set var="Style" value="even"/>
				                </c:otherwise>
				            </c:choose>
				
				            <%-- Creates an url with a parameter that contains the current auction id --%>
				            <c:url var="offertaUrl" value="GetAuctionDetails">
				              <c:param name="auctionId" value="${entry.key.id}" />
				              <%-- this tells the controller that the template to process is offerta.html
				              The same controller manages also the dettagli.html template --%>
				              <c:param name="page" value="offerta.html" />
				            </c:url>
				
				            <%-- Creates the link that points to the offerta.html page, with the parameters set above --%>
				            <a href="${offertaUrl}">
				              <%-- Sets the proper class for the current article --%>
				              <article class="${Style}">
				                  <h3>${article.name}</h3>
				                  <%-- It's not specified to show the code here --%>
				                  <h4>Codice: ${article.code}</h4>
				                  <%-- Displays the remainingTime for the current auction --%>
				                  <p>Tempo rimanente: ${remainingTimes.get(auction.id).days} giorni, ${remainingTimes.get(auction.id).hours} ore e ${remainingTimes.get(auction.id).minutes} minuti</p>
				              </article>
				            </a>
				          </c:forEach>
			            </section>
		            </div>
		          </c:when>
	          </c:choose>
          </c:when>
        </c:choose>
        
      <div class="row third-row offers-list">
        <h2 id="awarded-art">Aste aggiudicate</h2>
		<c:choose>
			<c:when test="${awardedArticles.isEmpty()}">
				<p>Al momento non è stata aggiudicata nessun' asta all' utente.</p>
			</c:when>
			<c:otherwise>
				<section class="scrollable scrll-table">
					<table>
						<thead>
							<tr>
								<th class="tbl-name">Nome Articolo</th>
								<th class="tbl-code">Codice</th>
								<th class="tbl-price">Prezzo (&euro;)</th>
							</tr>
						</thead>
						<tbody>
						  <%-- Iterates over the awarded articles' HashMap --%>
							<c:forEach var="awarded" items="${awardedArticles}" varStatus="row">
								<%-- Sets the auction and artcile variables that will be used below --%>
								<c:set var="article" value="${awarded.key}" scope="page"/>
								<c:set var="offer" value="${awarded.value}" scope="page"/>
	
								<%-- The following "choose tag" checks if the current iteration is even or odd and sets
								the "Style" variable to use as class later--%>
						        <c:choose>
						            <c:when test="${row.count % 2 == 0}">
						                <c:set var="Style" value="odd"/>
						            </c:when>
						            <c:otherwise>
						                <c:set var="Style" value="even"/>
						            </c:otherwise>
						        </c:choose>
						        <%-- The table rows are created dynamically --%>
								<tr class="${Style}">
									<td>${article.name}</td>
									<td>${article.code}</td>
									<%-- This formats the number in order to display it properly --%>
									<td><fmt:formatNumber type="number" value="${offer.value}" maxFractionDigits = "0"/></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</section>
			</c:otherwise>
		</c:choose>
      </div>
    </div>
    <div class="col rgt-col">
      <div class="quick-nav menu">
        <h3>Menu</h3>
        <ul>
          <a href="${pageContext.request.contextPath}/home.jsp"><li>Home</li></a>
          <a href="${pageContext.request.contextPath}/GoToAcquisto"><li>Acquista</li></a>
          <a href="${pageContext.request.contextPath}/GoToVendo"><li>Vendi</li></a>
          <a href="${pageContext.request.contextPath}/Logout"><li>Esci</li></a>
        </ul>
      </div>
    </div>
  </body>
</html>