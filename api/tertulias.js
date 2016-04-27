var api = {

    get: (request, response, next) => {
        var query = {
            sql: 'SELECT Tertulias.id AS id, Tertulias.title AS title FROM ((Tertulias INNER JOIN Members ON Tertulias.id = Members.tertulia) INNER JOIN Users ON Members.usr = Users.id) WHERE (Users.id = @userId OR Tertulias.private = @privacy)',
            parameters: [
//                { name: 'userId', value: request.query.userId },
                { name: 'userId', value: '4562FB8E-973A-439D-BDD6-D7FEACA29C17' },
                { name: 'privacy', value: 0 }
            ]
        };
        /*
        if (request.params.tertuliaId != 'undefined') {
            query += ' AND Tertulias.id = @tertuliaId';
            queryParams.push({ name: 'tertuliaId', value: request.params.tertuliaId });
        }

        Console.log (query);
        Console.log (queryParams);
        */

        request.azureMobile.data.execute(query)
        .then(function (results) {
            response.json(results);
        });
    }
*/

/*
    router.post('/:category/:id', (request, response, next) => {
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
    });
*/

};
 
module.exports = api;
