var api = {

    all: function (req, res, next) {
        console.log('In: TodoItem');
        next();
    },

    get: function (req, res, next) {
        var query = {
            sql: 'UPDATE TodoItem SET complete=@completed',
            parameters: [
                { completed: 1 }
            ]
        };
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
