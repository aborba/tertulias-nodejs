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
		// var userSid = req.azureMobile.user.id;
		var userSid = req.body.userSid;
		console.log(userSid);
		var voucher = req.body.voucher;
		console.log(voucher);
		res.json( {
			"links": [
				{ "rel": "userSid", "method": "voucher", "href": "/ok" }
			]}
		);
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			.input('userSid', sql.NVarChar(40), userSid)
			.input('token', sql.NVarChar(36), voucher)
			.execute('sp_acceptInvitationToTertulia')
			.then((recordsets) => {
				console.log('recordsets[1]: ', recordsets[1]);
				console.log('recordsets['returnValue']: ', recordsets['returnValue']);
				if (recordsets['returnValue'] == 0) {
					res.status(201)	// 201: Created
						.type('application/json')
						.json( { result: 'Ok' } );
					return next();
				} else {
					res.status(409)	// 409: Conflict, 422: Unprocessable Entity (WebDAV; RFC 4918)
						.type('application/json')
						.json( { result: 'Unavailable' } );
					return next('409');
				}
				next();
			})
			.catch(function(err) {
				next(err);
			});
		});
		next();
	});

	return router;
};
