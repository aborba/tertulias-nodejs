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
			var lastName = claims.surname;
			var selectedClaims = {
				sid: user.id,
				email: email,
				firstName: firstName,
				lastName: lastName,
				alias: email ? email : firstName + lastName,
				picture: claims.picture
			};
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
		getUserInfo(req.azureMobile.user, voucher, function(voucher, userInfo) {
console.log(HERE + ': ' + 'for sql');
			sql.connect(util.sqlConfiguration).then(function() {
console.log(HERE + ': ' + 'sql connected');
console.log(HERE + ': ' + voucher);
console.log(HERE + ': ' + userInfo.sid);
console.log(HERE + ': ' + userInfo.alias);
console.log(HERE + ': ' + userInfo.email);
console.log(HERE + ': ' + userInfo.firstName);
console.log(HERE + ': ' + userInfo.lastName);
console.log(HERE + ': ' + userInfo.picture);
				new sql.Request()
				.input('voucher', sql.VarChar(36), voucher)
				.input('userSid', sql.VarChar(40), userInfo.sid)
				.input('alias', sql.VarChar(20), userInfo.alias.substring(0, 20))
				.input('email', sql.VarChar(40), userInfo.email.substring(0, 40))
				.input('firstName', sql.VarChar(40), userInfo.firstName.substring(0, 40))
				.input('lastName', sql.VarChar(40), userInfo.lastName.substring(0, 40))
				.input('picture', sql.VarChar(255), userInfo.picture.substring(0, 255))
				.execute('sp_acceptInvitationToTertulia')
				.then(function(recordsets) {
console.log('query done');
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
				}).catch(function(err) {
					console.log('SQL Stored procedure Error');
					res.status(500)
					return next(err);
				});
			}).catch(function(err) {
				console.log('SQL Connection Error');
				res.status(500);
				return next(err);
			});
		});
	});

	return router;
};
