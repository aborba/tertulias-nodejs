var express = require('express'),
	bodyParser = require('body-parser'),
    authenticate = require('azure-mobile-apps/src/express/middleware/authenticate'),
    authorize = require('azure-mobile-apps/src/express/middleware/authorize');

var sql = require('mssql');
var util = require('../util');

const queryTertulias = 'SELECT' +
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
'  INNER JOIN Locations on ev_location = lo_id WHERE ev_targetdate > GETDATE()) AS a WHERE a.rank = 1) AS b' +
' ON ev_tertulia = tr_id' +
' LEFT JOIN (SELECT no_tertulia, count(*) AS no_count FROM Notifications WHERE no_id NOT IN' +
' (SELECT no_id FROM Notifications INNER JOIN Readnotifications ON rn_notification = no_id' +
' INNER JOIN Users ON rn_user = us_id WHERE us_sid = @sid)' +
' GROUP BY no_tertulia) AS c ON no_tertulia = tr_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid';

const queryPublicTertulias = 'SELECT' +
' TOP 25' +
' tr_id         AS id,' +
' tr_name       AS name,' +
' tr_subject    AS subject,' +
' lo_name       AS location' +
//' lo_latitude   AS latitude,' +
//' lo_longitude  AS longitude,' +
//' lo_geography.STDistance('POINT(38.7762 -9.171391)') AS Distance' +
' FROM Tertulias' +
' INNER JOIN Locations ON tr_location = lo_id' +
' WHERE tr_is_cancelled = 0' +
' AND tr_is_private = 0' +
' AND tr_id NOT IN (SELECT mb_tertulia FROM Tertulias INNER JOIN Members ON mb_tertulia = tr_id INNER JOIN Users ON mb_user = us_id WHERE us_sid = @sid)' +
' ORDER BY lo_geography.STDistance(\'POINT(38.7640613 -9.1123113)\')';

const queryTertuliaDetails = 'SELECT DISTINCT' +
	' tr_id                    AS id,' + // Tertulia
	' tr_name                  AS name,' +
	' tr_subject               AS subject,' +
	' lo_name                  AS location,' + // Location
	' lo_address               AS address,' +
	' lo_zip                   AS zip,' +
	' lo_city                  AS city,' +
	' lo_country               AS country,' +
	' lo_latitude              AS latitude,' +
	' lo_longitude             AS longitude,' +
	' sc_type                  AS scheduleId,' + // Schedule
	' _Schedule.nv_name        AS scheduleName,' +
	' _Schedule.nv_description AS scheduleDescription,' +
	' tr_is_private            AS private, ' +  // Private
	' _Member.nv_name          AS role' + // Role
' FROM Tertulias' +
' INNER JOIN Locations  ON tr_location = lo_id' +
' INNER JOIN Schedules  ON tr_schedule = sc_id' +
' INNER JOIN Members    ON mb_tertulia = tr_id' +
' INNER JOIN Users      ON mb_user = us_id' +
' INNER JOIN EnumValues AS _Member ON mb_role = _Member.nv_id' +
' INNER JOIN EnumValues AS _Schedule ON sc_type = _Schedule.nv_id' +
' WHERE tr_is_cancelled = 0 AND (us_sid = @sid OR tr_is_private = 0)' +
' AND tr_id = @tertulia';

/* { 'SQL types': {
	'String': 'sql.NVarChar', 'Number': 'sql.Int', 'Boolean': 'sql.Bit', 'Date': 'sql.DateTime', 'Buffer': 'sql.VarBinary', 'sql.Table': 'sql.TVP'
} } */
module.exports = function (configuration) {
    var router = express.Router();

    router.get('/', (req, res, next) => {
		var route = '/tertulias';
		req['tertulias'] = {};
		req.tertulias['resultsTag'] = 'tertulias';
		req.tertulias['query'] = queryTertulias;
	    req.tertulias['paramsTypes'] = { 'sid': sql.NVarChar };
	    req.tertulias['paramsValues'] = { 'sid': req.azureMobile.user.id };
	    req.tertulias['jsonType'] = "array";
	    req.tertulias['links'] = '[ ' +
			'{ "rel": "self", "method": "GET", "href": "' + route + '" }, ' +
			'{ "rel": "create", "method": "POST", "href": "' + route + '" }, ' +
			'{ "rel": "searchPublic", "method": "GET", "href": "' + route + '/publicsearch" } ' +
		']';
	    req.tertulias['itemLinks'] = '[ ' +
			'{ "rel": "self", "method": "GET", "href": "' + route + '/:id" }, ' +
			'{ "rel": "update", "method": "PUT", "href": "' + route + '/:id" }, ' +
			'{ "rel": "delete", "method": "DELETE", "href": "' + route + '/:id" }, ' +
			'{ "rel": "unsubscribe", "method": "DELETE", "href": "' + route + '/:id/unsubscribe" } ' +
		']';
	    goGet(req, res, next);
	});

    router.get('/publicSearch', (req, res, next) => {
		console.log('in /publicsearch');
	    sql.connect(util.sqlConfiguration)
	    .then(function() {
			new sql.Request()
	    	.input('sid', sql.NVarChar(40), req.azureMobile.user.id)
	    	.input('query', sql.NVarChar, req.query.query)
	    	.input('latitude', sql.Int, parseFloat(req.query.latitude))
	    	.input('longitude', sql.Int, parseFloat(req.query.longitude))
	    	.query('SELECT TOP 25' +
		    		' tr_id AS id,' +
		    		' tr_name AS name,' +
		    		' tr_subject AS subject,' +
		    		' lo_name AS location' +
	    		' FROM Tertulias' +
	    			' INNER JOIN Locations ON tr_location = lo_id' +
	    		' WHERE tr_is_cancelled = 0 AND tr_is_private = 0' +
		    		' AND tr_id NOT IN' +
		    			' (SELECT mb_tertulia FROM Tertulias' +
		    			' INNER JOIN Members ON mb_tertulia = tr_id' +
		    			' INNER JOIN Users ON mb_user = us_id WHERE us_sid = @sid)' +
				' ORDER BY lo_geography.STDistance(\'POINT(38.7640613 -9.1123113)\')')
	    	.then(function(recordset) {
	    		console.log('recordset:');
	    		console.log(recordset);
                var links = '[ ' +
						'{ "rel": "self", "method": "GET", "href": "' + route + '/publicSearch" }' +
					']'
                var itemLinks = '[ ' +
            	    	'{ "rel": "self", "method": "GET", "href": "' + route + '/:id" }, ' +
						'{ "rel": "subscribe", "method": "POST", "href": "' + route + '/:id/subscribe" }' +
					']';
                res.type('application/json');
                recordset.forEach(function(elem) {
                	elem['links'] = JSON.parse(itemLinks.replace(/:id/g, elem.id));
        		});
                preparedStatement.unprepare();
                var results = {};
                if (req.tertulias.jsonType == "array")
                	results[resultsTag] = recordset;
                else
                	results[resultsTag] = recordset.length == 0 ? {} : recordset[0];
                results['links'] = JSON.parse(links);
                res.json(results);
                return next();
            })
	    });
	});
/*
		var route = '/tertulias';
		req['tertulias'] = {};
		req.tertulias['resultsTag'] = 'tertulias';
		req.tertulias['query'] = queryPublicTertulias;
	    req.tertulias['paramsTypes'] = {
	    	'sid': sql.NVarChar
	    	, 'query': sql.NVarChar
	    	, 'latitude': sql.Int
	    	, 'longitude': sql.Int
	    };
	    var latitude = parseFloat(req.query.latitude);
	    var longitude = parseFloat(req.query.longitude);
	    req.tertulias['paramsValues'] = {
	    	'sid': req.azureMobile.user.id
	    	, 'query': req.query.query
	    	, 'latitude': parseFloat(req.query.latitude)
	    	, 'longitude': parseFloat(req.query.longitude)
	    };
	    req.tertulias['jsonType'] = "array";
	    req.tertulias['links'] = '[ ' +
			'{ "rel": "self", "method": "GET", "href": "' + route + '/publicSearch" }' +
		']';
	    req.tertulias['itemLinks'] = '[ ' +
			'{ "rel": "self", "method": "GET", "href": "' + route + '/:id" }, ' +
			'{ "rel": "subscribe", "method": "POST", "href": "' + route + '/:id/subscribe" }' +
		']';
	    goGet(req, res, next);
	});
	*/

	router.get('/:tr_id', (req, res, next) => {
		console.log('in /:tr_id');
		var tr_id = req.params.tr_id;

		if (isNaN(tr_id))
			return next();

		var route = '/tertulias/' + tr_id;
		req['tertulias'] = {};
		req.tertulias['resultsTag'] = 'tertulia';
		req.tertulias['query'] = queryTertuliaDetails;
	    req.tertulias['paramsTypes'] = { 'sid': sql.NVarChar, 'tertulia': sql.Int };
	    req.tertulias['paramsValues'] = { 'sid': req.azureMobile.user.id, 'tertulia': tr_id };
	    req.tertulias['jsonType'] = "object";
	    req.tertulias['links'] = '[ ' +
			'{ "rel": "self", "method": "GET", "href": "' + route + '" }, ' +
			'{ "rel": "update", "method": "PATCH", "href": "' + route + '" }, ' +
			'{ "rel": "delete", "method": "DELETE", "href": "' + route + '" }, ' +
			'{ "rel": "subscribe", "method": "POST", "href": "' + route + '/subscribe" }, ' +
			'{ "rel": "unsubscribe", "method": "DELETE", "href": "' + route + '/unsubscribe" } ' +
		']';
	    goGet(req, res, next);
	});

	router.post('/', (req, res, next) => {
		console.log('in /');
	    sql.connect(util.sqlConfiguration)
	    .then(function() {
			new sql.Request()
			.input('name', sql.NVarChar(40), req.body.name)
			.input('subject', sql.NVarChar(80), req.body.subject)
			.input('locationName', sql.NVarChar(40), req.body.location)
			.input('locationAddress', sql.NVarChar(80), req.body.address)
			.input('locationZip', sql.NVarChar(40), req.body.zip)
			.input('locationCity', sql.NVarChar(40), req.body.city)
			.input('locationCountry', sql.NVarChar(40), req.body.country)
			.input('locationLatitude', sql.NVarChar(12), req.body.latitude)
			.input('locationLongitude', sql.NVarChar(12), req.body.longitude)
			.input('weekDay', sql.NVarChar(20), 'Tuesday')
			.input('weekNr', sql.Int, 1)
			.input('fromStart', sql.BIT, 1)
			.input('skip', sql.Int, 0)
			.input('isPrivate', sql.Int, req.body.isPrivate ? 1 : 0)
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
		console.log('in /:tr_id/subscribe');
	    sql.connect(util.sqlConfiguration)
	    .then(function() {
			new sql.Request()
			.input('sid', sql.NVarChar(40), req.azureMobile.user.id)
			.input('tertulia', sql.Int, req.params.tr_id)
			.execute('spSubscribe')
			.then((recordset) => {
				console.log(recordset.returnValue);
				if (recordset.returnValue = 1) {
					res.sendStatus(200);
				} else {
					res.sendStatus(409);
				}
				return next();
			})
			.catch(function(err) {
				console.log('catch1');
				console.log(err);
				next(err);
			});
		})
		.catch(function(err) {
			console.log('catch');
			console.log(err);
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

	var goGet = function(req, res, next) {
		console.log('in GoGet');
		var query = req.tertulias.query;
		var resultsTag = req.tertulias.resultsTag;
	    var paramsTypes = req.tertulias.paramsTypes;
	    var paramsValues = req.tertulias.paramsValues;
	    var links = req.tertulias.links;
	    var itemLinks = req.tertulias.itemLinks;

		var connection = new sql.Connection(util.sqlConfiguration);
	    connection.connect(function(err) {
	        var preparedStatement = new sql.PreparedStatement(connection);
	        for (var key in paramsTypes)
	        	preparedStatement.input(key, paramsTypes[key]);
	        preparedStatement.prepare(query, function(err) {
	            if (err) {
	            	completeError(err, res);
	            	return next(err);
	            }
	            preparedStatement.execute(paramsValues, 
	                function(err, recordset, affected) {
	                    if (err) {
	                    	completeError(err, res);
	                    	return next(err);
	                    }
	                    res.type('application/json');
                    	if (typeof itemLinks !== typeof undefined) {
		                    recordset.forEach(function(elem) {
		                    	elem['links'] = JSON.parse(itemLinks.replace(/:id/g, elem.id));
                    		});
	                    };
	                    preparedStatement.unprepare();
	                    var results = {};
	                    if (req.tertulias.jsonType == "array")
	                    	results[resultsTag] = recordset;
	                    else
	                    	results[resultsTag] = recordset.length == 0 ? {} : recordset[0];
	                    results['links'] = JSON.parse(links);
	                    res.json(results);
	                    return next();
	                }
	            );
	        });
	    });
	};

    return router;

}
