var api = {
	get: function (req, res, next) {
		res.json({
			"_links": {
				"tertulias": { "href" : "/tertulias" },
				"registration": { "href" : "/register" }
			}
		});
		next();
    }
};

api.access = 'authenticated';

module.exports = api;
