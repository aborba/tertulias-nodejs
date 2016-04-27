var express = require('express');
var azureMobileApps = require('azure-mobile-apps');
var app = express();

var mobile = azureMobileApps();
mobile.tables.import('./tables');	// Configure /tables API.
mobile.api.import('./api');	// Configure the /api API.

mobile.tables.initialize()	// Initialize the database.
.then(function() {
	app.use(mobile);    // Register the Azure Mobile Apps middleware.
	app.listen(process.env.PORT || 3000);   // Start listening for requests.
});
