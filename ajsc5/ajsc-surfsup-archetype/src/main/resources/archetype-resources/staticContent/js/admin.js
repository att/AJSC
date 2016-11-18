var surfDemo = (function() {
	return {
		init : function() {
			$(document).ready(function() {
				surfDemo.initalCheck();
			});

		},
		initalCheck : function() {

			var hostUrl = location.protocol + '//' + location.host
					+ "/services/";
			$
					.get(
							hostUrl
									+ 'SurfsUp/v1/jaxrsExample/jaxrs-services/admin',
							function(data) {
							})
					.done(function(data) {
						$("#addAdminBtn").attr("disabled", false);
					})
					.fail(
							function() {
								$("#addAdminBtn")
										.tooltip(
												{
													title : 'You are UNAUTHORIZED to perform this operation.'
												});
								$("#addAdminBtn").attr("disabled", true);
							}).always(function() {

					})
		},
		addUser : function() {

			var hostUrl = location.protocol + '//' + location.host
					+ "/services/";
			var userInput = $("#userInputTxt").val();
			$
					.get(
							hostUrl
									+ 'SurfsUp/v1/jaxrsExample/jaxrs-services/user/'
									+ userInput, function(data) {
							})
					.done(function(data) {

						$('.status').removeClass("text-danger");
						$('.status').html(data);
						$('.status').addClass("text-success");
					})
					.fail(
							function() {
								$('.status').removeClass("text-success");
								$('.status')
										.html(
												"WARNING!!. You are UNAUTHORIZED to perform this operation.");
								$('.status').addClass("text-danger");

							}).always(function() {

					})
		},
		invokeRest : function(type) {
			if (type == "xml") {
				type = "returnXml"
			} else {
				type = "returnJson"
			}

			var hostUrl = location.protocol + '//' + location.host + "/rest/";
			$
					.get(hostUrl + "SurfsUp/v1/" + type, function(data) {
					})
					.done(function(data) {

						$('#resultText').val(data);

					})
					.fail(
							function() {

								$('#resultText')
										.val(
												"WARNING!!. You are UNAUTHORIZED to perform this operation.");

							}).always(function() {

					})

		}
	}
})();