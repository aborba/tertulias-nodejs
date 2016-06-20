var express = require('express'),
	bodyParser = require('body-parser'),
    authenticate = require('azure-mobile-apps/src/express/middleware/authenticate'),
    authorize = require('azure-mobile-apps/src/express/middleware/authorize');

var sql = require('mssql');
var util = require('../util');

const queryTertulias = 'SELECT tr_id, tr_name, tr_subject, ev_targetdate, nv_name, no_count' +
' FROM Tertulias' +
' INNER JOIN Members ON mb_tertulia = tr_id' +
' INNER JOIN Enumvalues ON mb_role = nv_id' +
' INNER JOIN Users ON mb_user = us_id' +
' LEFT JOIN' +
' (SELECT * FROM' +
' (SELECT RANK() OVER(PARTITION BY ev_tertulia ORDER BY ev_targetdate DESC) AS "rank", * FROM Events) AS a WHERE a.rank = 1) AS b' +
' ON ev_tertulia = tr_id' +
' LEFT JOIN (SELECT no_tertulia, count(*) AS no_count FROM Notifications where no_id not in' +
' (SELECT no_id FROM Notifications INNER JOIN Readnotifications ON rn_notification = no_id INNER JOIN Users ON rn_user = us_id WHERE us_sid = @sid)' +
' GROUP BY no_tertulia) AS c ON no_tertulia = tr_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid' ;

const queryTertuliaX = 'SELECT DISTINCT' +
	' tr_id, tr_name, tr_subject, ' + // Tertulia
	' lo_name, lo_address, lo_zip, lo_city, lo_country, lo_latitude, lo_longitude, ' + // Location
	' sc_type, _Schedule.nv_name AS schedule, _Schedule.nv_description AS description,' + // Schedule
	' tr_is_private, ' +
	' _Member.nv_name AS nv_name' + // Role
' FROM Tertulias' +
' INNER JOIN Locations  ON tr_location = lo_id' +
' INNER JOIN Schedules  ON tr_schedule = sc_id' +
' INNER JOIN Members    ON mb_tertulia = tr_id' +
' INNER JOIN Users      ON mb_user = us_id' +
' INNER JOIN EnumValues AS _Member ON mb_role = _Member.nv_id' +
' INNER JOIN EnumValues AS _Schedule ON sc_type = _Schedule.nv_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid' +
' AND tr_id = @tertulia';

const queryScheduleMonthlyW = 'SELECT mw_id, mw_dow, mw_weeknr, mw_is_fromstart, mw_skip' +
' FROM MonthlyW' +
' INNER JOIN Schedules  ON mw_schedule = sc_id' +
' INNER JOIN Tertulias  ON sc_tertulia = tr_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid' +
' AND tr_id = @tertulia';

module.exports = function (configuration) {
    var router = express.Router();

	var completeError = function(err, res) {
	    if (err) {
	        console.error(err);
	        if (res) res.sendStatus(500);
	    }
	};

    router.get('/', (req, res, next) => {
		req.selectedQuery = queryTertulias;
	    req.paramsT = { 'sid': sql.NVarChar }; // String -> sql.NVarChar; Number -> sql.Int; Boolean -> sql.Bit; Date -> sql.DateTime; Buffer -> sql.VarBinary; sql.Table -> sql.TVP
	    req.paramsV = { 'sid': req.azureMobile.user.id };
	    req.t_links = '{ "details": { "href": "tertulias/:tertulia" } }';
	    goGet(req, res, next);
	});

    router.get('/:tertulia', (req, res, next) => {
		req.selectedQuery = queryTertuliaX;
	    req.paramsT = { sid: sql.NVarChar, tertulia: sql.NVarChar };
	    req.paramsV = { sid: req.azureMobile.user.id, tertulia: req.params.tertulia };
	    req.t_links = '{ ' +
	    	'"edit":      { "href": "tertulias/:tertulia/edit" }, ' +
	    	'"members":   { "href": "tertulias/:tertulia/members" }, ' +
	    	'"messages":  { "href": "tertulias/:tertulia/messages" }, ' +
	    	'"events":    { "href": "tertulias/:tertulia/events" }, ' +
	    	'"nextevent": { "href": "tertulias/:tertulia/nextevent" } ' +
    	'}';
	    goGet(req, res, next);
	});

	var goGet = function(req, res, next) {
		var selectedQuery = req.selectedQuery;
	    var paramsT = req.paramsT;
	    var paramsV = req.paramsV;

		var connection = new sql.Connection(util.sqlConfiguration);
	    connection.connect(function(err) {
	        var preparedStatement = new sql.PreparedStatement(connection);
	        for (var key in paramsT) preparedStatement.input(key, paramsT[key]);
	        preparedStatement.prepare(selectedQuery, function(err) {
	            if (err) { completeError(err, res); return; }
	            preparedStatement.execute(paramsV, 
	                function(err, recordset, affected) {
	                    if (err) { completeError(err, res); return; }
	                    res.type('application/json');
	                    recordset.forEach(function(elem) {
	                    	console.log(elem.tr_id);
	                    	if (typeof req.t_links !== typeof undefined) {
	                    		console.log(req.t_links);
		                    	elem['_links'] = JSON.parse(req.t_links.replace(/:tertulia/g, elem.tr_id));
		                    	/*
		                    	elem['_links'] = { self: { href : 'tertulias/' + elem.tr_id } };
		                    	if (typeof req.t_links !== typeof undefined)
		                			for (var key in req.t_links)
		                				elem['_links'][key] = { href : 'tertulias/' + elem.tr_id + '/' + req.t_links[key]};
		                    	console.log(elem);
		                    	*/
		                    	console.log(elem._links);
	                    	}
	                    });
	                    console.log(recordset);
	                    preparedStatement.unprepare();
	                    res.json(recordset);
	                    next();
	                }
	            );
	        });
	    });
	}

	router.post('/', (req, res, next) => {
		console.log(req.body);
	    sql.connect(util.sqlConfiguration)
	    .then(function() {
			new sql.Request()
			.input('name', sql.NVarChar(40), req.body.tr_name)
			.input('subject', sql.NVarChar(80), req.body.tr_subject)
			.input('userId', sql.Int, req.azureMobile.user.id)
			.input('weekDay', sql.NVarChar(20), 'Tuesday')
			.input('weekNr', sql.Int, 1)
			.input('fromStart', sql.BIT, 1)
			.input('skip', sql.Int, 0)
			.input('locationName', sql.NVarChar(40), req.body.lo_name)
			.input('locationAddress', sql.NVarChar(80), req.body.lo_address)
			.input('locationZip', sql.NVarChar(40), req.body.lo_zip)
			.input('locationCountry', sql.NVarChar(40), req.body.lo_country)
			.input('locationLatitude', sql.NVarChar(12), req.body.lo_latitude)
			.input('locationLongitude', sql.NVarChar(12), req.body.lo_longitude)
			.input('isPrivate', sql.Int, req.body.tr_is_private ? 1 : 0)
			.execute('sp_insertTertulia_MonthlyW_sid')
			.then(function(recordsets) {
				console.log(recordsets);
				next();
			}).catch(function(err) {
				console.log('catch 1');
				console.log(err);
			});
		})
		.catch(function(err) {
			console.log('catch 2');
			console.log(err);
		});

		res.type('application/json')
			.json({id: '1'});
		next();
	})

    return router;

}
