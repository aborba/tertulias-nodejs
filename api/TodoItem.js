var api = {

    all: function (req, res, next) {
        console.log('In: TodoItem');
        console.log('In: ' + util.objName(api));
        next();
    },

    get: function (req, res, next) {
        var query = {
            sql: 'UPDATE TodoItem SET complete=@completed',
            parameters: [
                { completed: '1' }
            ]
        };
        req.azureMobile.data.execute(query).then(function(results) {
        	res.json('Ok');
        });
    }

};

//api.post.access = 'authenticated';
module.exports = api;
