var api = {
	get: function (req, res, next) {
		res.json( { "links": [
			{ "rel": "tertulias", "method": "GET", "href": "/tertulias" },
			{ "rel": "registration", "method": "POST", "href": "/me" }
		] } );
		next();
    }
};

api.access = 'authenticated';

module.exports = api;
