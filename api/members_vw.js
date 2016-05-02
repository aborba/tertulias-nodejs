var util = require('../util');

var api = {

    all: function (req, res, next) {
        console.log('In: api/membersvw');
        next();
    },

    get: function (req, res, next) {
    	var sqlStr = 'SELECT * FROM Members_Vw WHERE private = 0 OR userId=@userId;';
//    	util.dumpObj(req);
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
