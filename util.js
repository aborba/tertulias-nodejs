module.exports = {

	logBanner: function(banner) {
	    if (typeof banner === 'undefined') return;
	    var bannerLength = banner.length;
		for (var i = 0; i < bannerLength; i++) {
		    console.log(banner[i]);
		}
	},

	logBanner: function(banner, version) {
		logBanner(banner);
	    if (typeof version === 'undefined') return;
	    console.log(Version: version);
	},

	logTertulias: function(version) {
		logBanner(tertuliasBanner, version);
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
