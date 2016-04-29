var api = {
    get: function (request, response, next) {

/*
        if (typeof request.params.completed === 'undefined') return next();
        var query1 = {
            sql: 'UPDATE TodoItem SET complete=@completed',
            parameters: [{
                completed: request.params.completed
            }]
        };

        request.azureMobile.data.execute(query1);
*/
		console.log('Passed here');
        var query2 = {
            sql: 'GET text FROM TodoItem'
        };

        request.azureMobile.data.execute(query2)
        .then(function (results) {
            response.json(results);
        });
    }
};

//api.post.access = 'authenticated';
module.exports = api;
