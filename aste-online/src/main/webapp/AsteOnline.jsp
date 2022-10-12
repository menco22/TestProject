<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="it" dir="ltr">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

		<title>Titolo</title>

		<link rel="icon" href="img/icon.png">

		<%-- Java here is used only to redirect to the login page if the
		session doesn't exist.
		It's not possible to access the session object in javascript --%>

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
			    <!-- The type is "text/javascript" by default-->
			    <script src="js/cookies-management.js"></script>
			    <script src="js/data-management.js"></script>
			    <script src="js/form-validation.js"></script>
			   	<script src="js/event-handlers.js"></script>
			   	<script src="js/objects.js"></script>
			    <script src="js/views-management.js"></script>
			    <!-- This is the script that will be used as starting point. -->
			    <script src="js/main.js"></script>
			</c:otherwise>
		</c:choose>
	</head>
	<body>
		<%-- This will be used inside the javascript files to create and update the cookie --%>
		<input id="logUser" type="hidden" value="${sessionScope.currUser.username}">
		<div class="col lft-col">
		<!-- Added later with javascript -->
		</div>
		<div id="midCol" class="col mid-col">
		
		<!-- Added later with javascript -->
		</div>
	    <div class="col rgt-col">
			<div class="quick-nav menu">
		        <h3>Menu</h3>
		        <ul>
		          <!-- Some event's handlers will be binded to these links later-->
		          <a id="qck-home" href="#"><li>Home</li></a>
		          <a id="qck-acquisto" href="#"><li>Acquista</li></a>
		          <a id="qck-vendo" href="#"><li>Vendi</li></a>
		          <a href="Logout" href="#"><li>Esci</li></a>
		        </ul>
	      	</div>
	    </div>
	</body>
</html>