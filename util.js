module.exports = {

	logBanner: function(obj) {
	    if (typeof obj === typeof undefined) return;

	    function getPadString(pad, padChar) {
		    if (typeof pad === typeof undefined) pad = 1;
	    	if (typeof padChar === typeof undefined) padChar = ' ';
		    return Array(pad).join(padChar);
	    }

	    function displayBanner(b, pad) {
		    if (typeof b === typeof undefined) return;
		    var padString = getPadString(pad);
		    var bLen = b.length;
			for (var i = 0; i < bLen; i++) console.log(padString + b[i]);
	    }

	    function displayVersion(v, pad) {
		    if (typeof v === typeof undefined) return;
		    var padString = getPadString(pad);
		    console.log(padString + 'Version: ' + v);
	    }

	    displayBanner(obj.banner, obj.pad);
	    displayVersion(obj.version, obj.pad);
	},

	logTertulias: function(version) {
		this.logBanner({ banner: this.tertuliasBanner, version: version, padding: 3 });
	},

	dump: function(obj) {
	    console.log('=============================================');
	    console.log(obj.constructor.name + ' object');
	    console.log('vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv');
	    console.log(obj);
	    console.log('---------------------------------------------');
	},

	tertuliasBanner: [
	    ' ______               __             ___                           ',
	    '/\\__  _\\             /\\ \\__         /\\_ \\    __                    ',
	    '\\/_/\\ \\/    __   _ __\\ \\ ,_\\  __  __\\//\\ \\  /\\_\\     __      ____  ',
	    '   \\ \\ \\  /\'__`\\/\\`\'__\\ \\ \\/ /\\ \\/\\ \\ \\ \\ \\ \\/\\ \\  /\'__`\\   /\',__\\ ',
	    '    \\ \\ \\/\\  __/\\ \\ \\/ \\ \\ \\_\\ \\ \\_\\ \\ \\_\\ \\_\\ \\ \\/\\ \\ \\.\\_/\\__, `\\',
	    '     \\ \\_\\ \\____\\\\ \\_\\  \\ \\__\\\\ \\____/ /\\____\\\\ \\_\\ \\__/.\\_\\/\\____/',
	    '      \\/_/\\/____/ \\/_/   \\/__/ \\/___/  \\/____/ \\/_/\\/__/\\/_/\\/___/ '
	]

};
