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

    router.post('/', authenticate, (req, res, next) => {
		console.log('in POST /acceptinvitation');
    	var userSid = req.azureMobile.user.id;
    	console.log(userSid);
    	var voucher = req.body.voucher;
    	console.log(voucher);
		res.json( {
			"links": [
				{ "rel": "userSid", "method": "voucher", "href": "/ok" }
			]}
		);
    	next();
    });

    return router;
};
