var express = require('express');
var azureMobileApps = require('azure-mobile-apps');

var app = express();

// If you are producing a combined Web + Mobile app, then you should handle
// anything like logging, registering middleware, etc. here

// http://azure.github.io/azure-mobile-apps-node/global.html#configuration
var mobileApp = azureMobileApps({
    homePage: false,
    swagger: false // UI support is enabled by installing the swagger-ui npm module.
});

mobileApp.tables.import('./tables');
mobileApp.api.import('./api');

mobileApp.tables.initialize().then(function() {
    app.use(mobileApp);    // Register the Azure Mobile Apps middleware
    app.listen(process.env.PORT || 3000);   // Listen for requests
});
