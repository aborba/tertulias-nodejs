var util = require('../util');
var qs = require('querystring');

var api = {

    all: function (req, res, next) {
        console.log('In: api/tertulias_vw');
        return next();
    },

    get: function (req, res, next) {
        console.log('In: get');
        console.log(req.azureMobile.user.id);
        var query = {
            sql: 'SELECT * FROM Tertulias_Vw WHERE tertuliaPrivate=@privacy OR userId=@userId',
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
        res.status(200);
        return next();
    }

};

api.access = 'authenticated';
module.exports = api;
