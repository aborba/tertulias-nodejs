var express			= require('express'),
	bodyparser		= require('body-parser'),
	promises		= require('azure-mobile-apps/src/utilities/promises'),
	logger			= require('azure-mobile-apps/src/logger');

var sql				= require('mssql'),
	util			= require('../util');

module.exports = function (configuration) {
	var router		= express.Router(),
	authenticate	= require('azure-mobile-apps/src/express/middleware/authenticate')(configuration),
	authorize		= require('azure-mobile-apps/src/express/middleware/authorize');

	var completeError = function(err, res) {
		if (err) {
			console.error(err);
			if (res) res.sendStatus(500);
		}
	};

	var getUserInfo = function(user, voucher, continueWith) {
		var HERE = 'getUserInfo';
		user.getIdentity()
		.then( (identity) => {
			var claims = identity.google.claims;
			var email = claims.email_verified == 'true' ? claims.emailaddress : "";
			var firstName = claims.givenname;
			var lastName = claims.givenname;
			var selectedClaims = {
				sid: user.id,
				email: email,
				firstName: firstName,
				lastName: lastName,
				alias: email ? email : firstName + lastName,
				picture: claims.picture
			};
console.log(HERE);
selectedClaims.forEach(item, position) {
	console.log(item);
}
			continueWith(voucher, selectedClaims);
		});
	};

	router.post('/', (req, res, next) => {
		var HERE = '/acceptinvitation';
		console.log('in POST ' + HERE);
		if ( ! req.azureMobile.user) {
			console.log('401 - Unauthorized');
			res.status(401);	// 401: Unauthorized
			next(401);
		}
		var voucher = req.body.voucher;
		getUserInfo(req.azureMobile.user, voucher, (voucher, userInfo) => {
console.log(HERE + ': ' + 'for sql');
			sql.connect(util.sqlConfiguration)
			.then(() => {
console.log(HERE + ': ' + 'sql connected');
				new sql.Request()
				.input('voucher', sql.NVarChar(36), voucher)
				.input('userSid', sql.NVarChar(40), userInfo.sid)
				.input('alias', sql.NVarChar(20), userInfo.alias)
				.input('email', sql.NVarChar(40), userInfo.email)
				.input('firstName', sql.NVarChar(40), userInfo.firstName)
				.input('lastName', sql.NVarChar(40), userInfo.lastName)
				.input('picture', sql.NVarChar(255), userInfo.picture)
				.execute('sp_acceptInvitationToTertulia')
				.then((recordsets) => {
console.log(HERE + ': ' + 'query done');
console.log(recordsets);
					if (recordsets['returnValue'] == 0) {
						console.log('in 201 ok');
						res.status(201).end();	// 201: Created
						return;
					} else {
						console.log('in 422 error');
						res.status(422)	// 409: Conflict, 422: Unprocessable Entity (WebDAV; RFC 4918)
							.type('application/json')
							.json( { result: 'Voucher unavailable' } )
							.end();
						return next('422 - Unprocessable Entity');
					}
				});
			});
		});
	});

	return router;
};
