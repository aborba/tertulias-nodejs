var appVersion = 'A';

var util = require('./util');
util.logTertulias2(appVersion);
util.nodeVersion();

var express = require('express'),
    azureMobileApps = require('azure-mobile-apps'),
    tertuliasApi = require('./api/tertuliasApi');

var app = express();

var appConfiguration = {   // http://azure.github.io/azure-mobile-apps-node/global.html#configuration
    debug: true,
    homePage: true,
    swagger: false,
    skipVersionCheck: true
}

var mobile = azureMobileApps(appConfiguration);

mobile.api.import('./api');

console.log('Initializing...');
mobile.tables
.initialize()
.then(function () {
    //util.dumpObj(mobile);
    console.log('Registering the Azure Mobile Apps middleware.');
    app.use(mobile);
//    app.use('/api/home', homeApi(mobile.configuration));
    app.use('/api/tertulias', tertuliasApi(mobile.configuration));
    console.log('Listening for requests.');
    app.listen(process.env.PORT || 3000);
});
