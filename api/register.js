var util = require('../util');
var u = require('azure-mobile-apps/src/auth/user');
var sql = require('mssql');

var api = {

    all: function (req, res, next) {
        console.log('In: api/register');
        return next();
    },

    get: function (req, res, next) {
    	console.log('in GET');
    },

    post: function (req, res, next) {
    	console.log('in POST');
        var connection = new sql.Connection(util.sqlConfiguration);
        connection.connect(function(err) {
            if (err) {console.log(isError); res.sendStatus(500); return; }
            var transaction = new sql.Transaction(connection);
            transaction.begin(function(err) {
                if (err) { transaction.rollback(); return; }
                var rolledback = false;
                transaction.on('rollback', function(aborted) { rolledback = true; res.sendStatus(500); });
                var sqlRequest = new sql.Request(transaction);
                var _userId = '';
                var queryString = 'SELECT id FROM Users WHERE sid=@sid;';
                var preparedStatement_0 = new sql.PreparedStatement(connection);
                //transaction.on('commit', function(succeeded) { preparedStatement_0.unprepare(); res.sendStatus(200); });
                //transaction.on('rollback', function(aborted) { rolledback = true; preparedStatement_0.unprepare(); res.sendStatus(500); });
                preparedStatement_0.input('sid', sql.NVarChar);
                preparedStatement_0.prepare(queryString, function(err) {
                    if (err) { transaction.rollback(); return; }
                    preparedStatement_0.execute({ sid: req.azureMobile.user.id }, 
                        function(err, recordset, affected) {
                            if (err) {transaction.rollback(); return; }
                            if (recordset) {transaction.commit(); return; }
                            var preparedStatement_1 = new sql.PreparedStatement(connection);
                            /*
			                transaction.on('commit', function(succeeded) {
			                	preparedStatement_0.unprepare();
			                	preparedStatement_1.unprepare();
			                	res.sendStatus(200);
			                });
			                transaction.on('rollback', function(aborted) {
			                	rolledback = true;
			                	preparedStatement_0.unprepare();
			                	preparedStatement_1.unprepare();
			                	res.sendStatus(500);
			                });
			                */
                            queryString = 'INSERT INTO Users (sid) values (@sid);';
			                preparedStatement_1.input('sid', sql.NVarChar);
			                preparedStatement_1.prepare(queryString, function(err) {
			                    if (err) { transaction.rollback(); return; }
			                    preparedStatement_1.execute({ sid: req.azureMobile.user.id }, 
			                        function(err, recordset, affected) {
					                    if (err) { transaction.rollback(); return; }
					                    transaction.commit();
			                        }
		                        );
			                });
                        }
                    );
                 });
                transaction.rollback();
            });
        });

    	// FORMER
    	/*
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
        */
    }
};

api.access = 'authenticated';
module.exports = api;
