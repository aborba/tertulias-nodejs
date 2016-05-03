var util = require('../util');
var qs = require('querystring');

var api = {

    all: function (req, res, next) {
        console.log('In: api/tertulias_vw');
        next();
    },

    get: function (req, res, next) {
        util.dumpObj(req.azureMobile.user.id);
        var sqlStr = "SELECT * FROM Tertulias_Vw WHERE userAlias=@uId";
        var uid = req.azureMobile.user.id;
        uid = 'aborba';

        var parametersArr = [{
                uId: 'sid\\:' + uid.slice(4)
            }]
        parametersArr = [{
                uId: uid
            }]
        var query = {
            sql: sqlStr,
            parameters: parametersArr
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
