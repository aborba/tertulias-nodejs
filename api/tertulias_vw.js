var util = require('../util');
var qs = require('querystring');

var api = {

    all: function (req, res, next) {
        console.log('In: api/tertulias_vw');
        next();
    },

    get: function (req, res, next) {
        util.dumpObj(req.azureMobile.user.id);
        var sqlStr = 'SELECT * FROM Tertulias_Vw WHERE userId=@uId';
        var uidOk = '\'sid\:fadae567db0f67c6fe69d25ee8ffc0b5\'';
        var uid = req.azureMobile.user.id;
        var uid1 = uid.replace(/:/, "\\:");
        var uid2 = '\'sid\:' + uid.slice(4) + '\'';
        console.log('uidOk:' + uidOk);
        console.log('uid:' + uid);
        console.log('uid1:' + uid1);
        console.log('uid2:' + uid2);
        console.log('uidOk=uid? ' + uidOk.localeCompare(uid));
        console.log('uidOk=uid1? ' + uidOk.localeCompare(uid1));
        console.log('uidOk=uid2? ' + uidOk.localeCompare(uid2));

    	var parametersArr = [{
                uId: uid2
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
