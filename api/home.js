var api = {
	get: function (req, res, next) {
		var routes = { "_links": {
			"tertulias": { "href" : "/tertulias" } }
		};
		console.log(routes);
		res.json(routes);
		next();
    }
};

api.access = 'authenticated';

module.exports = api;
