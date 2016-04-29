var api = {
    get: function (request, response, next) {
    	var passCount = 1;
		console.log('Passed here ' + passCount++);
		console.log(request.params);
		console.log('Passed here ' + passCount++);

        if (typeof request.params.completed === 'undefined') return next();
		console.log('Passed here ' + passCount++);
        var query = {
            sql: 'UPDATE TodoItem SET complete=@completed',
            parameters: [{
                completed: request.params.completed
            }]
        };
		console.log('Passed here ' + passCount++);

        request.azureMobile.data.execute(query);
		console.log('Passed here ' + passCount++);
    }
};

//api.post.access = 'authenticated';
module.exports = api;
