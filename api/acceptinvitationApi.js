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
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			.input('userSid', sql.NVarChar(40), userSid)
			.input('token', sql.NVarChar(36), voucher)
			.execute('sp_acceptInvitationToTertulia')
			.then((recordsets) => {
				if (recordsets['returnValue'] == 0) {
					console.log('in 201 ok');
					res.status(201)	// 201: Created
						.type('application/json')
						.json( { result: 'Ok' } );
					return next();
				} else {
					console.log('in 409 error');
					res.status(409)	// 409: Conflict, 422: Unprocessable Entity (WebDAV; RFC 4918)
						.type('application/json')
						.json( { result: 'Voucher unavailable' } );
					return next();
				}
				return;
			})
			.catch(function(err) {
				console.log('in post error');
				next(err);
			});
		});
		return next();
	});

	return router;
};
