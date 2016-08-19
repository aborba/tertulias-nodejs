var express      = require('express'),
	bodyParser   = require('body-parser'),
    authenticate = require('azure-mobile-apps/src/express/middleware/authenticate'),
    authorize    = require('azure-mobile-apps/src/express/middleware/authorize');

var sql          = require('mssql'),
	util         = require('../util');

module.exports = function (configuration) {
    var router = express.Router();

	var completeError = function(err, res) {
	    if (err) {
	        console.error(err);
	        if (res) res.sendStatus(500);
	    }
	};

    router.get('/:voucher', (req, res, next) => {
		console.log('in GET /private_invitation/:voucher');
		if (! req.azureMobile.user) {
			console.log('redirecting...');
			// res.writeHead(302, {
			// 	'Location': 'tertulias.azurewebsites.net/.auth/login/google',
			// 	'redirect_uri': 'https://tertulias.azurewebsites.net/.auth/login/google/callback'
			// });

			res.redirect('tertulias.azurewebsites.net/.auth/login/google?redirect_uri=' +
				encodeURI('https://tertulias.azurewebsites.net/.auth/login/google/callback'));
			res.end();
			return;
		}
		console.log('proceeding...');
		var voucher = req.params.voucher;
		var userSid = 'req.azureMobile.user.id';
		var body = '' +
			'<script src="path/to/MobileServices.Web.min.js"></script>' +
			'<h1>Tertulias</h1>\n' +
			'	<p>Welcome to Tertulias platform site.</p>' +
			'	<p>You arrived at this page because you followed a link with a private invitation from a friend of yours to join a Tertulia.</p>' +
			'	<p>Your voucher number is <strong>' + voucher + '</strong>.</p>' +
			'	<p>Your user id is <strong>' + '</strong>.</p>' +
			'<script>' +
			'	alert("Hello");' +
			'	client' +
			'		.login( "google", {"access_token": token})' +
			'		.done(function (results) {' +
     		'				alert("You are now logged in as: " + results.userId);' +
			'			},' +
			'			function (err) {' +
     		'				alert("Error: " + err);' +
			'			}' +
			'		);' +
			'</script>' +
			'';
		res.send(body);
    	next();
    });

    express.access = 'authenticated';

    return router;
};
