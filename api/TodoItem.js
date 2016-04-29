var api = {

    all: function (req, res, next) {
        console.log('In: TodoItem');
        next();
    },

    get: function (req, res, next) {
        var query = {
            sql: 'SELECT text FROM TodoItem;'
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
