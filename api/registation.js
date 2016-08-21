var express         = require('express'),
	bodyParser      = require('body-parser');

var sql             = require('mssql'),
	util            = require('../util');

module.exports      = function (configuration) {
	var router      = express.Router(),
	authenticate    = require('azure-mobile-apps/src/express/middleware/authenticate')(configuration),
	authorize       = require('azure-mobile-apps/src/express/middleware/authorize');

	var completeError = function(err, res) {
		if (err) {
			console.error(err);
			if (res) res.sendStatus(500);
		}
	};

	router.get('/:voucher', (req, res, next) => {
		console.log('in GET /private_invitation/:voucher');
		console.log(configuration);
		var voucher = req.params.voucher;
		console.log(voucher);
		var body = `
<link rel="stylesheet" type="text/css" href="/tertulias.css">
<script type="application/javascript" src="/MobileServices.Web.min.js"></script>
<script type="application/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/fetch/1.0.0/fetch.min.js"></script>
<script type="application/javascript" src="/Tertulias.js"></script>

<div class="header">
	<img src="/tertulias-web.png" alt="Tertulias logo">
	<h1>Tertulias</h1>
</div>

	<p>Welcome to Tertulias platform site.</p>
	<p>You arrived at this page because you followed a link with a private invitation from a friend of yours, to join a Tertulia managed by him.</p>
	<p>Your voucher number is <strong><span id='voucherPlaceHolder'></span></strong>.</p>
	<p>In order to join the Tertulia, press the button bellow (You will be asked to authenticate with your authentication provider).</p>

	<button id='action' onclick="onClickAction('userIdMessagePlaceHolder')">Subscribe</button>

	<p id='userIdMessagePlaceHolder'></p>
	<p id='tertuliaMessagePlaceHolder'></p>

<script type="application/javascript">
	var voucher = getVoucher(window.location.href);
	document.getElementById('voucherPlaceHolder').innerHTML = voucher;

	function getUserData(client) {
		var url = client.applicationUrl + '/.auth/me';
		var headers = new Headers();
		headers.append('X-ZUMO-AUTH', client.currentUser.mobileServiceAuthenticationToken);
		fetch(url, { headers: headers })
		.then(function (data) {
			return data.json();
		}).then(function (user) {
        	console.log(user);
        	console.log(user.name);
        });
	}

	function getInfo(client, voucher) {
		client.login("google")
		.done(function(results) {
			client.invokeApi('/me', {method: "post"})
			.done(function(res) {

			});
			var token = results.mobileServiceAuthenticationToken;
			var userSid = results.userSid;
			var userData = getUserData(client);
	    	client.invokeApi('/tertulias/voucherinfo/' + voucher, {
	        	body: null,
	        	method: "get"
	    	}).done(function(results0) {
	        	// var name = results.result.tertulias[0].name || "no name";
	        	var name = "no name";
	        	// var subject = results.result.tertulias[0].subject || "no subject";
	        	var subject = "no subject";

				signInAndSubscribe(client, voucher,
					'Are you sure you want to subscribe tertulia "' + name + '" about "' + subject + '"?',
					'userIdMessagePlaceHolder', 'Your user id is:');
			});
		}, function(err) {
			alert("Authentication failed: " + err);
		});
	};

	function onClickAction() {
		var client = new WindowsAzure.MobileServiceClient("https://tertulias.azurewebsites.net"); //, "309180942544-p7pg44n9uamccukt8caic0jerl2jpmta.apps.googleusercontent.com");
		// getInfo(client, voucher);
		client.login("google")
		.done(
			function(results) {
				var url = client.applicationUrl + '/.auth/me';
				var headers = new Headers();
				headers.append('X-ZUMO-AUTH', client.currentUser.mobileServiceAuthenticationToken);
				fetch(url, { headers: headers })
				.then(function (data) {
					return data.json();
				})
				.then(function (user) {
					var user0 = user[0];
					var userSid = user0.user_id;
					var provider = user0.provider_name;
					var claims = user0.user_claims;
					var picture; var email; var name; var givenname; var surname;
					claims.forEach(function(item, index) {
						switch (item.typ) {
							case 'picture':
								picture = item.val;
								break;
							case 'http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress':
								email = item.val;
								break;
							case 'name':
								name = item.val;
								break;
							case 'http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname':
								givenname = item.val;
								break;
							case 'http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname':
								surname = item.val;
								break;
						}
					});
					console.log(name);
        		});
			},
			function(err){
				alert("Authentication failed: " + err);
			});
	};

</script>
`;
		res.send(body);
		next();
	});
	return router;
};
