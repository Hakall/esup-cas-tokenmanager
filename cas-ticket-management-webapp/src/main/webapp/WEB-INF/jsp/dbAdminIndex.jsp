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
		<c:if test="${!activate}">
			<c:url value="/admin/activate" var="activateServiceUrl"/>
			<a href="${activateServiceUrl}" class="btn btn-default">
				<spring:message code="admin.btn.activate"/>
			</a>
		</c:if>	
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
						<c:if test="${user.getTickets().size()>0}">
							<tr>
								<td>
									<p>${user.getUid()}</p>
								</td>
								<td>
									<button class="btn btn-default" onclick="showTickets('${user.getUid()}');" id="${user.getUid()}ShowBtn">
										<spring:message code="admin.form.submit.show"/>
									</button>
								</td>
								<td>
									<form:form method="post" action="admin/deleteAll?owner=${user.getUid()}" role="form">
									<button type="submit" class="btn btn-danger">
										<spring:message code="admin.form.submit.delete"/>
									</button>
									</form:form>
								</td>
							</tr>
							<tr class="userTickets hide" id="${user.getUid()}Tickets">
								<td colspan="3" class="ticketsList">
									<table class="table table-responsive table-striped table-hover">
										<thead>
											<tr>
												<th>
													<spring:message code="user.table.device"/>
												</th>
												<th>
													<spring:message code="user.table.creationDate"/>
												</th>
												<th>
													<spring:message code="user.table.expirationDate"/>
												</th>
												<th>
													<spring:message code="user.table.spot"/>
												</th>
												<th>
													<spring:message code="user.table.action"/>
												</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="ticket" items="${tickets}" varStatus="status">
											<c:if test="${user.getUid()==ticket.getOwner()}">
												<tr>
													<td>
														<cas:uaDetector userAgent="${ticket.getAuthenticationAttributes().userAgent}"/>
													</td>
													<td>
														<jsp:useBean id="creationTime" class="java.util.Date" />
														<jsp:setProperty name="creationTime" property="time" value="${ticket.getCreationTime()}" />
														<fmt:formatDate value="${creationTime}" type="both" />
													</td>
													<td>							
													<c:choose>
														<c:when test="${ticket.getAuthenticationAttributes()['org.jasig.cas.authentication.principal.REMEMBER_ME'] == true}">
															
															<fmt:parseNumber var="expirationPolicy" 
																			 integerOnly="true" 
																			 type="number" 
																			 value="${rememberMeExpirationPolicyInSeconds}" />
														</c:when>
														<c:when test="${ticket.getAuthenticationAttributes()['org.jasig.cas.authentication.principal.REMEMBER_ME'] == false}">
															
															<fmt:parseNumber var="expirationPolicy" 
																			 integerOnly="true" 
																			 type="number" 
																			 value="${expirationPolicyInSeconds}" />
														</c:when>
														<c:otherwise>
															
															<fmt:parseNumber var="expirationPolicy" 
																			 integerOnly="true" 
																			 type="number" 
																			 value="${expirationPolicyInSeconds}" />
														</c:otherwise>
													</c:choose>
													<cas:expirationDate creationDate="${creationTime}" expirationPolicy="${expirationPolicy}" var="expirationDate"/>
													<fmt:formatDate value="${expirationDate}" type="date"/>
												</td>
												<td>
													<c:if test="${activateIpGeolocation}">
														<cas:ipLocator ipAddress="${ticket.getAuthenticationAttributes().ipAddress}"/>
													</c:if>
													( <cas:timeConverter time="${ticket.getLastTimeUsed()}"/> )
												</td>
												<td>
													<c:url value="/admin/delete/${ticket.getId()}" var="revokeUrl"/>
													<a href="${revokeUrl}" class="btn btn-danger">
														<spring:message code="user.table.revoke"/>
													</a>
												</td>
												</tr>
											</c:if>
											</c:forEach>
										</tbody>
									</table>
								</td>
							</tr>
						</c:if>
					</c:forEach>
				</tbody>
			</table>
		</c:otherwise>
		</c:choose>
		<c:if test="${pageNumber!=null || pageNumber<1}">
			<ul class="pagination">
				<c:forEach var="i" begin="1" end="${pageNumber + 1}">
					<c:url value="/admin" var="paginatorUrl">
						<c:param name="page" value="${i}"/>
					</c:url>
					<c:choose>
						<c:when test="${i==(currentPage+1)}">
							<c:set var="activeClass" value="class=\"active\""/>
						</c:when>
						<c:otherwise>
							<c:set var="activeClass" value=""/>
						</c:otherwise>
					</c:choose>
					<li ${activeClass}>
						<a href="${paginatorUrl}">${i}</a>
					</li>
				</c:forEach>
			</ul>
		</c:if>
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
<jsp:directive.include file="includes/footer.jsp" />
