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

	var getUserInfo = function(user, voucher, next) {
		console.log('in getUserInfo');
	    user.getIdentity()
	    .then(function(identity) {
			console.log('in got identity');
	    	var claims = identity.google.claims;
			var myclaims = {
	    		sid: user.id,
	    		email: claims.email_verified == 'true' ? claims.emailaddress : "",
	    		firstName: claims.givenname,
	    		lastName: claims.surname,
	    		alias: email ? email : firstName + lastName,
	    		picture: claims.picture
	    	};
			console.log(myclaims);
	    	next(voucher, {
	    		sid: user.id,
	    		email: claims.email_verified == 'true' ? claims.emailaddress : "",
	    		alias: email ? email : firstName + lastName,
	    		firstName: claims.givenname,
	    		lastName: claims.surname,
	    		picture: claims.picture
	    	});
	    });
	};

	router.post('/', (req, res, next) => {
		console.log('in POST /acceptinvitation');
		if ( ! req.azureMobile.user) {
			console.log('401 - Unauthorized');
			res.status(401);	// 401: Unauthorized
			return next(401);
		}
		var voucher = req.body.voucher;
		getUserInfo(req.azureMobile.user, voucher, function(voucher, userInfo) {
			console.log('in after getinfo');
			sql.connect(util.sqlConfiguration)
			.then(function() {
				console.log('for sql');
				new sql.Request()
				.input('token', sql.NVarChar(36), voucher)
				.input('userSid', sql.NVarChar(40), userInfo.sid)
				.input('alias', sql.NVarChar(20), userInfo.alias)
	    		.input('email', sql.NVarChar(40), userInfo.email)
	    		.input('firstName', sql.NVarChar(40), userInfo.firstName)
	    		.input('lastName', sql.NVarChar(40), userInfo.lastName)
	    		.input('picture', sql.NVarChar(255), userInfo.picture)
				.execute('sp_acceptInvitationToTertulia')
				.then((recordsets) => {
					console.log('recordsets');
					console.log(recordsets);
					if (recordsets['returnValue'] == 0) {
						console.log('in 201 ok');
						res.status(201)	// 201: Created
							.type('application/json')
							.json( { result: 'Ok' } );
						res.end();
						return next();
					} else {
						console.log('in 409 error');
						res.status(409)	// 409: Conflict, 422: Unprocessable Entity (WebDAV; RFC 4918)
							.type('application/json')
							.json( { result: 'Voucher unavailable' } );
						res.end();
						return next();
					}
					return next();
				})
				.catch(function(err) {
					console.log('in post error');
					next(err);
				});
			});
		});

		// sql.connect(util.sqlConfiguration)
		// .then(function() {
		// 	new sql.Request()
		// 	.input('userSid', sql.NVarChar(40), userInfo.sid)
		// 	.input('token', sql.NVarChar(36), voucher)
		// 	.execute('sp_acceptInvitationToTertulia')
		// 	.then((recordsets) => {
		// 		if (recordsets['returnValue'] == 0) {
		// 			console.log('in 201 ok');
		// 			res.status(201)	// 201: Created
		// 				.type('application/json')
		// 				.json( { result: 'Ok' } );
		// 			return next();
		// 		} else {
		// 			console.log('in 409 error');
		// 			res.status(409)	// 409: Conflict, 422: Unprocessable Entity (WebDAV; RFC 4918)
		// 				.type('application/json')
		// 				.json( { result: 'Voucher unavailable' } );
		// 			return next();
		// 		}
		// 		return;
		// 	})
		// 	.catch(function(err) {
		// 		console.log('in post error');
		// 		next(err);
		// 	});
		// });
		return next();
	});

	return router;
};
