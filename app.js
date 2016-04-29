var express = require('express'),
    azureMobileApps = require('azure-mobile-apps');

var app = express();

var appConfiguration = {   // http://azure.github.io/azure-mobile-apps-node/global.html#configuration
    debug: true,
    homePage: true,
    swagger: true,
    skipVersionCheck: true
}

var mobile = azureMobileApps(appConfiguration);

// Import the files from the tables directory to configure the /tables endpoint
mobile.tables.import('./tables');

// Import the files from the api directory to configure the /api endpoint
mobile.api.import('./api');

// Initialize the database before listening for incoming requests
// The tables.initialize() method does the initialization asynchronously
// and returns a Promise.
mobile.tables.initialize()
    .then(function () {
        console.log('initializing...');
        console.log(dataConfiguration);
        app.use(mobile);    // Register the Azure Mobile Apps middleware
        app.listen(process.env.PORT || 3000);   // Listen for requests
    });
