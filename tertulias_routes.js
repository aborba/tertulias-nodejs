var express = require('express'),
    azureMobileApps = require('azure-mobile-apps');
var util = require('./util');
var u = require('azure-mobile-apps/src/auth/user');
var sql = require('mssql');

var router = express.Router();
var miniapp = express();

const statusOK = 200;

var appConfiguration = {   // http://azure.github.io/azure-mobile-apps-node/global.html#configuration
    debug: true,
    homePage: true,
    swagger: false,
    skipVersionCheck: true
}

var mobile = azureMobileApps(appConfiguration);

const queryTertulias = 'SELECT DISTINCT tr_id, tr_name, tr_subject, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude, sc_recurrency, tr_is_private, nv_name' +
' FROM Tertulias' +
' INNER JOIN Locations  ON tr_location = lo_id' +
' INNER JOIN Schedules  ON tr_schedule = sc_id' +
' INNER JOIN Members    ON mb_tertulia = tr_id' +
' INNER JOIN Users      ON mb_user = us_id' +
' INNER JOIN EnumValues ON mb_role = nv_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid';

const queryTertuliaX = queryTertulias + ' AND tr_id = @tertulia';

router.use(function timeLog(req, res, next) {
	console.log('Time: ', Date.now());
	next();
});

router.get('/', function(req, res) {
    console.log(req);
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
                }
            );
        });
    });
});

router.get('/:id', function(req, res) {
	var tr_id = req.params.id;
	res.status(statusOK)
		.send('Tertulia ' + tr_id + ' details.');
});

router.get('/:id/defaultlocation', function(req, res) {
	var tr_id = req.params.id;
	res.status(statusOK)
		.send('Tertulia ' + tr_id + ' default location.');
});

router.get('/:id/locations', function(req, res) {
	var tr_id = req.params.id;
	res.status(statusOK)
		.send('Tertulia ' + tr_id + ' locations list.');
});

router.get('/:id/members', function(req, res) {
	var tr_id = req.params.id;
	res.status(statusOK)
		.send('Tertulia ' + tr_id + ' members list.');
});

router.get('/:id/owner', function(req, res) {
	var tr_id = req.params.id;
	res.status(statusOK)
		.send('Tertulia ' + tr_id + ' owner.');
});

router.get('/:id/events', function(req, res) {
	var tr_id = req.params.id;
	res.status(statusOK)
		.send('Tertulia ' + tr_id + ' events list.');
});

router.get('/:id/nextevent', function(req, res) {
	var tr_id = req.params.id;
	res.status(statusOK)
		.send('Tertulia ' + tr_id + ' next event.');
});

module.exports = router;
