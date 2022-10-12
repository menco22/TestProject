<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%-- Specify the language of the page and the direction, left-to-right --%>
<html lang="it" dir="ltr">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		
		<title>Aste Online</title>
		
		<link rel="icon" href="${pageContext.request.contextPath}/img/icon.png">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/home.css">
		
	</head>
	<body>
		<!-- This is required even if empty in order to keep the right proportions
		when the page size is reduced below the max-width set
		for @media inside main.css -->
 	    <div class="col lft-col">
	    </div>
	    <div class="col mid-col introduction">
				<%-- This checks if the user is logged in --%>
				<%-- choose is like a switch --%>
				<section class="overview">
				<c:choose>
					<%-- This actually checks if the session's attribute is set (if the user is logged in) --%>
					<c:when test="${sessionScope.currUser == null}">
						<%-- Defines a url to use with redirect --%>
						<c:url value="login.jsp" var="loginUrl"/>
						<%-- Redirects the client to the given url(the login page) --%>
						<c:redirect url="${loginUrl}"/>
					</c:when>
					<%-- If the session's attribute is set, it means the user is logged in --%>
					<c:otherwise>
							<%-- Creates a bean and extract the user object from the session --%>
							<jsp:useBean id="currUser" scope="session" class="beans.User"/>
							<h2>Aste Online!</h2>
							<%-- Takes and prints the username of the currently logged user --%>
							<p>Bentornato ${currUser.username},<br><strong>Aste online</strong> non è un sito popolare, ma è un luogo in cui le persone possono
							ritrovarsi, acquistare a prezzi unici, oppure vendere un qualsiasi articolo. Ogni utente è libero di
							valutare il proprio usato o nuovi articoli in suo possesso e di creare un' asta per venderli. Non c' è alcuna limitazione
							 sul numero di aste a disposizione per ogni utente e non viene applicata alcuna commissione sugli articoli venduti.
							 Ci impegnamo per garantire che tutti i venditori e gli acquirenti siano tutelati durante l' utilizzo della nostra piattaforma 
							 al fine di fornire un' esperienza il più possibile piacevole ed efficiente.
							 <br>Aste online è quindi un sito nel quale è possibile gestire autonomamente acquisti e vendite, acquirente e venditore dovranno
							 accordarsi tra loro su come gestire la spedizione o effettuare eventuali cambi di proprità. Verranno tuttavia effettuati controlli
							 per escludere la possibilità di azioni o vendite di articoli illegali. E' a discrezione di ogni utente il rispetto
							 di tutte le norme vigenti.<p>
							 <h2>Informativa sulla privacy</h2>
							 <p>I dati raccolti vengono utilizzati esclusivamente per garantire il corretto funzionamento
							 dei servizi messi a disposizione. I dati collezionati sono solamente quelli forniti dall' utente: credenziali e indirizzo di spedizione,
							 aste create/aggiudicate e offerte effettuate.<br>Non vengono utilizzati cookie ne vengono raccolte altre informazione sull' utente
							 e sulla sua attività online. I dati sono salvati all' interno di server proprietari e non vengono condivisi con terze parti<p>
					</section>
				</c:otherwise>
			</c:choose>
		</div>
	    <!-- This is required even if empty in order to keep the right proportions
		when the page size is reduced below the max-width set
		for @media inside main.css -->
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