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

mobile.api.import('./api');

function calculateAndRespond(x, y, op, res) {
    var result = 3;
    switch (op) {
        case 'add':
            result = parseInt(x, 10) + parseInt(y, 10);
            break;
        case 'sub':
            result = parseInt(x, 10) - parseInt(y, 10);
            break;
        default:
            res.send(400, 'Error');
    }
    if (op === 'add') {
        result = parseInt(x, 10) + parseInt(y, 10);
    } else if (op === 'sub') {
        result = parseInt(x, 10) - parseInt(y, 10);
    } else {
        res.send(400, { error: 'Operation "' + op + '" not supported' });
    }
    res.send(200, { result: result });
}

function getImplementation(req, res) {
    console.log('In calculator');
    console.log('operation = ' + operation);
    console.log('x = ' + x);
    console.log('y = ' + y);
    var x = parseInt(req.query.x, 10);
    var y = parseInt(req.query.y, 10);
    var path = req.path;
    var operation = path.substring('/api/calculator/'.length);
    calculateAndRespond(x, y, operation, res);
}

exports.register = function (api) {
    console.log('Registering');
    api.get('*', getImplementation);
}

console.log('Initializing...');
mobile.tables.initialize()
    .then(function () {
        //util.dumpObj(mobile);
        console.log('Registering the Azure Mobile Apps middleware.');
        app.use(mobile);
        console.log('Listening for requests.');
        app.listen(process.env.PORT || 3000);
    });
