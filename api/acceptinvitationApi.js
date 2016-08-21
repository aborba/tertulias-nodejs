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

    router.post('/', (req, res, next) => {
    	var userSid = req.azureMobile.user.id;
    	console.log(userSid);
    	var voucher = req.body.voucher;
    	console.log(voucher);
		res.json( {
			"links": [
				{ "rel": userSid, "method": voucher, "href": "/ok" }
			]}
		);
    	next();
    });

    return router;
};
