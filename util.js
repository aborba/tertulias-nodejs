module.exports = {

	logBanner: function(obj) {
	    if (typeof obj === typeof undefined) return;

	    function displayBanner(b) {
		    if (typeof b === typeof undefined) return;
		    var bLen = b.length;
			for (var i = 0; i < bLen; i++) console.log(b[i]);
	    }

	    function displayVersion(v) {
		    if (typeof v === typeof undefined) return;
		    console.log('Version: ' + v);
	    }

	    displayBanner(obj.banner);
	    displayVersion(obj.version);
	},

	logTertulias: function(ver) {
		this.logBanner({ banner: this.tertuliasBanner, version: ver });
	},

	dump: function(obj) {
	    console.log('=============================================');
	    console.log(obj.constructor.name + ' object');
	    console.log('vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv');
	    console.log(obj);
	    console.log('---------------------------------------------');
	},

	tertuliasBanner: [
	    '   ______               __             ___                           ',
	    '  /\\__  _\\             /\\ \\__         /\\_ \\    __                    ',
	    '  \\/_/\\ \\/    __   _ __\\ \\ ,_\\  __  __\\//\\ \\  /\\_\\     __      ____  ',
	    '     \\ \\ \\  /\'__`\\/\\`\'__\\ \\ \\/ /\\ \\/\\ \\ \\ \\ \\ \\/\\ \\  /\'__`\\   /\',__\\ ',
	    '      \\ \\ \\/\\  __/\\ \\ \\/ \\ \\ \\_\\ \\ \\_\\ \\ \\_\\ \\_\\ \\ \\/\\ \\ \\.\\_/\\__, `\\',
	    '       \\ \\_\\ \\____\\\\ \\_\\  \\ \\__\\\\ \\____/ /\\____\\\\ \\_\\ \\__/.\\_\\/\\____/',
	    '        \\/_/\\/____/ \\/_/   \\/__/ \\/___/  \\/____/ \\/_/\\/__/\\/_/\\/___/ '
	]

};
