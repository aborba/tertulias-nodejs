var express = require('express'),
bodyParser = require('body-parser'),

    authenticate = require('azure-mobile-apps/src/express/middleware/authenticate'),
    authorize = require('azure-mobile-apps/src/express/middleware/authorize');
var sql = require('mssql');

const queryTertulias = 'SELECT DISTINCT tr_id, tr_name, tr_subject, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude, sc_recurrency, tr_is_private, nv_name' +
' FROM Tertulias' +
' INNER JOIN Locations  ON tr_location = lo_id' +
' INNER JOIN Schedules  ON tr_schedule = sc_id' +
' INNER JOIN Members    ON mb_tertulia = tr_id' +
' INNER JOIN Users      ON mb_user = us_id' +
' INNER JOIN EnumValues ON mb_role = nv_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid';

module.exports = function (configuration) {
    var router = express.Router();

    router.get('/', (req, res, next) => {
		var selectedQuery = queryTertulias;
	    var paramsT = [];
	    paramsT['sid'] = sql.NVarChar; // String -> sql.NVarChar; Number -> sql.Int; Boolean -> sql.Bit; Date -> sql.DateTime; Buffer -> sql.VarBinary; sql.Table -> sql.TVP
	    var paramsV = {'sid': req.azureMobile.user.id };

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
	                    console.log(recordset);
	                    preparedStatement.unprepare();
	                    res.type('application/json').json(recordset);
	                    next();
	                }
	            );
	        });
	    });
	});

    return router;

}

