var express         = require('express'),
	bodyParser      = require('body-parser');

var sql             = require('mssql'),
	util            = require('../util');

module.exports      = function (configuration) {
	var router      = express.Router(),
	authenticate    = require('azure-mobile-apps/src/express/middleware/authenticate')(configuration),
	fs				= require('fs'),
	authorize       = require('azure-mobile-apps/src/express/middleware/authorize');

	var completeError = function(err, res) {
		if (err) {
			console.error(err);
			if (res) res.sendStatus(500);
		}
	};

	router.get('/:voucher', authenticate, (req, res, next) => {
		console.log('in GET /private_invitation/:voucher');
		console.log(configuration);
		var voucher = req.params.voucher;
		console.log(voucher);
		fs.readfile('body.html', function(result) {
			res.send(result);
			next();
		});
		var body = `
<script type="application/javascript" src="/MobileServices.Web.min.js"></script>
<h1>Tertulias</h1>
	<p>Welcome to Tertulias platform site.</p>
	<p>You arrived at this page because you followed a link with a private invitation from a friend of yours, to join a Tertulia managed by him.</p>
	<p>Your voucher number is <strong><span id="voucher">____________________________________</span></strong>.</p>
	<p>In order to join the Tertulia, press the button bellow (You will be asked to authenticate with your authentication provider).</p>

	<button onclick="onClickAction()">Subscribe</button>

	<p id="userIdMessage"></p>
	<p id="tertuliaMessage"></p>

<script type="application/javascript">

	function getVoucher(href) {
		return href.substr(href.lastIndexOf("/") + 1);
	};

	function signInAndSubscribe(voucher) {
		new WindowsAzure.MobileServiceClient("https://tertulias.azurewebsites.net",
				"309180942544-p7pg44n9uamccukt8caic0jerl2jpmta.apps.googleusercontent.com")
			.login("google")
			.done(
				function(results) {
					var userSid = results.userId;
					var voucher = getVoucher(window.location.href);
					document.getElementById("userIdMessage").innerHTML = "You were assigned user id <strong>" + userSid + "</strong>.";
					subscribe(userSid, voucher);
				},
				function(err) { alert("Error: " + err); }
			);
	};

	function subscribe(userSid, voucher) {
		document.getElementById("userId").innerHTML = userSid;
		alert(userSid + " " + voucher);
	//<p id="userIdMessage">You were assigned user id <strong><span id="userId">____________________________________</span></strong>.</p>
	//<p id="tertuliaMessage">You subscribed to Tertulia <strong><span id="userId">____________________________________</span></strong>.</p>
		' +
		' +
		' +
	};

	function onClickAction() {
		signInAndSubscribe(getVoucher(window.location.href))' +
	};

</script>

<script type="application/javascript">
	document.getElementById("voucher").innerHTML = getVoucher(window.location.href);
</script>
`;
		res.send(body);
		next();
	});
	return router;
};
