var api = {
    get: function (req, res, next) {
        var date = { currentTime: Date.now() };
        res.status(200).type('application/json');
        res.send(date);
    }
};

api.get.access = 'anonymous';	// anonymous | authenticated
//api.post.access = 'authenticated';

module.exports = api;
