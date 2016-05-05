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
        console.log('==============================================================================================');
        console.log('TRANSACTION TESTS');

        console.log('connecting');

        var configuration = {
            user: 'aborba@tertulias',
            password: 'Apples123',
            server: 'tertulias.database.windows.net',
            database: 'tertulias',
            options: { encrypt: true }
        };

        var connection = new sql.Connection(configuration);
        
        connection.connect(function(isError) {
            if (isError) {
                console.log('An error ocurred while connecting to the database. Aborting');
                console.log(isError);
                res.sendStatus(500);
                return;
            }
            console.log('connected');

            var transaction = new sql.Transaction(connection);
            console.log('beginning transaction');
            transaction.begin(function(err) {
                var rolledback = false;
                transaction.on('rollback', function(aborted) {
                    console.log('rolling back - on');
                    rolledback = true;
                })
                console.log('building request');

                // FROM HERE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                var request0 = new sql.Request(transaction);
                var _userId = "";
                request0.query('SELECT id FROM Users WHERE sid=\'sid:fadae567db0f67c6fe69d25ee8ffc0b5\';')
                .then(function(rs) {
                    console.log(rs);
                    console.log(rs[0]);
                    console.log(rs[0].id);
                    _userId = rs[0].id;
                }).then(function() {
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
        var x = req.azureMobile.user.getIdentity({
            success: function (identities) {
                console.log('ok');
                console.log(identities);
                var request = require('request');
                if (identities.google) {
                    var googleAccessToken = identities.google.accessToken;
                    var url = 'https://www.googleapis.com/oauth2/v3/userinfo?access_token=' + googleAccessToken;
                    request(url, function (err, resp, body) {
                        if (err || resp.statusCode !== 200) {
                            console.error('Error sending data to Google API: ', err);
                            request.respond(statusCodes.INTERNAL_SERVER_ERROR, body);
                        } else {
                            try {
                                var userData = JSON.parse(body);
                                console.log('userdata: ' + userData);
                                item.UserName = userData.name;
                                request.execute();
                            } catch (ex) {
                                console.error('Error parsing response from Google API: ', ex);
                                request.respond(statusCodes.INTERNAL_SERVER_ERROR, ex);
                            }
                        }
                    });
                } else {
                    // Insert with default user name
                    request.execute();
                }
            },
            error: function(error) {
                console.log('error: ' + error);
            }
        });

        console.log(x);
*/
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
