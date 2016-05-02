var util = require('../util');

var api = {

    all: function (req, res, next) {
        console.log('In: api/tertulias_vw');
        next();
    },

    get: function (req, res, next) {
        util.dumpObj(req.azureMobile.user.id);
        var sqlStr = 'SELECT * FROM Tertulias_Vw WHERE userId=@userId';
        sqlStr = 'SELECT * FROM Tertulias_Vw WHERE userId='sid:fadae567db0f67c6fe69d25ee8ffc0b5' OR tertuliaPrivate=0'
    	var parametersArr = [{
                userId: req.azureMobile.user.id
            }]
        var query = {
            sql: sqlStr,
            parameters: parametersArr
        };
        console.log(query);
        req.azureMobile.data.execute(query).then(function(results) {
            console.log('results');
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
