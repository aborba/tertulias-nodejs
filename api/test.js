var express = require('express'),
    azureMobileApps = require('azure-mobile-apps');

var app = express();

var router = express.Router();

router.param('name', function(req, res, next, name) {
    console.log('doing first name validations on ' + name);
    req.name = name;
    next(); 
});

router.get('/', function(req, res) {
    res.send('Im the test home page!');
});

router.get('/about', function(req, res) {
    res.send('Im the test about page!');
});

router.get('/hello/:name', function(req, res) {
    res.send('Hello ' +  req.params.name + '!');
});

