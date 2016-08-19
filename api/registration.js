var express = require('express'),
	bodyParser = require('body-parser'),
    authenticate = require('azure-mobile-apps/src/express/middleware/authenticate'),
    authorize = require('azure-mobile-apps/src/express/middleware/authorize');

var util = require('../util');

module.exports = function (configuration) {
    var router = express.Router();

	var completeError = function(err, res) {
	    if (err) {
	        console.error(err);
	        if (res) res.sendStatus(500);
	    }
	};

    router.get('/', (req, res, next) => {
		console.log('in GET /private_invitation');
		var body = '<h1>Tertulias</h1>\n' +
			'<p>Welcome to Tertulias site.</p>' +
			'<p>You arrived at this page because you followed a link with a private invitation from a friend of yours to join a Tertulia.</p>' +
			'';
		res.send(body);
    	next();
    });

    return router;
};
