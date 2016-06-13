var express = require('express');
var router = express.Router();

var statusOK = 200;

router.use(function timeLog(req, res, next) {
	console.log('Time: ', Date.now());
	next();
});

router.get('/', function(req, res) {
	res.status(statusOK)
		.send('Tertulias list');
});

router.get('/:id', function(req, res) {
	var id = 0;
	res.status(statusOK)
		.send('Tertulia ' + id + ' details.');
});

router.get('/:id/defaultlocation', function(req, res) {
	var id = 0;
	res.status(statusOK)
		.send('Tertulia ' + id + ' default location.');
});

router.get('/:id/locations', function(req, res) {
	var id = 0;
	res.status(statusOK)
		.send('Tertulia ' + id + ' locations list.');
});

router.get('/:id/members', function(req, res) {
	var id = 0;
	res.status(statusOK)
		.send('Tertulia ' + id + ' members list.');
});

router.get('/:id/owner', function(req, res) {
	var id = 0;
	res.status(statusOK)
		.send('Tertulia ' + id + ' owner.');
});

router.get('/:id/events', function(req, res) {
	var id = 0;
	res.status(statusOK)
		.send('Tertulia ' + id + ' events list.');
});

router.get('/:id/nextevent', function(req, res) {
	var id = 0;
	res.status(statusOK)
		.send('Tertulia ' + id + ' next event.');
});

module.exports = router;
