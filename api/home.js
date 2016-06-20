var api = {
	get: function (req, res, next) {
		var routes = { "_links": {
			"tertulias": { "href" : "/tertulias" } }
		};
		res.json(routes);
		next();
    }
};

api.access = 'authenticated';

module.exports = api;
