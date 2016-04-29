var api = {
    get: function (req, res, next) {
        console.log('In TodoItem get');
        
    	var control = 1;

/*
        if (typeof req.params.completed === 'undefined') {
        	console.log('-' + control++ + ': No completed parameter');
        	res.json(result: 'none');
        	return next();
        }
    	console.log('-' + control++ + ': completed parameter: ' + req.params.completed);
        var query = {
            sql: 'UPDATE TodoItem SET complete=@completed',
            parameters: [{
                completed: req.params.completed
            }]
        };
    	console.log('-' + control++ + ': query: ' + query);
*/

        var query = {
            sql: 'UPDATE TodoItem SET complete=@completed',
            parameters: [
                { completed: '1' }
            ]
        };
        req.azureMobile.data.execute(query).then(function(results) {
        	res.json(results);
        });
    }
};

//api.post.access = 'authenticated';
module.exports = api;
