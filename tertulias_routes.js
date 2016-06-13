var express = require('express');
var router = express.Router();

router.use(function timeLog(req, res, next) {
  console.log('Time: ', Date.now());
  next();
});

router.get('/', function(req, res) {
  res.send('Tertulias list');
});

router.get('/:id', function(req, res) {
  res.send('Tertulias item ');
});

router.get('/about', function(req, res) {
  res.send('About Tertulias');
});

module.exports = router;
