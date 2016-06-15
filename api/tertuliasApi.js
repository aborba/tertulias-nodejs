var express = require('express'),
	bodyParser = require('body-parser'),


    authenticate = require('azure-mobile-apps/src/express/middleware/authenticate'),
    authorize = require('azure-mobile-apps/src/express/middleware/authorize');
var sql = require('mssql');
var util = require('../util');

var route_paths = require('../route_paths');

console.log(route_paths.tertulia_defaultlocation);

const queryTertulias = 'SELECT DISTINCT tr_id, tr_name, tr_subject, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude, sc_recurrency, tr_is_private, nv_name' +
' FROM Tertulias' +
' INNER JOIN Locations  ON tr_location = lo_id' +
' INNER JOIN Schedules  ON tr_schedule = sc_id' +
' INNER JOIN Members    ON mb_tertulia = tr_id' +
' INNER JOIN Users      ON mb_user = us_id' +
' INNER JOIN EnumValues ON mb_role = nv_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid';

var queryTertuliaX = queryTertulias + ' AND tr_id = @tertulia';

module.exports = function (configuration) {
    var router = express.Router();

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
	    req.t_links = { edit: route_paths['edit'],
	    	members: route_paths['members'],
	    	messages: route_paths['messages'],
	    	events: route_paths['events'],
	    	nextevent: route_paths['nextevent'] };
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
	                    recordset.forEach(function(elem) {
	                    	elem['_links'] = { self: { href : 'tertulias/' + elem.tr_id } };
	                    	if (typeof req.t_links !== typeof undefined)
	                			for (var key in req.t_links)
	                				elem['_links'][key] = { href : 'tertulias/' + elem.tr_id + '/' + req.t_links[key]};
	                    	console.log(elem['_links']);
	                    });
	                    preparedStatement.unprepare();
	                    res.type('application/json').json(recordset);
	                    next();
	                }
	            );
	        });
	    });
	}

    return router;

}

