var appVersion = 'A';

var util = require('./util');
util.logTertulias2(appVersion);
util.nodeVersion();

var express = require('express'),
    azureMobileApps = require('azure-mobile-apps');
//me
var bodyParser = require('body-parser');

var app = express();

var appConfiguration = {   // http://azure.github.io/azure-mobile-apps-node/global.html#configuration
    debug: true,
    homePage: true,
    swagger: false,
    skipVersionCheck: true
}

//me
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

var mobile = azureMobileApps(appConfiguration);

mobile.api.import('./api');

/*
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

mobile.api.register = function (api) {
    console.log('Registering');
    api.get('*', getImplementation);
}
*/

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
    var operation = path.substring('/calculator/'.length);
    console.log(operation);
    calculateAndRespond(x, y, operation, res);
}

function getImplementation2(req, res) {
    console.log('In Implementation2');
    res.send(200, { result: 'OK' });
}


console.log('Initializing...');
mobile.tables
.initialize()
.then(function () {
    //util.dumpObj(mobile);
    console.log('Registering the Azure Mobile Apps middleware.');
    app.use(mobile);
    exports.register = function (api) {
        console.log('Registering');
        api.get('calculator', getImplementation2);
    };
    console.log('Listening for requests.');
    app.listen(process.env.PORT || 3000);
});
