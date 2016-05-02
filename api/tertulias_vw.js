var util = require('../util');

var api = {

    all: function (req, res, next) {
        console.log('In: api/tertulias_vw');
        next();
    },

    get: function (req, res, next) {
        util.dumpObj(req.azuremobile.user);
        util.dumpObj(req.user);
        util.dumpObj(res.user);
        var sqlStr = 'SELECT * FROM Tertulias_Vw WHERE private = 0 OR userId=@userId;';
    	var parametersArr = [{
                userId: 'context.user.userId'
            }]
        var query = {
            sql: sqlStr,
            parameters: parametersArr
        };
        console.log(query);
        req.azureMobile.data.execute(query).then(function(results) {
            res.status(200)
            .type('application/json')
        	.json(results);
            return next();
        });
    }

};

api.access = 'authenticated';
module.exports = api;
