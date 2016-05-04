var util = require('../util');
var qs = require('querystring');
var u = require('azure-mobile-apps/src/auth/user');

var api = {

    all: function (req, res, next) {
        console.log('In: api/tertulias');
        return next();
    },

    get: function (req, res, next) {
        console.log('In: get');
        console.log(req.azureMobile.user.id);

        var query = {
            sql: 'SELECT DISTINCT tertuliaId AS id, \
                      tertuliaTitle AS title, \
                      tertuliaSubject AS subject, \
                      tertuliaSchedule AS schedule, \
                      tertuliaPrivate AS private \
                  FROM Tertulias_Vw \
                  WHERE tertuliaPrivate=@privacy OR userId=@userId',
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
    },

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

};

api.access = 'authenticated';
module.exports = api;
