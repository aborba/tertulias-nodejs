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

	var pushMessage = function(tag, message) {
		var notificationHubService = azure.createNotificationHubService('tertulias', 'Endpoint=sb://tertulias.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=Ef9XYWpw3byXXlTPG/HF9E9hoLG+Pc65cySLzrFRvLY=');
		var payload = {data: {message: message } };
		notificationHubService.gcm.send(tag, payload, function(err) {
			if (err) {
				console.log('Error while sending push notification');
				console.log(err);
			} else {
				console.log('Push notification sent successfully');
			}
		});
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
			sql.connect(util.sqlConfiguration).then(function() {
				var request = new sql.Request()
				.input('voucher', sql.VarChar(36), voucher)
				.input('userSid', sql.VarChar(40), userInfo.sid)
				.input('alias', sql.VarChar(20), userInfo.alias.substring(0, 20))
				.input('email', sql.VarChar(40), userInfo.email.substring(0, 40))
				.input('firstName', sql.VarChar(40), userInfo.firstName.substring(0, 40))
				.input('lastName', sql.VarChar(40), userInfo.lastName.substring(0, 40))
				.input('picture', sql.VarChar(255), userInfo.picture.substring(0, 255))
				.output('tertulia', sql.Int);
				request.execute('sp_acceptInvitationToTertulia')
				.then(function(recordsets) {
					if (recordsets['returnValue'] == 0) {
						console.log('in 201 ok');
						console.log(req);
						var tr_id = request.parameters.tertulia.value;
						var tag = 'tertulia_' + tr_id;
						var message = '{action:"subscribe",tertulia:' + 'tr_id' + '}';
						pushMessage(null, message);
						res.status(201).end();	// 201: Created
						return;
					} else {
						console.log('in 422 error');
						console.log(recordsets);
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
