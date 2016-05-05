var util = require('../util');
var qs = require('querystring');
var u = require('azure-mobile-apps/src/auth/user');
var sql = require('mssql');

var api = {

    all: function (req, res, next) {
        console.log('In: api/tertulias');
        return next();
    },

    get: function (req, res, next) {

        var connection = new sql.Connection(util.sqlConfiguration);

        connection.connect(function(err) {
            if (err) {
                console.log('An error ocurred while connecting to the database. Aborting');
                console.log(isError);
                res.sendStatus(500);
                return;
            }

            var transaction = new sql.Transaction(connection);

            transaction.begin(function(err) {

                if (err) { transaction.rollback(); return; }

                var rolledback = false;
                transaction.on('rollback', function(aborted) { rolledback = true; });

                var sqlRequest = new sql.Request(transaction);

                var _userId = "";

                var queryString = 'SELECT id FROM Users WHERE sid=@sid;';

                var preparedStatement = new sql.PreparedStatement();

                preparedStatement.input('sid', sql.String);

                transaction.rollback();
            });

        });

    },

    post: function (req, res, next) {
        console.log('In: post');
        console.log(req.azureMobile.user.id);
        console.log(req.body);
        console.log('req.azureMobile.connection');
        console.log(req.azureMobile.connection);
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

};

api.access = 'authenticated';
module.exports = api;
