var util = require('../util');
var qs = require('querystring');

var api = {

    all: function (req, res, next) {
        console.log('In: api/tertulias_vw');
        next();
    },

    get: function (req, res, next) {
        util.dumpObj(req.azureMobile.user.id);
        var sqlStr = 'SELECT * FROM Tertulias_Vw WHERE userId=@_userId';
        var uid = 'sid\\';
        console.log(uid);
        var uid = uid + ':';
        console.log(uid);
        var uid = 'sid:fadae567db0f67c6fe69d25ee8ffc0b5'; //uid + req.azureMobile.user.id.slice(4);
        console.log(uid);
    	var parametersArr = [{
                _userId: uid
            }]
        var query = {
            sql: sqlStr,
            parameters: parametersArr
        };
        console.log(query);
        req.azureMobile.data.execute(query).then(function(results) {
            console.log(results);
            util.dump(results);
            res.status(200)
            .type('application/json')
        	.json(results);
            return next();
        });
    }

};

api.access = 'authenticated';
module.exports = api;
