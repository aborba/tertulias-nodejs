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
        
        connection.connect(function(isError) {

            if (isError) {
                console.log('An error ocurred while connecting to the database. Aborting');
                console.log(isError);
                res.sendStatus(500);
                return;
            }

            var transaction = new sql.Transaction(connection);

            transaction.begin(function(err) {

                if (err) {
                    transaction.rollback();
                    return;
                }

                var rolledback = false;

                transaction.on('rollback', function(aborted) {
                    rolledback = true;
                })

                var sqlRequest = new sql.Request(transaction);

                var _userId = "";

                console.log('req.azureMobile.user.id: ' + req.azureMobile.user.id);

                var preparedStatement = new sql.PreparedStatement();

                preparedStatement.input('sid', sql.String);

                preparedStatement.prepare('SELECT id FROM Users WHERE sid=@sid;', funtion(err) {
                    preparedStatement.execute({sid: req.azureMobile.user.id}, function(err, recordset, affected) {
                        console.log('preparedStatement result');
                        console.log(recordset);
                    });
                    preparedStatement.unprepare(function(err) {
                    });
                });

                sqlRequest.query('SELECT id FROM Users WHERE sid=\'sid:fadae567db0f67c6fe69d25ee8ffc0b5\';')
                .then(function(rs) {
                    _userId = rs[0].id;
                    return _userId;
                }).then(function(par) {
                    console.log("par: " + par);
                    console.log("Read User Id (1): " + _userId);
                });
                
                console.log("Read User Id (0): " + _userId);


                // TO HERE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                var request = new sql.Request(transaction);
                console.log('request built');
                request.query('SELECT DISTINCT tertuliaId AS id, ' +
                          'tertuliaTitle AS title, ' +
                          'tertuliaSubject AS subject, ' +
                          'tertuliaSchedule AS schedule, ' +
                          'tertuliaPrivate AS private ' +
                      'FROM Tertulias_Vw ' +
                      'WHERE tertuliaPrivate=0',
                    function(err, recordset) {
                        console.log('select result');
                        if (err) {
                            if (!rolledBack) {
                                console.log('trying to rollback');
                                transaction.rollback(function(err) {
                                    console.log('rolling back');
                                });
                            }
                        } else {
                            transaction.commit(function(err) {
                                if (err) {
                                    if (!rolledBack) {
                                        console.log('trying to rollback');
                                        transaction.rollback(function(err) {
                                            console.log('rolling back');
                                        });
                                    }
                                }
                                console.log('committed');
                            });
                            console.log('recordset');
                            console.log(recordset);
                        }
                    });
                if (err) {
                    console.log('trying to rollback');
                    transaction.rollback();
                }
            });
        });

        console.log('TRANSACTION TESTS END');
        console.log('==============================================================================================');
/*
        var query = {
            sql: 'SELECT DISTINCT tertuliaId AS id, ' +
                      'tertuliaTitle AS title, ' +
                      'tertuliaSubject AS subject, ' +
                      'tertuliaSchedule AS schedule, ' +
                      'tertuliaPrivate AS private ' +
                  'FROM Tertulias_Vw ' +
                  'WHERE tertuliaPrivate=@privacy OR userId=@userId',
            parameters: [
                { 'name': 'privacy', 'value': '0'},
                { 'name': 'userId', 'value': req.azureMobile.user.id }
            ]
        };
        console.log(query);
        req.azureMobile.data.execute(query).then(function(results) {
            console.log('results');
            console.log(results);
            res.status(200)
                .type('application/json')
                .json(results);
            return next();
        });
        */
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
