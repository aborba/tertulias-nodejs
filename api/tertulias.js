var util = require('../util');
var u = require('azure-mobile-apps/src/auth/user');
var sql = require('mssql');

var querySelectTertulias = 'SELECT DISTINCT Tertulias.* FROM Tertulias' +
        ' INNER JOIN Members ON tr_id=mb_tertulia' +
        ' INNER JOIN Users ON mb_user=us_id';// +
    //' WHERE us_sid=@sid';

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
    }

};

api.access = 'authenticated';
module.exports = api;
