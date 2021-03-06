var express      = require('express'),
	bodyParser   = require('body-parser');

var	authenticate = require('azure-mobile-apps/src/express/middleware/authenticate'),
	authorize    = require('azure-mobile-apps/src/express/middleware/authorize');

var sql          = require('mssql'),
	util         = require('../util');

/* { 'SQL types': {
	'String': 'sql.NVarChar', 'Number': 'sql.Int', 'Boolean': 'sql.Bit', 'Date': 'sql.DateTime', 'Buffer': 'sql.VarBinary', 'sql.Table': 'sql.TVP'
} } */
module.exports = function (configuration) {

	var router = express.Router(),
		azure = require('azure'),
		promises = require('azure-mobile-apps/src/utilities/promises'),
		logger = require('azure-mobile-apps/src/logger');

	var pushMessage = (tag, message) => {
		var notificationHubService = azure.createNotificationHubService('tertulias', 'Endpoint=sb://tertulias.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=Ef9XYWpw3byXXlTPG/HF9E9hoLG+Pc65cySLzrFRvLY=');
		var payload = { data: { message: message } };
		notificationHubService.gcm.send(tag, payload, function(err) {
			if (err) {
				console.log('Error while sending push notification');
				console.log(err);
			} else {
				var log_msg = 'Push notification sent successfully' + tag ? ' to tag ' + tag : '';
				console.log(log_msg);
				console.log(payload);
			}
		});
	};

	var getPushTag = (tr_id) => {
		return 'tertulia_' + tr_id;
	}

	// '{ "rel": "self", "method": "GET", "href": "/tertulias" }, ' +
	router.get('/', (req, res, next) => {
		console.log('in GET /api/tertulias');
		var route = '/tertulias';
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.query('SELECT' +
					' tr_id         AS id,' +
					' tr_name       AS name,' +
					' tr_subject    AS subject,' +
					' ev_targetdate AS nextEventDate,' +
					' lo_name       AS nextEventLocation,' +
					' no_count      AS messages,' +
					' nv_name       AS role ' +
				' FROM Tertulias' +
					' INNER JOIN Members ON mb_tertulia = tr_id' +
					' INNER JOIN Enumvalues ON mb_role = nv_id' +
					' INNER JOIN Users ON mb_user = us_id' +
					' LEFT JOIN' +
						' (SELECT * FROM' +
							' (SELECT RANK() OVER(PARTITION BY ev_tertulia ORDER BY ev_targetdate DESC) AS "rank", * FROM Events' +
						' INNER JOIN Locations on ev_location = lo_id WHERE ev_targetdate > GETDATE()) AS a WHERE a.rank = 1) AS b' +
							' ON ev_tertulia = tr_id' +
						' LEFT JOIN (SELECT no_tertulia, count(*) AS no_count FROM Notifications WHERE no_id NOT IN' +
							' (SELECT no_id FROM Notifications INNER JOIN Readnotifications ON rn_notification = no_id' +
						' INNER JOIN Users ON rn_user = us_id WHERE us_sid = @userSid)' +
				' GROUP BY no_tertulia) AS c ON no_tertulia = tr_id' +
				' WHERE tr_is_cancelled = 0 AND us_sid = @userSid')
			.then(function(recordset) {
				var links = '[ ' +
						'{ "rel": "self", "method": "GET", "href": "' + route + '" }, ' +
						'{ "rel": "create_weekly", "method": "POST", "href": "' + route + '/weekly" }, ' +
						'{ "rel": "create_monthly", "method": "POST", "href": "' + route + '/monthly" }, ' +
						'{ "rel": "create_monthlyw", "method": "POST", "href": "' + route + '/monthlyw" }, ' +
						'{ "rel": "create_yearly", "method": "POST", "href": "' + route + '/yearly" }, ' +
						'{ "rel": "create_yearlyw", "method": "POST", "href": "' + route + '/yearlyw" }, ' +
						'{ "rel": "searchPublic", "method": "GET", "href": "' + route + '/publicsearch" } ' +
					']';
				var itemLinks = '[ ' +
						'{ "rel": "self", "method": "GET", "href": "' + route + '/:id" }, ' +
						'{ "rel": "update", "method": "PATCH", "href": "' + route + '/:id" }, ' +
						'{ "rel": "delete", "method": "DELETE", "href": "' + route + '/:id" }, ' +
						'{ "rel": "unsubscribe", "method": "DELETE", "href": "' + route + '/:id/unsubscribe" }, ' +
						'{ "rel": "members", "method": "GET", "href": "' + route + '/:id/members" }, ' +
						'{ "rel": "members_count", "method": "GET", "href": "' + route + '/:id/members/count" }, ' +
						'{ "rel": "get_messages", "method": "GET", "href": "' + route + '/:id/messages" }, ' +
						'{ "rel": "post_messages", "method": "POST", "href": "' + route + '/:id/messages" }, ' +
						'{ "rel": "event", "method": "POST", "href": "' + route + '/:id/event" } ' +
					']';
				res.type('application/json');
				recordset.forEach(function(elem) {
					elem['links'] = JSON.parse(itemLinks.replace(/:id/g, elem.id));
				});
				var results = {};
				results['tertulias'] = recordset;
				results['links'] = JSON.parse(links);
				res.json(results);
				res.sendStatus(200);
				return next();
			})
		});
	});

	router.get('/voucherinfo/:voucher', (req, res, next) => {
		console.log('in GET /api/voucherinfo/:voucher');
		var voucher = req.params.voucher;
		if (!req.azureMobile.user) {
			res.send(401); // 401: Unauthorized
			return next();
		}
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			.input('voucher', sql.NVarChar(36), voucher)
			.query('SELECT' +
					' tr_name AS name,' +
					' tr_subject AS subject' +
				' FROM Invitations' +
					' INNER JOIN Tertulias ON in_tertulia = tr_id' +
				' WHERE tr_is_cancelled = 0' +
					' AND in_is_acknowledged = 0 AND in_key = @voucher')
			.then(function(recordset) {
				res.type('application/json');
				var results = {};
				results['tertulias'] = recordset;
				res.json(results);
				res.sendStatus(200);
				return next();
			});
		});
	});

	router.get('/publicSearch', (req, res, next) => {
		var HERE = '/tertulias/publicsearch';
		console.log('in GET ' + HERE);
		var route = '/tertulias';
		var point = 'POINT(' + req.query.latitude + ' ' + req.query.longitude + ')';
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.input('query', sql.NVarChar, '%' + req.query.query + '%')
			.input('latitude', sql.NVarChar, req.query.latitude)
			.input('longitude', sql.NVarChar, req.query.longitude)
			.input('point', sql.NVarChar, point)
			.query('SELECT TOP 25' +
					' tr_id AS id,' +
					' tr_name AS name,' +
					' tr_subject AS subject,' +
					' lo_name AS location' +
				' FROM Tertulias' +
					' INNER JOIN Locations ON tr_location = lo_id' +
				' WHERE (tr_name LIKE @query OR tr_subject LIKE @query OR lo_name LIKE @query)' +
					' AND tr_is_cancelled = 0 AND tr_is_private = 0' +
					' AND tr_id NOT IN' +
						' (SELECT mb_tertulia FROM Tertulias' +
						' INNER JOIN Members ON mb_tertulia = tr_id' +
						' INNER JOIN Users ON mb_user = us_id WHERE us_sid = @userSid)' +
				' ORDER BY lo_geography.STDistance( @point )')
				// ' ORDER BY lo_geography.STDistance(\'POINT( @latitude @longitude )\')')
				// ' ORDER BY lo_geography.STDistance(\'POINT( 38.11 -9.1123113 )\')')
			.then(function(recordset) {
				var links = '[ { "rel": "self", "method": "GET", "href": "' + route + '/publicSearch" } ]';
				var itemLinks = '[ ' +
						'{ "rel": "self", "method": "GET", "href": "' + route + '/:id" }, ' +
						'{ "rel": "subscribe", "method": "POST", "href": "' + route + '/:id/subscribe" }' +
					']';
				res.type('application/json');
				recordset.forEach(function(elem) {
					elem['links'] = JSON.parse(itemLinks.replace(/:id/g, elem.id));
				});
				var results = {};
				results['tertulias'] = recordset;
				results['links'] = JSON.parse(links);
				res.json(results);
				res.sendStatus(200);
				return next();
			})
		});
	});

	router.post('/weekly', (req, res, next) => {
		console.log('in POST /tertulias/weekly');
		console.log(req.body);
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.input('tertuliaName', sql.NVarChar(40), req.body.tertulia_name)
			.input('tertuliaSubject', sql.NVarChar(80), req.body.tertulia_subject)
			.input('tertuliaIsPrivate', sql.Int, req.body.tertulia_isprivate ? 1 : 0)
			.input('locationName', sql.NVarChar(40), req.body.location_name)
			.input('locationAddress', sql.NVarChar(80), req.body.location_address)
			.input('locationZip', sql.NVarChar(40), req.body.location_zip)
			.input('locationCity', sql.NVarChar(40), req.body.location_city)
			.input('locationCountry', sql.NVarChar(40), req.body.location_country)
			.input('locationLatitude', sql.NVarChar(12), req.body.location_latitude)
			.input('locationLongitude', sql.NVarChar(12), req.body.location_longitude)
			.input('scheduleWeekDay', sql.Int, req.body.schedule_weekday)
			.input('scheduleSkip', sql.Int, req.body.schedule_skip)
			.execute('sp_insertTertulia_Weekly_sid')
			.then((recordsets) => {
				console.log(recordsets);
				if (recordsets.length == 0) {
					res.status(201)	// 201: Created
						.type('application/json')
						.json( { result: 'Ok' } );
					return next();
				} else {
					res.status(409)	// 409: Conflict, 422: Unprocessable Entity (WebDAV; RFC 4918)
						.type('application/json')
						.json( { result: 'Duplicate' } );
					return next('409');
				}
				return next();
			})
			.catch(function(err) {
				return next(err);
			});
		})
		.catch(function(err) {
			return next(err);
		});
	});

	router.post('/monthly', (req, res, next) => {
		console.log('in POST /tertulias/monthly');
		console.log(req.body);
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.input('tertuliaName', sql.NVarChar(40), req.body.tertulia_name)
			.input('tertuliaSubject', sql.NVarChar(80), req.body.tertulia_subject)
			.input('tertuliaIsPrivate', sql.Int, req.body.tertulia_isprivate ? 1 : 0)
			.input('locationName', sql.NVarChar(40), req.body.location_name)
			.input('locationAddress', sql.NVarChar(80), req.body.location_address)
			.input('locationZip', sql.NVarChar(40), req.body.location_zip)
			.input('locationCity', sql.NVarChar(40), req.body.location_city)
			.input('locationCountry', sql.NVarChar(40), req.body.location_country)
			.input('locationLatitude', sql.NVarChar(12), req.body.location_latitude)
			.input('locationLongitude', sql.NVarChar(12), req.body.location_longitude)
			.input('scheduleDayNr', sql.Int, req.body.schedule_daynr)
			.input('scheduleIsFromStart', sql.BIT, req.body.schedule_isfromstart ? 1 : 0)
			.input('scheduleSkip', sql.Int, req.body.schedule_skip)
			.execute('sp_insertTertulia_Monthly_sid')
			.then((recordsets) => {
				if (recordsets.length == 0) {
					res.status(201)	// 201: Created
						.type('application/json')
						.json( { result: 'Ok' } );
					return next();
				} else {
					res.status(409)	// 409: Conflict, 422: Unprocessable Entity (WebDAV; RFC 4918)
						.type('application/json')
						.json( { result: 'Duplicate' } );
					return next('409');
				}
				return next();
			})
			.catch(function(err) {
				return next(err);
			});
		})
		.catch(function(err) {
			return next(err);
		});
	});

	router.post('/monthlyw', (req, res, next) => {
		console.log('in POST /tertulias/monthlyw');
		console.log(req.body);
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.input('tertuliaName', sql.NVarChar(40), req.body.tertulia_name)
			.input('tertuliaSubject', sql.NVarChar(80), req.body.tertulia_subject)
			.input('tertuliaIsPrivate', sql.Int, req.body.tertulia_isprivate ? 1 : 0)
			.input('locationName', sql.NVarChar(40), req.body.location_name)
			.input('locationAddress', sql.NVarChar(80), req.body.location_address)
			.input('locationZip', sql.NVarChar(40), req.body.location_zip)
			.input('locationCity', sql.NVarChar(40), req.body.location_city)
			.input('locationCountry', sql.NVarChar(40), req.body.location_country)
			.input('locationLatitude', sql.NVarChar(12), req.body.location_latitude)
			.input('locationLongitude', sql.NVarChar(12), req.body.location_longitude)
			.input('scheduleWeekDay', sql.NVarChar(20), req.body.schedule_weekday)
			.input('scheduleWeekNr', sql.Int, req.body.schedule_weeknr)
			.input('scheduleIsFromStart', sql.BIT, req.body.schedule_isfromstart ? 1 : 0)
			.input('scheduleSkip', sql.Int, req.body.schedule_skip)
			.execute('sp_insertTertulia_MonthlyW_sid')
			.then((recordsets) => {
				if (recordsets.length == 0) {
					res.status(201)	// 201: Created
						.type('application/json')
						.json( { result: 'Ok' } );
					return next();
				} else {
					console.log(recordsets);
					res.status(409)	// 409: Conflict, 422: Unprocessable Entity (WebDAV; RFC 4918)
						.type('application/json')
						.json( { result: 'Duplicate' } );
					return next('409');
				}
				return next();
			})
			.catch(function(err) {
				return next(err);
			});
		})
		.catch(function(err) {
			return next(err);
		});
	});

	// '{ "rel": "self", "method": "GET", "href": "'/api/tertulias/:id" }, ' +
	router.get('/:tr_id', (req, res, next) => {
		var HERE = '/tertulias/:tr_id';
		console.log('in GET ' + HERE);
		var tr_id = req.params.tr_id;
		var route = '/tertulias/' + tr_id;
		if (isNaN(tr_id))
			return next();
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			.input('tertulia', sql.Int, tr_id)
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.query('SELECT' +
					' tr_id' +                    ' AS tertulia_id,' +          // Tertulia
					' tr_name' +                  ' AS tertulia_name,' +
					' tr_subject' +               ' AS tertulia_subject,' +
					' tr_is_private' +            ' AS tertulia_isprivate, ' +  // Privacy
					' mb_role' +                  ' AS role_id,' +             // Role
					' _Member.nv_name' +          ' AS role_name,' +
					' tr_location' +              ' AS location_id,' +         // Location
					' lo_name' +                  ' AS location_name,' +
					' lo_address' +               ' AS location_address,' +
					' lo_zip' +                   ' AS location_zip,' +
					' lo_city' +                  ' AS location_city,' +
					' lo_country' +               ' AS location_country,' +
					' lo_latitude' +              ' AS location_latitude,' +
					' lo_longitude' +             ' AS location_longitude,' +
					' tr_schedule' +              ' AS schedule_id,' +        // Schedule
					' _Schedule.nv_name' +        ' AS schedule_name,' +
					' _Schedule.nv_description' + ' AS schedule_description' +
				' FROM Tertulias' +
					' INNER JOIN Locations ON tr_location = lo_id' +
					' INNER JOIN Schedules ON tr_schedule = sc_id' +
					' LEFT JOIN Members ON mb_tertulia = tr_id' +
					' LEFT JOIN Users ON mb_user = us_id' +
					' LEFT JOIN EnumValues AS _Member ON mb_role = _Member.nv_id' +
					' INNER JOIN EnumValues AS _Schedule ON sc_type = _Schedule.nv_id' +
				' WHERE tr_is_cancelled = 0 AND (us_sid = @userSid OR (tr_is_private = 0' +
					' AND tr_id NOT IN' +
						' (SELECT tr_id' +
						' FROM Tertulias' +
							' INNER JOIN Members ON mb_tertulia = tr_id' +
							' INNER JOIN Users ON mb_user = us_id' +
						' WHERE us_sid = @userSid)))' +
					' AND tr_id = @tertulia')
			.then(function(recordset) {
				var results = {};
				results['tertulia'] = recordset[0];
				var links = '[ ' +
						'{ "rel": "self", "method": "GET", "href": "' + route + '" }, ' +
						'{ "rel": "update", "method": "PATCH", "href": "' + route + '" }, ' +
						'{ "rel": "delete", "method": "DELETE", "href": "' + route + '" }, ' +
						'{ "rel": "members", "method": "GET", "href": "' + route + '/members" }, ' +
						'{ "rel": "members_count", "method": "GET", "href": "' + route + '/members/count" }, ' +
						'{ "rel": "get_messages", "method": "GET", "href": "' + route + '/messages" }, ' +
						'{ "rel": "post_messages", "method": "POST", "href": "' + route + '/messages" }, ' +
						'{ "rel": "subscribe", "method": "POST", "href": "' + route + '/subscribe" }, ' +
						'{ "rel": "unsubscribe", "method": "DELETE", "href": "' + route + '/unsubscribe" } ' +
					']';
				results['links'] = JSON.parse(links);
				req.results = results;
				switch(results['tertulia'].schedule_name.toUpperCase()) {
					case 'WEEKLY':
						console.log('in weekly');
						sql.connect(util.sqlConfiguration)
						.then(function() {
							new sql.Request()
							.input('schedule', sql.Int, recordset[0].schedule_id)
							// .input('tertulia', sql.Int, recordset[0].tertulia_id)
							// .input('sid', sql.NVarChar(40), req.azureMobile.user.id)
							.query('SELECT' +
									' wk_id' +    ' AS schedule_id,' +
									' nv_value' + ' AS schedule_weekday,' +
									' wk_skip' +  ' AS schedule_skip' +
								' FROM Weekly' +
									' INNER JOIN Schedules ON wk_schedule = sc_id' +
									' INNER JOIN Tertulias ON tr_schedule = sc_id' +
									' INNER JOIN EnumValues ON wk_dow = nv_id' +
								' WHERE tr_schedule = @schedule')
							.then(function(recordset) {
								results['weekly'] = recordset[0];
								res.type('application/json');
								res.json(results);
								res.sendStatus(200);
								return next();
							})
						});
						break;
					case 'MONTHLYD':
						console.log('in monthly');
						sql.connect(util.sqlConfiguration)
						.then(function() {
							new sql.Request()
							.input('schedule', sql.Int, recordset[0].schedule_id)
							// .input('tertulia', sql.Int, recordset[0].tertulia_id)
							// .input('sid', sql.NVarChar(40), req.azureMobile.user.id)
							.query('SELECT' +
									' md_id' +           ' AS schedule_id,' +
									' md_dom' +          ' AS schedule_daynr,' +
									' md_is_fromstart' + ' AS schedule_isfromstart,' +
									' md_skip' +         ' AS schedule_skip' +
								' FROM MonthlyD' +
									' INNER JOIN Schedules ON md_schedule = sc_id' +
									' INNER JOIN Tertulias ON tr_schedule = sc_id' +
								' WHERE tr_schedule = @schedule')
							.then(function(recordset) {
								results['monthly'] = recordset[0];
								res.type('application/json');
								res.json(results);
								res.sendStatus(200);
								return next();
							})
						});
						break;
					case 'MONTHLYW':
						console.log('in monthlyw');
						sql.connect(util.sqlConfiguration)
						.then(function() {
							new sql.Request()
							.input('schedule', sql.Int, recordset[0].schedule_id)
							//.input('tertulia', sql.Int, recordset[0].tertulia_id)
							// .input('sid', sql.NVarChar(40), req.azureMobile.user.id)
							.query('SELECT' +
									' mw_id' +           ' AS schedule_id,' +
									' nv_value' +        ' AS schedule_weekday,' +
									' mw_weeknr' +       ' AS schedule_weeknr,' +
									' mw_is_fromstart' + ' AS schedule_isfromstart,' +
									' mw_skip' +         ' AS schedule_skip' +
								' FROM MonthlyW' +
									' INNER JOIN Schedules ON mw_schedule = sc_id' +
									' INNER JOIN Tertulias ON tr_schedule = sc_id' +
									' INNER JOIN EnumValues ON mw_dow = nv_id' +
								' WHERE tr_schedule = @schedule')
							.then(function(recordset) {
								results['monthlyw'] = recordset[0];
								res.type('application/json');
								res.json(results);
								res.sendStatus(200);
								return next();
							})
						});
						break;
					default:
						res.status(404)	// 404: NOT Found
						.type('application/json')
						.json( { result: 'Not Found' } );
						return next('404');
				}
			})
		});
	});

	// '{ "rel": "update", "method": "PATCH", "href": "/tertulias/:id" }, '
	router.patch('/:tr_id', (req, res, next) => {
		var HERE = '/tertulias/:tr_id';
		console.log('in PATCH ' + HERE);
		var tr_id = req.params.tr_id;
		if (isNaN(tr_id))
			return next();
		var myKey = req.body.myKey;
		console.log(tr_id);
		console.log(req.azureMobile.user.id);
		console.log(req.body);
		switch (req.body.schedule_name.toUpperCase()) {
			case "WEEKLY":
				console.log("in WEEKLY");
				sql.connect(util.sqlConfiguration)
				.then(function() {
					new sql.Request()
					.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
					.input('tertuliaId', sql.Int, tr_id)
					.input('tertuliaName', sql.NVarChar(40), req.body.tertulia_name)
					.input('tertuliaSubject', sql.NVarChar(80), req.body.tertulia_subject)
					.input('tertuliaIsPrivate', sql.Int, req.body.tertulia_isprivate ? 1 : 0)
					.input('locationName', sql.NVarChar(40), req.body.location_name)
					.input('locationAddress', sql.NVarChar(80), req.body.location_address)
					.input('locationZip', sql.NVarChar(40), req.body.location_zip)
					.input('locationCity', sql.NVarChar(40), req.body.location_city)
					.input('locationCountry', sql.NVarChar(40), req.body.location_country)
					.input('locationLatitude', sql.NVarChar(12), req.body.location_latitude)
					.input('locationLongitude', sql.NVarChar(12), req.body.location_longitude)
					.input('scheduleWeekDay', sql.Int, req.body.schedule_weekday)
					.input('scheduleSkip', sql.Int, req.body.schedule_skip)
					.execute('sp_updateTertulia_Weekly_sid')
					.then((recordsets) => {
						if (recordsets.length == 0) {
							console.log("WEEKLY updated");
							var tag = getPushTag(tr_id);
							var message = '{action:"update",tertulia:' + tr_id + ',myKey:' + myKey + '}';
							pushMessage(tag, message);
							res.status(201)	// 201: Created
								.type('application/json')
								.json( { result: 'Ok' } );
							return next();
						} else {
							console.log("WEEKLY update failed");
							res.status(422)	// 422: Unprocessable Entity, 409: Conflict (WebDAV; RFC 4918)
								.type('application/json')
								.json( { result: 'Duplicate' } );
							return next('422');
						}
						return next();
					})
					.catch(function(err) {
						console.log("WEEKLY error");
						return next(err);
					});
				})
				.catch(function(err) {
					return next(err);
				});
				break;
			case "MONTHLYD":
				console.log("in MONTHLYD");
				sql.connect(util.sqlConfiguration)
				.then(function() {
					new sql.Request()
					.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
					.input('tertuliaId', sql.Int, tr_id)
					.input('tertuliaName', sql.NVarChar(40), req.body.tertulia_name)
					.input('tertuliaSubject', sql.NVarChar(80), req.body.tertulia_subject)
					.input('tertuliaIsPrivate', sql.Int, req.body.tertulia_isprivate ? 1 : 0)
					.input('locationName', sql.NVarChar(40), req.body.location_name)
					.input('locationAddress', sql.NVarChar(80), req.body.location_address)
					.input('locationZip', sql.NVarChar(40), req.body.location_zip)
					.input('locationCity', sql.NVarChar(40), req.body.location_city)
					.input('locationCountry', sql.NVarChar(40), req.body.location_country)
					.input('locationLatitude', sql.NVarChar(12), req.body.location_latitude)
					.input('locationLongitude', sql.NVarChar(12), req.body.location_longitude)
					.input('scheduleDayNr', sql.Int, req.body.schedule_daynr)
					.input('scheduleIsFromStart', sql.BIT, req.body.schedule_isfromstart ? 1 : 0)
					.input('scheduleSkip', sql.Int, req.body.schedule_skip)
					.execute('sp_updateTertulia_Monthly_sid')
					.then((recordsets) => {
						if (recordsets.length == 0) {
							console.log("MONTHLYD updated");
							var tag = getPushTag(tr_id);
							var message = '{action:"update",tertulia:' + tr_id + ',myKey:' + myKey + '}';
							pushMessage(tag, message);
							res.status(201)	// 201: Created
								.type('application/json')
								.json( { result: 'Ok' } );
							return next();
						} else {
							console.log("MONTHLYD update failed");
							res.status(409)	// 409: Conflict, 422: Unprocessable Entity (WebDAV; RFC 4918)
								.type('application/json')
								.json( { result: 'Duplicate' } );
							return next('409');
						}
						return next();
					})
					.catch(function(err) {
						console.log("MONTHLYD error");
						return next(err);
					});
				})
				.catch(function(err) {
					return next(err);
				});
				break;
			case "MONTHLYW":
				console.log("in MONTHLYW");
				sql.connect(util.sqlConfiguration)
				.then(function() {
					new sql.Request()
					.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
					.input('tertuliaId', sql.Int, tr_id)
					.input('tertuliaName', sql.NVarChar(40), req.body.tertulia_name)
					.input('tertuliaSubject', sql.NVarChar(80), req.body.tertulia_subject)
					.input('tertuliaIsPrivate', sql.Int, req.body.tertulia_isprivate ? 1 : 0)
					.input('locationName', sql.NVarChar(40), req.body.location_name)
					.input('locationAddress', sql.NVarChar(80), req.body.location_address)
					.input('locationZip', sql.NVarChar(40), req.body.location_zip)
					.input('locationCity', sql.NVarChar(40), req.body.location_city)
					.input('locationCountry', sql.NVarChar(40), req.body.location_country)
					.input('locationLatitude', sql.NVarChar(12), req.body.location_latitude)
					.input('locationLongitude', sql.NVarChar(12), req.body.location_longitude)
					.input('scheduleWeekDay', sql.Int, req.body.schedule_weekday)
					.input('scheduleWeekNr', sql.Int, req.body.schedule_weeknr)
					.input('scheduleIsFromStart', sql.BIT, req.body.schedule_isfromstart ? 1 : 0)
					.input('scheduleSkip', sql.Int, req.body.schedule_skip)
					.execute('sp_updateTertulia_MonthlyW_sid')
					.then((recordsets) => {
						if (recordsets.length == 0) {
							console.log("MONTHLYW updated");
							var tag = getPushTag(tr_id);
							var message = '{action:"update",tertulia:' + tr_id + ',myKey:' + myKey + '}';
							pushMessage(tag, message);
							res.status(201)	// 201: Created
								.type('application/json')
								.json( { result: 'Ok' } );
							return next();
						} else {
							console.log("MONTHLYW update failed");
							res.status(409)	// 409: Conflict, 422: Unprocessable Entity (WebDAV; RFC 4918)
								.type('application/json')
								.json( { result: 'Duplicate' } );
							return next('409');
						}
						return next();
					})
					.catch(function(err) {
						console.log("MONTHLYW error");
						return next(err);
					});
				})
				.catch(function(err) {
					return next(err);
				});
				break;
			case "YEARLY":
				console.log("in YEARLY");
				break;
			case "YEARLYW":
				console.log("in YEARLYW");
				break;
		}
	});

	router.get('/:tr_id/members', (req, res, next) => {
		var HERE = '/tertulias/:tr_id/members';
		console.log('in GET ' + HERE);
		var tr_id = req.params.tr_id;
		var tertulia = '/tertulias/' + tr_id;
		var route = tertulia + '/members';
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.input('tertulia', sql.Int, tr_id)
			.execute('sp_getTertuliaMembers')
			.then(function(recordset) {
				var links = '[ ' +
					'{ "rel": "self", "method": "GET", "href": "' + route + '" }, ' +
					'{ "rel": "create_vouchers", "method": "POST", "href": "' + route + '/voucher" } ' +
				']';
				var itemLinks = '[ ' +
					'{ "rel": "self", "method": "GET", "href": "' + route + '/:id" }, ' +
					'{ "rel": "edit_member", "method": "PATCH", "href": "' + route + '/:id/edit" }' +
				']';
				res.type('application/json');
				var results = {};
				if (recordset[0]) {
					recordset[0].forEach(function(elem) {
						elem['links'] = JSON.parse(itemLinks.replace(/:id/g, elem.id));
					});
					results['members'] = recordset[0];
				}
				results['links'] = JSON.parse(links);
				res.json(results);
				res.status(200);
				return next();
			}).catch(function(err) {
				console.log('SQL Query processing Error');
				res.status(500)
				return next(err);
			});
		}).catch(function(err) {
			console.log('SQL Connection Error');
			res.status(500);
			return next(err);
		});
	});

	router.get('/:tr_id/members/count', (req, res, next) => {
		var HERE = '/tertulias/:tr_id/members/count';
		console.log('in GET ' + HERE);
		var tr_id = req.params.tr_id;
		var tertulia = '/tertulias/' + tr_id;
		var route = tertulia + '/members/count';
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			// .input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.input('tertulia', sql.Int, tr_id)
			.query('SELECT COUNT(*) AS total' +
				' FROM Members' +
					' INNER JOIN Tertulias ON mb_tertulia = tr_id' +
				' WHERE tr_is_cancelled = 0' +
					' AND mb_tertulia = @tertulia')
			.then(function(recordset) {
				res.type('application/json');
				var results = {};
				if (recordset[0])
					results['totals'] = recordset[0];
				res.json(results);
				res.status(200);
				return next();
			}).catch(function(err) {
				console.log('SQL Query processing Error');
				res.status(500)
				return next(err);
			});
		}).catch(function(err) {
			console.log('SQL Connection Error');
			res.status(500);
			return next(err);
		});
	});


	router.post('/:tr_id/members/voucher', (req, res, next) => {
		console.log('in POST /tertulias/:tr_id/members/voucher');
		var tr_id = req.params.tr_id;
		sql.connect(util.sqlConfiguration)
		.then(function() {
			var request = new sql.Request()
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.input('tertulia', sql.Int, tr_id)
			.input('vouchers_count', sql.Int, req.body.count)
			.output('vouchers_batch', sql.NVarChar(36));
			request.execute('sp_createInvitationVouchers')
			.then(function(recordsets) {
				console.log(request.parameters.vouchers_batch.value);
				var route = '/tertulias/' + tr_id + '/members/voucher';
				console.log(route);
				var batch = request.parameters.vouchers_batch.value;
				console.log(batch);
				var links = '[ ' +
					// '{ "rel": "self", "method": "GET", "href": "' + route + '" }, ' +
					'{ "rel": "get_vouchers", "method": "GET", "href": "' + route + '/' + batch + '" } ' +
				']';
				console.log(links);
				var results = {};
				results['vouchers_batch'] = batch;
				results['links'] = JSON.parse(links);
				console.log(results);
				res.type('application/json');
				res.json(results);
				res.sendStatus(200);
				return next();
			});
		})
		.catch(function(err) {
			return next(err);
		});
	});

	router.get('/:tr_id/members/voucher/:voucher_batch', (req, res, next) => {
		console.log('in GET /tertulias/:tr_id/members/voucher/:voucher_batch');
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.input('tertulia', sql.Int, req.params.tr_id)
			.input('batch', sql.NVarChar(36), req.params.voucher_batch)
			.query('SELECT' +
					' in_key AS voucher,' +
					' tr_name AS tertulia,' +
					' tr_subject AS subject' +
				' FROM Invitations' +
					' INNER JOIN Users ON in_user = us_id' +
					' INNER JOIN Tertulias ON in_tertulia = tr_id' +
				' WHERE tr_is_cancelled = 0 AND us_sid = @userSid' +
					' AND in_batch = @batch')
			.then(function(recordset) {
				console.log(recordset);
				res.json( { vouchers : recordset } );
				return next();
			});
		})
		.catch(function(err) {
			return next(err);
		});
	});

	router.post('/:tr_id/subscribe', (req, res, next) => {
		var HERE = '/tertulias/:tr_id/subscribe';
		console.log('in POST ' + HERE);
		var tr_id = req.params.tr_id;
		var myKey = req.body.myKey;
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.input('tertulia', sql.Int, tr_id)
			.execute('sp_subscribePublicTertulia')
			.then((recordset) => {
				console.log(recordset);
				if (recordset.returnValue = 1) {
					var tag = getPushTag(tr_id);
					var message = '{action:"subscribe",tertulia:' + tr_id + ',myKey:' + myKey + '}';
					pushMessage(tag, message);
					res.sendStatus(200);
					return next();
				} else {
					res.sendStatus(409);
					return next();
				}
			})
			.catch(function(err) {
				return next(err);
			});
		})
		.catch(function(err) {
			return next(err);
		});
	});

	router.delete('/:tr_id/unsubscribe', (req, res, next) => {
		var HERE = '/tertulias/:tr_id/unsubscribe';
		console.log('in DELETE ' + HERE);
		var tr_id = req.params.tr_id;
		var myKey = req.body.myKey;
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.input('tertulia', sql.Int, req.params.tr_id)
			.execute('spUnsubscribe')
			.then((recordset) => {
				if (recordset.returnValue = 1) {
					var tag = getPushTag(tr_id);
					var message = '{action:"unsubscribe",tertulia:' + tr_id + ',myKey:' + myKey + '}';
					pushMessage(tag, message);
					res.sendStatus(200);
				} else {
					res.sendStatus(409);
				}
				return next();
			})
			.catch(function(err) {
				return next(err);
			});
		})
		.catch(function(err) {
			return next(err);
		});
	});

	router.get('/:tr_id/messages', (req, res, next) => {
		var HERE = '/tertulias/:tr_id/message';
		console.log('in GET ' + HERE);
		var tr_id = req.params.tr_id;
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.input('tertulia', sql.Int, tr_id)
			.input('min_id', sql.Int, req.body.min_id)
			.query('SELECT' +
					' no_id AS id,' +
					' us_id AS userId,' +
					' us_alias AS alias,' +
					' no_tertulia AS tertulia,' +
					' tr_name AS tertuliaName,' +
					' no_tag AS tag,' +
					' no_message AS message' +
				' FROM Notifications' +
					' INNER JOIN Users ON no_user = us_id' +
					' INNER JOIN Tertulias ON no_tertulia = tr_id' +
					' INNER JOIN Members ON mb_tertulia = tr_id' +
				' WHERE tr_is_cancelled = 0' +
					' AND mb_user = us_id' +
					' AND tr_id = @tertulia' +
					' AND no_id > @min_id')
			.then((recordset) => {
				console.log(recordset);
				if (recordset['returnValue'] == 0) {
					res.sendStatus(200);
				} else {
					res.sendStatus(409);
				}
				return next();
			})
			.catch(function(err) {
				return next(err);
			});
		})
		.catch(function(err) {
			return next(err);
		});
	});

	// '{ "rel": "post_messages", "method": "POST", "href": "' + route + '/:id/messages" }, ' +
	router.post('/:tr_id/messages', (req, res, next) => {
		var HERE = '/tertulias/:tr_id/message';
		console.log('in POST ' + HERE);
		var tr_id = req.params.tr_id;
		var myKey = req.body.myKey;
		sql.connect(util.sqlConfiguration)
		.then(function() {
			new sql.Request()
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.input('tertulia', sql.Int, tr_id)
			.input('message', sql.NVarChar(40), req.body.message)
			.execute('sp_postNotification')
			.then((recordset) => {
				console.log(recordset);
				if (recordset['returnValue'] == 0) {
					var tag = getPushTag(tr_id);
					var message = '{action:"message",tertulia:' + tr_id + ',myKey:' + myKey + '}';
					pushMessage(tag, message);
					res.sendStatus(200);
					return next();
				} else {
					res.sendStatus(409);
					return next();
				}
			})
			.catch(function(err) {
				return next(err);
			});
		})
		.catch(function(err) {
			return next(err);
		});
	});

	var completeError = function(err, res) {
		if (err) {
			console.log("Error:");
			console.error(err);
			if (res) res.sendStatus(500);
		}
	};

	return router;

}
