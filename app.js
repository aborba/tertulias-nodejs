var appVersion = '1';

//var util = require('./util');
//util.logBanner(util.tertuliasBanner, appVersion);

var x = { tertuliasBanner: [
        '   ______               __             ___                           ',
        '  /\\__  _\\             /\\ \\__         /\\_ \\    __                    ',
        '  \\/_/\\ \\/    __   _ __\\ \\ ,_\\  __  __\\//\\ \\  /\\_\\     __      ____  ',
        '     \\ \\ \\  /\'__`\\/\\`\'__\\ \\ \\/ /\\ \\/\\ \\ \\ \\ \\ \\/\\ \\  /\'__`\\   /\',__\\ ',
        '      \\ \\ \\/\\  __/\\ \\ \\/ \\ \\ \\_\\ \\ \\_\\ \\ \\_\\ \\_\\ \\ \\/\\ \\ \\.\\_/\\__, `\\',
        '       \\ \\_\\ \\____\\\\ \\_\\  \\ \\__\\\\ \\____/ /\\____\\\\ \\_\\ \\__/.\\_\\/\\____/',
        '        \\/_/\\/____/ \\/_/   \\/__/ \\/___/  \\/____/ \\/_/\\/__/\\/_/\\/___/ '
    ]};
for (var i = 0; i < x.tertuliasBanner.length; i++) {
            console.log(x.tertuliasBanner[i]);
        }

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
        //util.dump(mobile);
        console.log('Registering the Azure Mobile Apps middleware.');
        app.use(mobile);
        console.log('Listening for requests.');
        app.listen(process.env.PORT || 3000);
    });
