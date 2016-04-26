var api = {
    get: function (request, response, next) {
        // Check for parameters - if not there, pass on to a later API call
        if (typeof request.params.completed === 'undefined') return next();

        // Define the query - anything that can be handled by the mssql driver is allowed.
        var query = {
            sql: 'UPDATE TodoItem SET complete = @completed',
            parameters: [ {completed: request.params.completed} ]
        };

        // Execute the query. The context for Azure Mobile Apps is available through request.azureMobile - the data object contains the configured data provider.
        request.azureMobile.data.execute(query).then(function (results) {
            response.json(results);
        });
    }
};

api.get.access = 'authenticated';

module.exports = api;
