var api = {
    get: function (request, response, next) {
        if (typeof request.params.completed === 'undefined') return next();
        var query = {
            sql: 'UPDATE TodoItem SET complete=@completed',
            parameters: [{
                completed: request.params.completed
            }]
        };

        request.azureMobile.data.execute(query)
        .then(function (results) {
            response.json(results);
        });
    }
};

api.post.access = 'authenticated';
module.exports = api;
