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
			'<script src="https://tertulias.scm.azurewebsites.net/api/vfs/site/wwwroot/MobileServices.Web.min.js"></script>\n' +
			'<h1>Tertulias</h1>\n' +
			'	<p>Welcome to Tertulias platform site.</p>\n' +
			'	<p>You arrived at this page because you followed a link with a private invitation from a friend of yours to join a Tertulia.</p>\n' +
			'	<p>Your voucher number is <strong><span id="voucher">' + voucher + '</span></strong>.</p>\n' +
			'	<p>Your user id is <strong><span id="userId">________________________________</span></strong>.</p>\n' +
			'<script>\n' +
			'	function signIn() {\n' +
     		'		new WindowsAzure.MobileServiceClient("https://tertulias.azurewebsites.net",\n' +
     		'				"309180942544-p7pg44n9uamccukt8caic0jerl2jpmta.apps.googleusercontent.com")\n' +
    		'			.login("google")\n' +
    		'			.done(\n' +
    		'				function(results) {\n' +
    		'					var href = window.location.href;\n' +
    		'					console.log(href);\n' +
    		'					var pos = "pos/href/kjahsdkadjh".lastIndexOf('/') + 1;\n' +
    		'					alert(href.substr(pos));\n' +
    		'					var userSid = results.userId;\n' +
    		'					var url = window.location.href;\n' +
    		'					//var voucher = document.getElementById("voucher").innerHTML;\n' +
    		'					var voucher = document.createElement(url).pathname;\n' +
    		'					document.getElementById("userId").innerHTML = userSid;\n' +
    		'					alert("userId: " + userSid + ", voucher: " + voucher);\n' +
    		'				},\n' +
    		'				function(err) { alert("Error: " + err); }\n' +
    		'			);\n' +
			'	};\n' +
			'	signIn();\n' +
			'</script>\n' +
			'';
		res.send(body);
    	next();
    });
    return router;
};
