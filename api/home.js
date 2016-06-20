var api = {
	get: function (req, res, next) {
		var routes = {
			"tertulias": "/tertulias"
		};
		res.json(routes);
		next();
    }
};

api.access = 'authenticated';

module.exports = api;
