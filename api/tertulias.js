var api = {

    get: (request, response, next) => {

        var query = {
            sql: 'SELECT * FROM Tertulias WHERE private = FALSE'
        };
/*
        var query = {
            sql: 'UPDATE TodoItem SET complete = @completed',
            parameters: [
                { name: 'completed', value: request.query.completed }
            ]
        };
*/
        request.azureMobile.data.execute({
            sql: 'SELECT * FROM Tertulias WHERE private = @privacy',
            parameters: [
                { name: 'privacy', value: 'FALSE' }
            ]
        })
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