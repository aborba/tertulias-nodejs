var util = require('../util');
var sql = require('mssql');

var querySelectId = 'SELECT us_id FROM Users WHERE us_sid=@sid;';
var queryInsertSid = 'INSERT INTO Users (us_sid, us_alias, us_email, us_firstName, us_lastName, us_picture) values (@sid, @alias, @email, @firstName, @lastName, @picture);';

var tranDone = false;

var api = {
	get: function (req, res, next) {
		var HERE = '/me';
		console.log('in GET ' + HERE);
		var route = '/me';
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			.input('sid', sql.NVarChar(40), req.azureMobile.user.id)
			.query('SELECT' +
					' us_alias AS alias,' +
					' us_firstName AS firstName,' +
					' us_lastName AS lastName,' +
					' us_email AS email,' +
					' us_picture AS picture', +
					' us_myKey AS myKey' +
				' FROM Users' +
				' WHERE us_sid = @sid')
			.then(function(recordset) {
				var links = '[ ' +
						'{ "rel": "self", "method": "GET", "href": "/me" }, ' +
						'{ "rel": "update", "method": "PATCH", "href": "/me" }, ' +
						'{ "rel": "delete", "method": "DELETE", "href": "/me" } ' +
					']';
				res.type('application/json');
				var results = {};
				console.log(recordset);
				results['me'] = recordset[0];
				results['links'] = JSON.parse(links);
				res.status(200)
					.type('application/json')
					.json(results)
					.end();
				return next();
			})
			.catch(function(err) {
				return next(err);
			});
		});
	},

	post: function (req, res, next) {
		var HERE = '/me';
		console.log('in POST ' + HERE);
		var conn = new sql.Connection(util.sqlConfiguration);
		conn.connect(function(err) {
			if (err) { completeError(err, res); return; }
			var tran = new sql.Transaction(conn);
			tran.begin(function(err) {
				if (err) { rollback500(err, res, tran); return; }
				var sqlRequest = new sql.Request(tran);
				var psSelectId = new sql.PreparedStatement(conn);
				psSelectId.input('sid', sql.NVarChar);
				psSelectId.prepare(querySelectId, function(err) {
					if (err) { rollback500(err, res, tran); return; }
					psSelectId.execute({ sid: req.azureMobile.user.id }, 
						function(err, recordset, affected) {
							if (err) { rollback500(err, res, tran); return; }
							psSelectId.unprepare();
							if (typeof recordset != 'undefined' && recordset[0] != null) {
console.log('User registered; Returning.');
								rollback200(res, tran);
								next();
							} else {
console.log('User not registered; Registering.');
console.log(req.azureMobile.user);
								userName(req.azureMobile.user, function(userInfo) {
									var psInsertSid = new sql.PreparedStatement(conn);
									psInsertSid.input('sid', sql.NVarChar);
									psInsertSid.input('alias', sql.NVarChar);
									psInsertSid.input('email', sql.NVarChar);
									psInsertSid.input('firstName', sql.NVarChar);
									psInsertSid.input('lastName', sql.NVarChar);
									psInsertSid.input('picture', sql.NVarChar);
									psInsertSid.prepare(queryInsertSid, function(err) {
										if (err) { rollback500(err, res, tran); return; }
										psInsertSid.execute({
												sid: req.azureMobile.user.id,
												alias: userInfo.alias ? userInfo.alias : userInfo.email.substring(0, 20),
												email: userInfo.email,
												firstName: userInfo.firstName,
												lastName: userInfo.lastName,
												picture: userInfo.picture
											}, function(err, recordset, affected) {
												if (err) { rollback500(err, res, tran); return; }
												commit200(res, tran);
												psInsertSid.unprepare();
												next();
											}
										);
									});
								});
							}
						}
					);
				});
			});
		});
	}
};

api.access = 'authenticated';

var userName = function(user, next) {
	user.getIdentity().then(function(identity){
		var claims = identity.google.claims;
		next({
			alias: "",
			email: claims.email_verified ? claims.emailaddress : "",
			firstName: claims.givenname,
			lastName: claims.surname,
			picture: claims.picture
		});
	});
};

var completeError = function(err, res) {
	if (err) {
		console.log(err);
		if (res) res.sendStatus(500);
	}
}

var completetran = function(err, data) {
	if (err) {
		console.log(err);
		return;
	}
	if (!data) {
		console.log('no data');
		return;
	}
	if (!data.tranDone) {
		data.tranDone = true;
		if (data.action && util.isFunction(data.action))
			data.action();
		if (data.res && data.sendStatus && util.isFunction(data.res.sendStatus)) {
			console.log('sending status ' + data.sendStatus);
			data.res.sendStatus(data.sendStatus);
		}
	}
}

var rollback500 = function(err, res, tran) {
	completetran(err, {
		tranDone: tranDone, 
		action: tran.rollback,
		res: res,
		sendStatus: 500
	});
}

var rollback200 = function(res, tran) {
	completetran(undefined, {
		tranDone: tranDone, 
		action: tran.rollback,
		res: res,
		sendStatus: 200
	});
}

var commit200 = function(res, tran) {
	completetran(undefined, {
		tranDone: tranDone, 
		action: tran.commit,
		res: res,
		sendStatus: 200
	});
}

module.exports = api;
