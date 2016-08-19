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
		console.log('in GET /tertulias');
		res.send( '<HTML>' );
		// res.send( '<HEAD>' );
		// res.send( '</HEAD>' );
		res.send( '<BODY>' );
		res.send( '<H1>Tertulias</H1>' );
		res.send( '<P>Tertulias</P>' );
		res.send( '</BODY>' );
		res.send( '</HTML>' );
    	next();
    });

    return router;
};
