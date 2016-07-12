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
		res.json( {
			"links": [
				{ "rel": "tertulias", "method": "GET", "href": "/tertulias" },
				{ "rel": "registration", "method": "POST", "href": "/me" }
				{ "rel": "profile", "method": "GET", "href": "/me" }
			]}
		);
    	next();
    });

    return router;
};
