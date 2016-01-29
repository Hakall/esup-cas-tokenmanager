<jsp:directive.include file="includes/header.jsp" />
<div class="jumbotron app">	
	<h2>Gestionnaire des tickets de connexion</h2>
	<c:url value="/user" var="userIndex"/>
			<a href="${userIndex}"><button type="button" class="btn btn-info">Utilisateur</button></a>
	<c:url value="/admin" var="adminIndex"/>
			<a href="${adminIndex}"><button type="button" class="btn btn-warning">Administrateur</button></a>
</div>
<jsp:directive.include file="includes/footer.jsp" />
