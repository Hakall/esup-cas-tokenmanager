<jsp:directive.include file="includes/header.jsp" />

	<h2><spring:message code="header.title"/> <small><spring:message code="header.title.admin"/></small></h2>
	
	<c:if test="${delete}">
		<div class="modal fade" id="modal-delete"><div class="modal-dialog"><div class="modal-content">
		      
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="admin.modal.title"/>
				</h4>
			</div>
			<div class="modal-body">
				<p>
					<spring:message code="admin.modal.body"/>
				</p>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-success" data-dismiss="modal">
					<spring:message code="admin.modal.close"/>
				</button>
			</div>

			<script type="text/javascript">
				window.onload=function(){ $('#modal-delete').modal(); };
			</script>

		</div></div></div>
	</c:if>
	<fieldset>
		<c:choose>
		<c:when test="${fn:length(users) eq 0}">
		
			<p class="alert alert-warning alert-dismissable">
				<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
				<spring:message code="admin.alert.empty"/>
			</p>
		
		</c:when>
		<c:otherwise>
			<table class="table table-responsive table-striped table-hover">
				<thead>
					<tr>
						<th>
							<spring:message code="admin.table.name"/>
						</th>
						<th></th><th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="user" items="${users}" varStatus="status">
					<tr>
						<td>
							<p>${user}</p>
						</td>
						<td>
							<button class="btn btn-default" onclick="showTickets('${user}');" id="${user}ShowBtn">
								<spring:message code="admin.form.submit.show"/>
							</button>
						</td>
						<td>
							<form:form method="post" action="admin/deleteAll?owner=${user}" role="form">
							<button type="submit" class="btn btn-danger">
								<spring:message code="admin.form.submit.delete"/>
							</button>
							</form:form>
						</td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:otherwise>
		</c:choose>
	</fieldset>
	<form:form method="post" action="admin/deleteAll" role="form">
		
		<fieldset>
			<legend>
				<spring:message code="admin.form.legend"/>
			</legend>
	
			<p class="alert alert-warning alert-dismissable">
				<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
				<spring:message code="admin.form.alert"/>
			</p>
			
			<div class="form-group">
				<form:label path="owner">
					<spring:message code="admin.form.label"/>
				</form:label>
			
				<spring:message code="admin.form.placeholder" var="adminFormPlaceholder"/>
				<form:input path="owner" placeholder="${adminFormPlaceHolder}" class="form-control" required="true"/>
			</div>
			<button type="submit" class="btn btn-danger">
				<spring:message code="admin.form.submit.delete"/>
			</button>
		</fieldset>
				
	</form:form>
<!--  -->
<jsp:directive.include file="includes/footer.jsp" />
