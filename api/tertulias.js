var util = require('../util');
var u = require('azure-mobile-apps/src/auth/user');
var sql = require('mssql');

var querySelectTertulias = 'SELECT DISTINCT * FROM Tertulias WHERE private=0' +
    ' UNION' +
        ' SELECT DISTINCT Tertulias.* FROM Tertulias INNER JOIN Members ON Tertulias.id=Members.tertulia' +
            ' INNER JOIN Users ON Members.usr=Users.id' +
        ' WHERE private=1 AND Users.sid=@sid;';

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
/*
    ,

    post: function (req, res, next) {
        console.log('In: post');
        console.log(req.azureMobile.user.id);
        console.log(req.body);
        var query = {
            sql: 'INSERT INTO Terulias(title, subject, schedule, privacy) VALUES (@title, @subject, @schedule, @privacy)',
            parameters: [
                {name: 'title', value: req.body.title || ""},
                {name: 'subject', value: req.body.subject || ""},
                {name: 'schedule', value: req.body.schedule || "0"},
                {name: 'privacy', value: req.body.privacy || "0"},
            ]
        };
        console.log(query);
        var query1 = {
            sql: 'SELECT id FROM Users WHERE sid=@userSid',
            parameters: [
                {name: 'userSid', value: req.azureMobile.user.id }
            ]
        };
        console.log(query1);
        var query2 = {
            sql: 'SELECT id FROM Roles WHERE roleName=@roleName',
            parameters: [
                {name: 'roleName', value: 'admin' }
            ]
        };
        console.log(query2);
        var query3 = {
            sql: 'INSERT INTO Members(tertulia, usr, role) VALUES (@tertuliaId, @usrId, @roleId)',
            parameters: [
                {name: 'tertuliaId', value: req.body.title || ""},
                {name: 'usrId', value: req.body.subject || ""},
                {name: 'roleId', value: req.body.schedule || "0"},
            ]
        };
        console.log(query3);
        res.sendStatus(200);
        return next();
    }
*/

};

api.access = 'authenticated';
module.exports = api;
