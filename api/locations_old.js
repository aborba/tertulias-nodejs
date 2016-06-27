var util = require('../util');
var u = require('azure-mobile-apps/src/auth/user');
var sql = require('mssql');

var query = 'SELECT DISTINCT lo_id, lo_name, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude, lo_tertulia' +
' FROM Locations' +
    ' INNER JOIN Tertulias ON lo_tertulia = tr_id' +
    ' INNER JOIN Members ON mb_tertulia = tr_id' +
    ' INNER JOIN Users ON mb_user = us_id' +
' WHERE tr_is_cancelled = 0' +
    ' AND us_sid = @sid' +
    ' AND tr_name = @tertulia';

var completeError = function(err, res) {
    if (err) {
        console.error(err);
        if (res) res.sendStatus(500);
    }
};

var api = {

    all: function (req, res, next) {
        console.log('In: api/tertulias');
        return next();
    },

    get: function (req, res, next) {
        var connection = new sql.Connection(util.sqlConfiguration);
        connection.connect(function(err) {
            var sqlRequest = new sql.Request(connection);
            var preparedStatement = new sql.PreparedStatement(connection);
            /* String -> sql.NVarChar; Number -> sql.Int; Boolean -> sql.Bit; Date -> sql.DateTime;
               Buffer -> sql.VarBinary; sql.Table -> sql.TVP */
            preparedStatement.input('sid', sql.NVarChar);
            preparedStatement.input('tertulia', sql.NVarChar);
            preparedStatement.prepare(query, function(err) {
                if (err) { completeError(err, res); return; }
                preparedStatement.execute({ sid: req.azureMobile.user.id, tertulia: 'Tertulia do Tejo' }, 
                    function(err, recordset, affected) {
                        if (err) { completeError(err, res); return; }
                        console.log(recordset);
                        preparedStatement.unprepare();
                        res.type('application/json').json(recordset);
                    }
                );
             });
        });
    }

};

api.access = 'authenticated';
module.exports = api;