var api = {
	get: function (req, res, next) {
		res.json( {
			"_links": {
				"GET_TERTULIAS": { "href" : "/tertulias" },
				"POST_REGISTRATION": { "href" : "/register" }
			}
		});
		next();
    }
};

api.access = 'authenticated';

module.exports = api;
