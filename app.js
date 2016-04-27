var express = require('express');
var azureMobileApps = require('azure-mobile-apps');
var app = express();

// <http://azure.github.io/azure-mobile-apps-node/global.html#configuration>

var mobileApp = azureMobileApps();
mobileApp.tables.import('./tables');	// Configure /tables API.
mobileApp.api.import('./api');	// Configure the /api API.

mobileApp
	.tables.initialize()	// Initialize the database.
	.then(
		function() {
			app.use(mobileApp);    // Register the Azure Mobile Apps middleware.
			app.listen(process.env.PORT || 3000);   // Start listening for requests.
		}
	);
