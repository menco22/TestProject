<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%-- Specify the language of the page and the direction, left-to-right --%>
<html lang="it" dir="ltr">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		
		<title>Login</title>
		
		<link rel="icon" href="${pageContext.request.contextPath}/img/icon.png">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/login.css">
	</head>
	<body>
	<!-- This is required even if empty in order to keep the right proportions
	when the page size is reduced below the max-width set
	for @media inside main.css -->
    <div class="col lft-col">
    </div>
    <div class="col mid-col form lgn-form">
    	<h2>Accedi per proseguire</h2>
		<%-- This checks if the user is logged in --%>
		<%-- choose is like a switch --%>
		<c:choose>
			<%-- This actually checks if the session's attribute is set (if the user is logged in) --%>
			<c:when test="${sessionScope.currUser == null}">
				<%-- Defines the url to use below --%>
				<c:url value="Login" var="loginCtr"/>
				<form action="${loginCtr}" method="post">
					<div>
						<label>Nome Utente:</label>
						<input type="text" name="username" <%-- value="${request.getParameter("username")}" --%> required>
					</div>
					<div>
						<label>Password:</label>
						<input type="password" name="password" required>
					</div>
					<div>
						<input type="radio" name="version" value="html" required>
						<label>Html</label>
						<input type="radio" name="version" value="js" required>
						<label>Javascript</label>
					</div>
					<button name="login-submit" type="submit">Accedi</button>
				</form>
			</c:when>
			<%-- If the session's attribute is set, it means the user is logged in --%>
			<c:otherwise>
				<%-- If the user is already logged in and the cookie exists, --%>
				<%-- redirects to the javascript version's page (AsteOnline.jsp) --%>
				<%-- instead of showing the form. --%>
				<c:choose>
					<%-- This actually checks if the cookie exists --%>
					<c:when test="${cookie.AsteOnline != null}">
						<%-- Defines a url to use with redirect --%>
						<c:url value="AsteOnline.jsp" var="AsteOnline"/>
						<%-- Redirects the client to the given url(the AsteOnline page) --%>
						<c:redirect url="${AsteOnline}"/>
					</c:when>
					<%-- If the user is already logged in but the cookie doesn't exist, --%>
					<%-- redirects to the pure html version's default page (home.jsp) --%>
					<%-- instead of showing the form. --%>
					<c:otherwise>
						<%-- Defines a url to use with redirect --%>
						<c:url value="home.jsp" var="home"/>
						<%-- Redirects the client to the given url(the home page) --%>
						<c:redirect url="${home}"/>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
    </div>
    <!-- This is required even if empty in order to keep the right proportions
	when the page size is reduced below the max-width set
	for @media inside main.css -->
    <div class="col rgt-col">
    </div>
  </body>
</html>