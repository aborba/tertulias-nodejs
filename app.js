var appVersion = 'A';

var util = require('./util');
util.logTertulias2(appVersion);
util.nodeVersion();

var express = require('express'),
    azureMobileApps = require('azure-mobile-apps');

var app = express();

var appConfiguration = {   // http://azure.github.io/azure-mobile-apps-node/global.html#configuration
    debug: true,
    homePage: true,
    swagger: false,
    skipVersionCheck: true
}

var mobile = azureMobileApps(appConfiguration);

// region My experiment
var bodyParser = require('body-parser');
var router = express.Router();
router.get('/', function(req, res) {
    res.send('Im the home page!');
});
router.get('/about', function(req, res) {
    res.send('Im the about page!');
});
router.get('/hello/:name', function(req, res) {
    res.send('Hello' +  req.params.name + '!');
});


router.use(bodyParser.json());
app.use('/', router);

// endregion My experiment

mobile.api.import('./api');

console.log('Initializing...');
mobile.tables.initialize()
    .then(function () {
        //util.dumpObj(mobile);
        console.log('Registering the Azure Mobile Apps middleware.');
        app.use(mobile);
        console.log('Listening for requests.');
        app.listen(process.env.PORT || 3000);
    });
