var util = require('../util');
var api = {

    all: function (req, res, next) {
        console.log('In: api/tertulias');
        next();
    },

    get: function (req, res, next) {
    	var sql = 'SELECT * FROM Tertulias WHERE private = 0';
    	util.dump(context);
    	var parameters = [{
                user: context.user.userId
            }]
        var query = {
            sql: 'SELECT * FROM Tertulias WHERE private = 0;',
            parameters: [
            	user
            ]
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

//api.post.access = 'authenticated';
module.exports = api;
