var util = require('../util');
var qs = require('querystring');

var api = {

    all: function (req, res, next) {
        console.log('In: api/tertulias_vw');
        next();
    },

    get: function (req, res, next) {
        console.log(req.azureMobile.user.id);
        var query = {
            sql: 'SELECT * FROM Tertulias_Vw WHERE tertuliaPrivate=0 OR userId=@userId',
            parameters: [
                { name: 'privacy', type: 'number', value: '0'},
                { name: 'userId', type: 'string', value: req.azureMobile.user.id }
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
    }

};

api.access = 'authenticated';
module.exports = api;
