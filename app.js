var appVersion = '1';

var util = require('./util');
util.logBanner(util.tertuliasBanner, appVersion);

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

mobile.tables.import('./tables');
mobile.api.import('./api');

console.log('Initializing...');
mobile.tables.initialize()
    .then(function () {
        util.dump(mobile);
        console.log('Registering the Azure Mobile Apps middleware.');
        app.use(mobile);
        console.log('Listening for requests.');
        app.listen(process.env.PORT || 3000);
    });
