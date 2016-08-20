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

	router.get('/:voucher', authenticate, (req, res, next) => {
		console.log('in GET /private_invitation/:voucher');
		console.log(configuration);
		var voucher = req.params.voucher;
		console.log(voucher);
		var body = '' +
			'<script type="application/javascript" src="/MobileServices.Web.min.js"></script>\n' +
			'<h1>Tertulias</h1>\n' +
			'	<p>Welcome to Tertulias platform site.</p>\n' +
			'	<p>You arrived at this page because you followed a link with a private invitation from a friend of yours to join a Tertulia.</p>\n' +
			'	<p>Your voucher number is <strong><span id="voucher">____________________________________</span></strong> and it is valid to subscribe to tertulia <span id="tertulia">______________</span>.</p>\n' +
			'	<p>Your user id is <strong><span id="userId">____________________________________</span></strong>.</p>\n' +
			'	<p>Press the button bellow to authenticate with your authentication provider and to subscribe to the tertulia.</p>\n' +
			'	<button onclick="signIn(subscribe)">Subscribe</button>' +
			'<script type="application/javascript">\n' +
			'	function getVoucher(href)) {\n' +
			'		return href.substr(href.lastIndexOf("/") + 1);\n' +
			'	};\n' +
			'	function signIn(continuation) {\n' +
			'		new WindowsAzure.MobileServiceClient("https://tertulias.azurewebsites.net",\n' +
			'				"309180942544-p7pg44n9uamccukt8caic0jerl2jpmta.apps.googleusercontent.com")\n' +
			'			.login("google")\n' +
			'			.done(\n' +
			'				function(results) {\n' +
			'					var userSid = results.userId;\n' +
			'					var voucher = getVoucher(window.location.href);\n' +
			'					continuation(userSid, voucher);\n' +
			'				},\n' +
			'				function(err) { alert("Error: " + err); }\n' +
			'			);\n' +
			'	};\n' +
			'	function subscribe(userSid, voucher) {\n' +
			'		document.getElementById("userId").innerHTML = userSid;\n' +
			'		alert(userSid + " " + voucher);' +
			'		' +
			'		' +
			'		' +
			'	};\n' +
			'</script>\n' +
			'<script type="application/javascript">\n' +
			'	document.getElementById("voucher").innerHTML = getVoucher(window.location.href);\n' +
			'</script>\n' +
			'';
		res.send(body);
		next();
	});
	return router;
};
