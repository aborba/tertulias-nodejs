var express = require('express');

var util = require('../util');
var sql = require('mssql');

var router = express.Router();

router.get('/', function (req, res, next) {
    var connection = new sql.Connection(util.sqlConfiguration);
    connection.connect(function(err) {
        var sqlRequest = new sql.Request(connection);
        var preparedStatement = new sql.PreparedStatement(connection);
        /* String -> sql.NVarChar; Number -> sql.Int; Boolean -> sql.Bit; Date -> sql.DateTime;
           Buffer -> sql.VarBinary; sql.Table -> sql.TVP */
        preparedStatement.input('sid', sql.NVarChar);
        preparedStatement.prepare(querySelectTertulias, function(err) {
            if (err) { completeError(err, res); return; }
            preparedStatement.execute({ sid: req.azureMobile.user.id }, 
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

router.get('/page/:nr', function (req, res, next) {
	console.log('par ' + req.params.nr);
}

router.access = 'authenticated';
module.exports = router;
