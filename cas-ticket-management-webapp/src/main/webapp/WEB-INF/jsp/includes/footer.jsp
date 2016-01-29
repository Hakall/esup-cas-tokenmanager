<%@ page contentType="text/html; charset=UTF-8" %>
	
				</div>
			</div>
		</div>
	</div>

	<footer class="site-footer esupbar">
		<div class="container">
			<div class="row">
				<div class="col-md-12">
					<a href="https://www.esup-portail.org/node/28">Mentions légales</a>
					<a class="esup-link" href="https://www.esup-portail.org/node/28">Mentions légales</a>
				</div>
			</div>
		</div>
		
	</footer>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
	<script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
	<script type="text/javascript">
		var strings = new Array();
		strings['admin.form.submit.hide'] = "<spring:message code='admin.form.submit.hide' javaScriptEscape='true' />";
		strings['admin.form.submit.show'] = "<spring:message code='admin.form.submit.show' javaScriptEscape='true' />";
		function showTickets(id){
			if(document.getElementById(id+'Tickets').classList.contains("hide")){
				$('#'+id+'Tickets').removeClass('hide');
				$('#'+id+'ShowBtn').html(strings['admin.form.submit.hide']);
				console.log("get user tickets...");	
			}else{
				$('#'+id+'Tickets').addClass('hide');
				$('#'+id+'ShowBtn').html(strings['admin.form.submit.show']);
			}
			
		}
		window.onload=function(e) {
			parent.postMessage(getDocumentHeight(), "*"); 

			function getDocumentHeight() {
				var body = document.body;
				var html = document.documentElement;
				return Math.max(body.scrollHeight, body.offsetHeight,
								html.clientHeight, html.scrollHeight, html.offsetHeight);
			}
		};
	</script>
</body>
</html>