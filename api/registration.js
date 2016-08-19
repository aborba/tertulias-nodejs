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
		console.log(req.azureMobile);
		var voucher = req.params.voucher;
		var userSid = 'req.azureMobile.user.id';
		var body = '<h1>Tertulias</h1>\n' +
			'	<p>Welcome to Tertulias platform site.</p>' +
			'	<p>You arrived at this page because you followed a link with a private invitation from a friend of yours to join a Tertulia.</p>' +
			'	<p>Your voucher number is <strong>' + voucher + '</strong>.</p>' +
			'	<p>Your user id is <strong>' + '</strong>.</p>' +
			'';
		res.send(body);
    	next();
    });

    router.access = 'authenticated';
    router.get.access = 'authenticated';

    return router;
};
