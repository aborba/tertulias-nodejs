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
            if (err) {console.log('control point 0'); console.log(isError); res.sendStatus(500); return; }
            console.log('control point 1'); 
            var transaction = new sql.Transaction(connection);
            console.log('control point 2'); 
            transaction.begin(function(err) {
            console.log('control point 3');
                if (err) { console.log('control point 4'); transaction.rollback(); return; }
	            console.log('control point 5');
                var rolledback = false;
	            console.log('control point 6');
                transaction.on('rollback', function(aborted) { console.log('control point 7'); rolledback = true; res.sendStatus(500); });
            	console.log('control point 8');
                var sqlRequest = new sql.Request(transaction);
            	console.log('control point 9');
                var _userId = '';
            	console.log('control point 10');
                var queryString = 'SELECT id FROM Users WHERE sid=@sid;';
            	console.log('control point 11');
                var preparedStatement_0 = new sql.PreparedStatement(connection);
            	console.log('control point 12');
                //transaction.on('commit', function(succeeded) { preparedStatement_0.unprepare(); res.sendStatus(200); });
                //transaction.on('rollback', function(aborted) { rolledback = true; preparedStatement_0.unprepare(); res.sendStatus(500); });
            	console.log('control point 13');
                preparedStatement_0.input('sid', sql.NVarChar);
            	console.log('control point 14');
                preparedStatement_0.prepare(queryString, function(err) {
            		console.log('control point 15');
                    if (err) { console.log('control point 16'); transaction.rollback(); return; }
            		console.log('control point 17');
                    preparedStatement_0.execute({ sid: req.azureMobile.user.id }, 
                        function(err, recordset, affected) {
                        	console.log('control point 18'); 
                            if (err) {console.log('control point 19'); transaction.rollback(); return; }
                            if (typeof recordset != 'undefined' && recordset[0] != null) {console.log('control point 20'); console.log(recordset); transaction.commit(); return; }
                            if (typeof recordsetnone != 'undefined' && recordsetnone[0] != null) {console.log('control point 20'); console.log(recordset); transaction.commit(); return; }
							console.log('control point 21');
                            var preparedStatement_1 = new sql.PreparedStatement(connection);
							console.log('control point 22');
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
							console.log('control point 23');
			                preparedStatement_1.input('sid', sql.NVarChar);
							console.log('control point 24');
			                preparedStatement_1.prepare(queryString, function(err) {
			                	console.log('control point 25');
			                    if (err) { console.log('control point 26'); transaction.rollback(); return; }
								console.log('control point 27');
			                    preparedStatement_1.execute({ sid: req.azureMobile.user.id }, 
			                        function(err, recordset, affected) {
			                        	console.log('control point 28'); 
					                    if (err) { console.log('control point X'); transaction.rollback(); return; }
			                        	console.log('control point 29'); 
					                    transaction.commit();
			                        	console.log('control point 30'); 
			                        }
		                        );
			                });
                        }
                    );
                 });
                console.log('control point 31'); 
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
