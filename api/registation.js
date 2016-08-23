var express         = require('express'),
	bodyParser      = require('body-parser');

var sql             = require('mssql'),
	util            = require('../util');

module.exports      = function (configuration) {
	var router      = express.Router(),
	// authenticate    = require('azure-mobile-apps/src/express/middleware/authenticate')(configuration),
	authorize       = require('azure-mobile-apps/src/express/middleware/authorize');

	var completeError = function(err, res) {
		if (err) {
			console.error(err);
			if (res) res.sendStatus(500);
		}
	};

	router.get('/:voucher', (req, res, next) => {
		console.log('in GET /private_invitation/:voucher');
		var voucher = req.params.voucher;
		var body = `
<link rel="stylesheet" type="text/css" href="/tertulias.css">
<script type="application/javascript" src="/MobileServices.Web.min.js"></script>
<script type="application/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/fetch/1.0.0/fetch.min.js"></script>

<div class="header">
	<img src="/tertulias-web.png" alt="Tertulias logo">
	<h1>Tertulias</h1>
</div>

	<p>Welcome to Tertulias platform site.</p>
	<p>You arrived at this page because you followed a link with a private invitation from a friend of yours, to join a Tertulia managed by him.</p>
	<p>Your voucher number is <strong><span id='voucherPlaceHolder'></span></strong>.</p>
	<p>In order to join the Tertulia, press the button bellow (You will be asked to authenticate with your authentication provider).</p>
	<p align="center">»»» Please turn off the popup blocker to be able to authenticate «««</p>

	<button id='action' onclick="onClickAction('userIdMessagePlaceHolder')">Subscribe</button>

	<p id='userIdMessagePlaceHolder'></p>
	<p id='tertuliaMessagePlaceHolder'></p>

<script type="application/javascript">

	// Source: http://javascriptsource.com
	var isPopupsBlocked = () => {
		var puTest = window.open(null, "", "left=100, top=100, width=100, height=100, location=no, menubar=no, resizable=no, scrollbars=no, status=no, titlebar=no");
		try { puTest.close(); return false; }
		catch(e) { return true; }
	};

	var getVoucher = (href) => {
		return href.substr(href.lastIndexOf("/")+1);
	};

	var voucher = getVoucher(window.location.href).replace(/\#$/, '');
	document.getElementById('voucherPlaceHolder').innerHTML = voucher;

	var getConfirmationQuestion = (tertulia) => {
		var result = 'Please confirm that you want to join the tertulia';
		if (tertulia.name)
			result += ' named "' + tertulia.name + '"';
		result += '.';
		if (tertulia.subject)
			result += ' The tertulia subject is "' + tertulia.subject + '".';
		return result;
	};

	function onClickAction() {
		if (isPopupsBlocked()) {
			alert('Your browser is blocking popups; You need to enable popups in order to proceed.');
			return;
		}
		var client = new WindowsAzure.MobileServiceClient("https://tertulias.azurewebsites.net");
		client.login("google")
		.done(
			(results) => {
				var url = client.applicationUrl + '/.auth/me';
				var headers = new Headers();
				headers.append('X-ZUMO-AUTH', client.currentUser.mobileServiceAuthenticationToken);
				fetch(url, { headers: headers })
				.then( (data) => { return data.json(); })
				.then( (user) => {
					var user0 = user[0];
					var userSid = user0.user_id;
					var provider = user0.provider_name;
					var claims = user0.user_claims;
					var picture, email, name, givenname, surname;
					claims.forEach( (item, index) => {
						switch (item.typ) {
							case 'picture': picture = item.val; break;
							case 'http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress': email = item.val; break;
							case 'name': name = item.val; break;
							case 'http://schemas.xmlsoap.org/ws/2005/05/identity/claims/givenname': givenname = item.val; break;
							case 'http://schemas.xmlsoap.org/ws/2005/05/identity/claims/surname': surname = item.val; break;
						}
					});
					client.invokeApi('/tertulias/voucherinfo/' + voucher, { body: null, method: "get"})
					.done( (results) => {
						if (results.result.tertulias.length == 0) {
							alert("Voucher not available. If the voucher code is valid, " +
								"either it was already claimed or it has expired. " +
								"In order to join the Tertulia, please contact your source to get you a new voucher.");
							return;
						}
						var tertulia = results.result.tertulias[0];
						var confirmationQuestion = getConfirmationQuestion(tertulia);
						if ( ! confirm(confirmationQuestion))
							return;
						client.invokeApi("/", { body: null, method: "get" })
						.done( (results) => {
							var links = results.result.links;
					    	for (var i = 0; i < links.length; i++) {
					    		if (links[i].rel == "accept_invitation") {
					    			var method = links[i].method;
					    			var href = links[i].href;
					    			break;
					    		}
					    	}
					    	if ( ! href) {
					    		alert("An internal error ocurred, please try again later.");
					    		return;
					    	}
					    	client.invokeApi(href, { body: { voucher: voucher }, method: method })
					    	.done( (results) => { alert("You joined the Tertulia successfuly. Access the Tertulia using the app in your App store. Enjoy.");
					    	}, (err) => { alert("Tertulia join result: " + err.message); });
						}, (err) => { alert("System failure: " + err.message); });
					}, (err) => { alert("Voucher information retrieval failed: " + err.message); });
        		});
			}, (err) => { alert("Authentication failed: " + err.message); });
	};

</script>
`;
		res.send(body);
		next();
	});
	return router;
};
