var express = require('express'),
	bodyParser = require('body-parser'),
    authenticate = require('azure-mobile-apps/src/express/middleware/authenticate'),
    authorize = require('azure-mobile-apps/src/express/middleware/authorize');

var sql = require('mssql');
var util = require('../util');

/* { 'SQL types': {
	'String': 'sql.NVarChar', 'Number': 'sql.Int', 'Boolean': 'sql.Bit', 'Date': 'sql.DateTime', 'Buffer': 'sql.VarBinary', 'sql.Table': 'sql.TVP'
} } */
module.exports = function (configuration) {
    var router = express.Router();

    router.get('/', (req, res, next) => {
		console.log('in GET /tertulias');
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

    router.get('/publicSearch', (req, res, next) => {
		console.log('in GET /tertulias/publicsearch');
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

    router.get('/:tr_id/members', (req, res, next) => {
		console.log('in GET /tertulias/:tr_id/members');
		var tr_id = req.params.tr_id;
		var route = '/tertulias/' + tr_id + '/members';
	    sql.connect(util.sqlConfiguration)
	    .then(function() {
	    	console.log(req.azureMobile.user.id);
	    	console.log(tr_id);
			new sql.Request()
	    	.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
	    	.input('tertulia', sql.Int, tr_id)
			.execute('sp_getTertuliaMembers')
	    	.then(function(recordset) {
                var links = '[ ' +
					'{ "rel": "self", "method": "GET", "href": "' + route + '" }, '
					'{ "rel": "get_vouchers", "method": "POST", "href": "' + route + '/voucher" } '
            	']';
                var itemLinks = '[ ' +
        	    	'{ "rel": "self", "method": "GET", "href": "' + route + '/:id" }, ' +
					'{ "rel": "edit_member", "method": "PATCH", "href": "' + route + '/:id/edit" }' +
				']';
                res.type('application/json');
                recordset.forEach(function(elem) {
                	elem['links'] = JSON.parse(itemLinks.replace(/:id/g, elem.id));
        		});
                var results = {};
            	results['members'] = recordset;
                results['links'] = JSON.parse(links);
                console.log(results);
                res.json(results);
                res.sendStatus(200);
                return next();
            })
	    });
	});

	router.get('/:tr_id', (req, res, next) => {
		console.log('in GET /tertulias/:tr_id');
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
						'{ "rel": "subscribe", "method": "POST", "href": "' + route + '/subscribe" }, ' +
						'{ "rel": "unsubscribe", "method": "DELETE", "href": "' + route + '/unsubscribe" } ' +
					']';
                results['links'] = JSON.parse(links);
				req.results = results;
				console.log("Schedule name: " + results['tertulia'].schedule_name);
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
								console.log(results);
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
								console.log(results);
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
								console.log(results);
				                res.type('application/json');
				                res.json(results);
				                res.sendStatus(200);
				                return next();
							})
						});
						break;
					default:
						console.log(results['tertulia'].schedule_name);
						res.status(404)	// 404: NOT Found
						.type('application/json')
						.json( { result: 'Not Found' } );
						return next('404');
				}
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
				next();
			})
			.catch(function(err) {
				next(err);
			});
		})
		.catch(function(err) {
			return next(err);
		});
	});

	router.patch('/:tr_id', (req, res, next) => {
		console.log('in PATCH /tertulias/:tr_id');
		var tr_id = req.params.tr_id;
		if (isNaN(tr_id))
			return next();
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
						next();
					})
					.catch(function(err) {
						console.log("WEEKLY error");
						next(err);
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
						next();
					})
					.catch(function(err) {
						console.log("MONTHLYD error");
						next(err);
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
						next();
					})
					.catch(function(err) {
						console.log("MONTHLYW error");
						next(err);
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
				next();
			})
			.catch(function(err) {
				next(err);
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
				next();
			})
			.catch(function(err) {
				next(err);
			});
		})
		.catch(function(err) {
			return next(err);
		});
	});

	router.post('/:tr_id/subscribe', (req, res, next) => {
		console.log('in POST /tertulias/:tr_id/subscribe');
	    sql.connect(util.sqlConfiguration)
	    .then(function() {
			new sql.Request()
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.input('tertulia', sql.Int, req.params.tr_id)
			.execute('spSubscribe')
			.then((recordset) => {
				if (recordset.returnValue = 1) {
					res.sendStatus(200);
				} else {
					res.sendStatus(409);
				}
				return next();
			})
			.catch(function(err) {
				next(err);
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
			.query('SELECT' + ' in_key AS voucher' +
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
				var route = '/tertulias/' + tr_id + '/voucher';
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

		// sql.connect(util.sqlConfiguration)
		// .then(function() {
		// 	new sql.Request()
		// 	.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
		// 	.input('tertulia', sql.Int, req.params.tr_id)
		// 	.output('voucher', sql.NVarChar(36), '3C1EC24B-31AD-4D2D-9DAE-8F98EF32B155')
		// 	.execute('sp_inviteToTertulia')
		// 	.then((recordsets) => {
		// 		console.log(recordsets);
		// 		console.log(parameters);
		// 		if (recordsets == '[ returnValue : 0 ]') {
		// 			console.log('success');
		// 			res.sendStatus(200);
		// 		} else {
		// 			console.log('failure');
		// 			res.sendStatus(409);
		// 		}
		// 		return next();
		// 	})
		// 	.catch(function(err) {
		// 		console.log('catched error');
		// 		next(err);
		// 	});
		// })
		// .catch(function(err) {
		// 	return next(err);
		// });
	});

	router.delete('/:tr_id/unsubscribe', (req, res, next) => {
		console.log('in DELETE /tertulias/:tr_id/unsubscribe');
	    sql.connect(util.sqlConfiguration)
	    .then(function() {
			new sql.Request()
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.input('tertulia', sql.Int, req.params.tr_id)
			.execute('spUnsubscribe')
			.then((recordset) => {
				console.log(recordset);
				if (recordset.returnValue = 1) {
					res.sendStatus(200);
				} else {
					res.sendStatus(409);
				}
				return next();
			})
			.catch(function(err) {
				next(err);
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
