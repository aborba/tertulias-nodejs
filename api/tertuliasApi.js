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
	    	.input('sid', sql.NVarChar(40), req.azureMobile.user.id)
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
						' INNER JOIN Users ON rn_user = us_id WHERE us_sid = @sid)' +
				' GROUP BY no_tertulia) AS c ON no_tertulia = tr_id' +
				' WHERE tr_is_cancelled = 0 AND us_sid = @sid')
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
	    	.input('sid', sql.NVarChar(40), req.azureMobile.user.id)
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
		    			' INNER JOIN Users ON mb_user = us_id WHERE us_sid = @sid)' +
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

	router.get('/:tr_id/weekly', (req, res, next) => {
		console.log('in GET /tertulias/:tr_id/weekly');
		var tr_id = req.params.tr_id;
		var route = '/tertulias/' + tr_id + "/weekly";
		if (isNaN(tr_id))
			return next();

	    sql.connect(util.sqlConfiguration)
	    .then(function() {
			new sql.Request()
			.input('tertulia', sql.Int, tr_id)
			.input('sid', sql.NVarChar(40), req.azureMobile.user.id)
			.query('SELECT' +
					' wk_id AS id,' +
					' wk_schedule AS scheduleId,' +
					' wk_dow AS dow,' +
					' wk_skip AS skip' +
				' FROM Weekly' +
					' INNER JOIN Schedules ON wk_schedule = sc_id' +
					' INNER JOIN Tertulias ON tr_schedule = sc_id' +
				' WHERE tr_is_cancelled = 0 AND us_sid = @sid' +
					' AND tr_id = @tertulia')
			.then(function(recordset) {
				var links = '[ ' +
						'{ "rel": "self", "method": "GET", "href": "' + route + '" }, ' +
						'{ "rel": "update", "method": "PATCH", "href": "' + route + '" }, ' +
						'{ "rel": "delete", "method": "DELETE", "href": "' + route + '" } ' +
					']';
                res.type('application/json');
                var results = {};
            	results['weekly'] = recordset[0];
                results['links'] = JSON.parse(links);
                res.json(results);
                res.sendStatus(200);
                return next();
			})
		});
	}

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
			.input('sid', sql.NVarChar(40), req.azureMobile.user.id)
			.query('SELECT' +
					' tr_id AS id,' + // Tertulia
					' tr_name AS name,' +
					' tr_subject AS subject,' +
					' lo_name AS location,' + // Location
					' lo_address AS address,' +
					' lo_zip AS zip,' +
					' lo_city AS city,' +
					' lo_country AS country,' +
					' lo_latitude AS latitude,' +
					' lo_longitude AS longitude,' +
					' sc_type AS scheduleId,' + // Schedule
					' _Schedule.nv_name AS scheduleName,' +
					' _Schedule.nv_description AS scheduleDescription,' +
					' tr_is_private AS private, ' +  // Private
					' _Member.nv_name AS role' + // Role
				' FROM Tertulias' +
					' INNER JOIN Locations ON tr_location = lo_id' +
					' INNER JOIN Schedules ON tr_schedule = sc_id' +
					' LEFT JOIN Members ON mb_tertulia = tr_id' +
					' LEFT JOIN Users ON mb_user = us_id' +
					' LEFT JOIN EnumValues AS _Member ON mb_role = _Member.nv_id' +
					' INNER JOIN EnumValues AS _Schedule ON sc_type = _Schedule.nv_id' +
				' WHERE tr_is_cancelled = 0 AND (us_sid = @sid OR (tr_is_private = 0' +
					' AND  tr_id NOT IN' +
						' (SELECT tr_id' +
						' FROM Tertulias' +
							' INNER JOIN Members ON mb_tertulia = tr_id' +
							' INNER JOIN Users ON mb_user = us_id' +
						' WHERE us_sid = @sid)))' +
					' AND tr_id = @tertulia')
			.then(function(recordset) {
				var links = '[ ' +
						'{ "rel": "self", "method": "GET", "href": "' + route + '" }, ' +
						'{ "rel": "update", "method": "PATCH", "href": "' + route + '" }, ' +
						'{ "rel": "delete", "method": "DELETE", "href": "' + route + '" }, ' +
						'{ "rel": "subscribe", "method": "POST", "href": "' + route + '/subscribe" }, ' +
						'{ "rel": "unsubscribe", "method": "DELETE", "href": "' + route + '/unsubscribe" } ' +
					']';
                res.type('application/json');
                var results = {};
            	results['tertulia'] = recordset[0];
                results['links'] = JSON.parse(links);
                res.json(results);
                res.sendStatus(200);
                return next();
			})
		});
	});

	router.post('/weekly', (req, res, next) => {
		console.log('in POST /tertulias/weekly');
	    sql.connect(util.sqlConfiguration)
	    .then(function() {
			new sql.Request()
			.input('name', sql.NVarChar(40), req.body.tr_name)
			.input('subject', sql.NVarChar(80), req.body.tr_subject)
			.input('locationName', sql.NVarChar(40), req.body.lo_name)
			.input('locationAddress', sql.NVarChar(80), req.body.lo_address)
			.input('locationZip', sql.NVarChar(40), req.body.lo_zip)
			.input('locationCity', sql.NVarChar(40), req.body.lo_city)
			.input('locationCountry', sql.NVarChar(40), req.body.lo_country)
			.input('locationLatitude', sql.NVarChar(12), req.body.lo_latitude)
			.input('locationLongitude', sql.NVarChar(12), req.body.lo_longitude)
			.input('weekDay', sql.NVarChar(20), req.body.sc_weekDay)
			.input('skip', sql.Int, req.body.sc_skip)
			.input('isPrivate', sql.Int, req.body.tr_isPrivate ? 1 : 0)
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.execute('sp_insertTertulia_Weekly_sid')
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

	router.post('/monthly', (req, res, next) => {
		console.log('in POST /tertulias/monthly');
	    sql.connect(util.sqlConfiguration)
	    .then(function() {
			new sql.Request()
			.input('name', sql.NVarChar(40), req.body.tr_name)
			.input('subject', sql.NVarChar(80), req.body.tr_subject)
			.input('locationName', sql.NVarChar(40), req.body.lo_name)
			.input('locationAddress', sql.NVarChar(80), req.body.lo_address)
			.input('locationZip', sql.NVarChar(40), req.body.lo_zip)
			.input('locationCity', sql.NVarChar(40), req.body.lo_city)
			.input('locationCountry', sql.NVarChar(40), req.body.lo_country)
			.input('locationLatitude', sql.NVarChar(12), req.body.lo_latitude)
			.input('locationLongitude', sql.NVarChar(12), req.body.lo_longitude)
			.input('dom', sql.Int, req.body.sc_dayNr)
			.input('fromStart', sql.BIT, req.body.sc_fromStart ? 1 : 0)
			.input('skip', sql.Int, req.body.sc_skip)
			.input('isPrivate', sql.Int, req.body.tr_isPrivate ? 1 : 0)
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
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
	    sql.connect(util.sqlConfiguration)
	    .then(function() {
			new sql.Request()
			.input('name', sql.NVarChar(40), req.body.tr_name)
			.input('subject', sql.NVarChar(80), req.body.tr_subject)
			.input('locationName', sql.NVarChar(40), req.body.lo_name)
			.input('locationAddress', sql.NVarChar(80), req.body.lo_address)
			.input('locationZip', sql.NVarChar(40), req.body.lo_zip)
			.input('locationCity', sql.NVarChar(40), req.body.lo_city)
			.input('locationCountry', sql.NVarChar(40), req.body.lo_country)
			.input('locationLatitude', sql.NVarChar(12), req.body.lo_latitude)
			.input('locationLongitude', sql.NVarChar(12), req.body.lo_longitude)
			.input('weekDay', sql.NVarChar(20), req.body.sc_weekDay)
			.input('weekNr', sql.Int, req.body.sc_weekNr)
			.input('fromStart', sql.BIT, req.body.sc_fromStart ? 1 : 0)
			.input('skip', sql.Int, req.body.sc_skip)
			.input('isPrivate', sql.Int, req.body.tr_isPrivate ? 1 : 0)
			.input('userSid', sql.NVarChar(40), req.azureMobile.user.id)
			.execute('sp_insertTertulia_MonthlyW_sid')
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

	router.post('/:tr_id/subscribe', (req, res, next) => {
		console.log('in POST /tertulias/:tr_id/subscribe');
	    sql.connect(util.sqlConfiguration)
	    .then(function() {
			new sql.Request()
			.input('sid', sql.NVarChar(40), req.azureMobile.user.id)
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

	router.delete('/:tr_id/unsubscribe', (req, res, next) => {
		console.log('in DELETE /tertulias/:tr_id/unsubscribe');
	    sql.connect(util.sqlConfiguration)
	    .then(function() {
			new sql.Request()
			.input('sid', sql.NVarChar(40), req.azureMobile.user.id)
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
