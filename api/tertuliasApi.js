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
' LEFT JOIN (SELECT no_tertulia, COUNT(*) AS no_count FROM Notifications GROUP BY no_tertulia) AS c ON no_tertulia = tr_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid';

const queryTertuliaX = 'SELECT DISTINCT' +
	' tr_id, tr_name, tr_subject, ' + // Tertulia
	' lo_address, lo_zip, lo_country, lo_latitude, lo_longitude, ' + // Location
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
	    goQuery(req, res, next);
	});

    router.get('/:tertulia', (req, res, next) => {
		req.selectedQuery = queryTertuliaX;
	    req.paramsT = { sid: sql.NVarChar, tertulia: sql.NVarChar };
	    req.paramsV = { sid: req.azureMobile.user.id, tertulia: req.params.tertulia };
	    req.t_links = { edit: 'edit', members: 'members', messages: 'messages', events: 'events', nextevent: 'nextevent' };
	    goQuery(req, res, next);
	});

	var goQuery = function(req, res, next) {
		var selectedQuery = req.selectedQuery;
	    var paramsT = req.paramsT;
	    var paramsV = req.paramsV;

		var connection = new sql.Connection(util.sqlConfiguration);
	    connection.connect(function(err) {
	        var sqlRequest = new sql.Request(connection);
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
	                    	elem['_links'] = { self: { href : 'tertulias/' + elem.tr_id } };
	                    	if (typeof req.t_links !== typeof undefined)
	                			for (var key in req.t_links)
	                				elem['_links'][key] = { href : 'tertulias/' + elem.tr_id + '/' + req.t_links[key]};
	                    	console.log(elem);
	                    	console.log(elem._links);
	                    });
	                    preparedStatement.unprepare();
	                    res.json(recordset);
	                    next();
	                }
	            );
	        });
	    });
	}

    return router;

}
