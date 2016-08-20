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
			'<script src="https://tertulias.scm.azurewebsites.net/api/vfs/site/wwwroot/MobileServices.Web.min.js"></script>' +
			'<h1>Tertulias</h1>\n' +
			'	<p>Welcome to Tertulias platform site.</p>' +
			'	<p>You arrived at this page because you followed a link with a private invitation from a friend of yours to join a Tertulia.</p>' +
			'	<p>Your voucher number is <strong>' + voucher + '</strong>.</p>' +
			'	<p>Your user id is <strong><span id="userId">________________________________</span></strong>.</p>' +
			'<script>' +
			'	function signIn() {' +
     		'		new WindowsAzure.MobileServiceClient("https://tertulias.azurewebsites.net",' +
     		'				"309180942544-p7pg44n9uamccukt8caic0jerl2jpmta.apps.googleusercontent.com")' +
    		'			.login("google")' +
    		'			.done(' +
    		'				function(results) {' +
    		'					var userSid = results.userId;' +
    		'					document.getElementById("userId").innerHTML = userSid;' +
    		'					alert("userId: " + userSid + ", voucher: " + voucher);' +
    		'				},' +
    		'				function(err) { alert("Error: " + err); }' +
    		'			);' +
			'	};' +
			'	signIn();' +
			'</script>' +
			'';
		res.send(body);
    	next();
    });
    return router;
};
