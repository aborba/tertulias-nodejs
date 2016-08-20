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
		var userSid = 'req.azureMobile.user.id';
		console.log(voucher);
		console.log(req.azureMobile.user);
		var body = '' +
			'<script src="https://tertulias.scm.azurewebsites.net/api/vfs/site/wwwroot/MobileServices.Web.min.js"></script>' +
			'<h1>Tertulias</h1>\n' +
			'	<p>Welcome to Tertulias platform site.</p>' +
			'	<p>You arrived at this page because you followed a link with a private invitation from a friend of yours to join a Tertulia.</p>' +
			'	<p>Your voucher number is <strong>' + voucher + '</strong>.</p>' +
			'	<p>Your user id is <strong>' + userSid + '</strong>.</p>' +
			'<script>' +
			'	function signIn(){' +
     		'		var MobileServiceClient = WindowsAzure.MobileServiceClient;' +
     		'		var client = new MobileServiceClient("https://tertulias.azurewebsites.net", "309180942544-p7pg44n9uamccukt8caic0jerl2jpmta.apps.googleusercontent.com");' +
    		'		client.login("google").done(' +
    		'			function (results) { alert("You are now logged in as: " + results.userId); },' +
    		'			function (err) { alert("Error: " + err); });' +
			'	}' +
			'	alert("Hello");' +
			'	signIn();' +
			'</script>' +
			'';
		res.send(body);
    	next();
    });
    return router;
};
