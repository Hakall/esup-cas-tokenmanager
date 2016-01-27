<jsp:directive.include file="includes/header.jsp" />

	<c:if test="${delete}">
		<div class="modal fade" id="modal-delete"><div class="modal-dialog"><div class="modal-content">
		      
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="user.modal.title"/>
				</h4>
			</div>
			<div class="modal-body">
				<p>
					<spring:message code="user.modal.body"/>
				</p>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-success" data-dismiss="modal">
					<spring:message code="user.modal.close"/>
				</button>
			</div>

			<script type="text/javascript">
				window.onload=function(){ $('#modal-delete').modal(); };
			</script>

		</div></div></div>
	</c:if>

	<h1>
		<spring:message code="header.title"/>
	</h1>

<jsp:directive.include file="includes/ticketsList.jsp" />

<jsp:directive.include file="includes/footer.jsp" />