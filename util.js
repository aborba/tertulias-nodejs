module.exports = {

	logBanner: function(obj) {
	    if (typeof obj === typeof undefined) return;

	    function getPadString(pad, padChar) {
		    pad = typeof pad === 'undefined' ? 0 : pad + 1;
	    	if (typeof padChar === 'undefined') padChar = ' ';
		    return Array(pad).join(padChar);
	    }

	    function displayBanner(banner, pad) {
		    if (typeof banner === 'undefined') return;
		    var padString = getPadString(pad);
		    var bannerLen = banner.length;
			for (var i = 0; i < bannerLen; i++) console.log(padString + banner[i]);
	    }

	    function displayVersion(version, pad) {
		    if (typeof version === typeof undefined) return;
		    var padString = getPadString(pad);
		    console.log(padString + 'Version: ' + version);
	    }

	    displayBanner(obj.banner, obj.pad);
	    displayVersion(obj.version, obj.pad);
	    console.log();
	},

	logTertulias: function(version) {
		this.logBanner({ banner: this.tertuliasBanner, version: version, padding: 3 });
	},

	logTertulias2: function(version) {
		this.logBanner({ banner: this.tertuliasBanner2, version: version, padding: 3 });
	},

	dumpObj: function(obj) {
	    console.log('=============================================');
	    console.log(obj.constructor.name + ' object');
	    console.log('vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv');
	    console.log(obj);
	    console.log('---------------------------------------------');
	},

	nodeVersion: function() {
		console.log('Node Version: ' + process.version);
	},

	objName: function(obj) {
		return obj.constructor.name;
	},

	tertuliasBanner: [
	    ' ______               __             ___                           ',
	    '/\\__  _\\             /\\ \\__         /\\_ \\    __                    ',
	    '\\/_/\\ \\/    __   _ __\\ \\ ,_\\  __  __\\//\\ \\  /\\_\\     __      ____  ',
	    '   \\ \\ \\  /\'__`\\/\\`\'__\\ \\ \\/ /\\ \\/\\ \\ \\ \\ \\ \\/\\ \\  /\'__`\\   /\',__\\ ',
	    '    \\ \\ \\/\\  __/\\ \\ \\/ \\ \\ \\_\\ \\ \\_\\ \\ \\_\\ \\_\\ \\ \\/\\ \\ \\.\\_/\\__, `\\',
	    '     \\ \\_\\ \\____\\\\ \\_\\  \\ \\__\\\\ \\____/ /\\____\\\\ \\_\\ \\__/.\\_\\/\\____/',
	    '      \\/_/\\/____/ \\/_/   \\/__/ \\/___/  \\/____/ \\/_/\\/__/\\/_/\\/___/ '
	],

	tertuliasBanner2: [
		'___ ____ ____ ___ _  _ _    _ ____ ____ ',
		' |  |___ |__/  |  |  | |    | |__| [__  ',
		' |  |___ |  \\  |  |__| |___ | |  | ___] '
	]

};
