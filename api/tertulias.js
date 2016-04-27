var api = {

    get: (request, response, next) => {

/*
        var query = {
            sql: 'UPDATE TodoItem SET complete = @completed',
            parameters: [
                { name: 'completed', value: request.query.completed }
            ]
        };
            parameters: [
                { name: 'userId', value: '63C0085B-C1F1-4217-93A6-33DA7E592DD0' },
                { name: 'privacy', value: 0 }
            ]
*/
        request.azureMobile.data.execute({
            sql: 'SELECT Tertulias.id AS id, Tertulias.title AS title \
            FROM ((Tertulias INNER JOIN Members ON Tertulias.id = Members.tertulia) \
            INNER JOIN Users ON Members.usr = Users.id)\
            WHERE Users.id = @userId OR Tertulias.private = @privacy;',
            parameters: [
                { name: 'userId', value: request.query.userId },
                { name: 'privacy', value: 0 }
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