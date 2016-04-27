var api = {

    get: (request, response, next) => {

        var query = {
            sql: 'UPDATE TodoItem SET complete = @completed',
            parameters: [
                { name: 'completed', value: request.query.completed }
            ]
        };

        request.azureMobile.data
        .execute(query)
        .then(function (results) {
            response.json(results);
        });
    },

    post: (request, response, next) => {
        var query = {
            sql: 'EXEC completeAllStoredProcedure @completed',
            parameters: [
                // { name: 'completed', value: request.query.completed }
                { name: 'completed', value: 1 }
            ]
        };

        request.azureMobile.data
        .execute(query)
        .then(function (results) {
            response.json(results);
        });
    }

};

module.exports = api;