module.exports = {

	printHeader: function() {
	    console.log('   ______               __             ___                           ');
	    console.log('  /\\__  _\\             /\\ \\__         /\\_ \\    __                    ');
	    console.log('  \\/_/\\ \\/    __   _ __\\ \\ ,_\\  __  __\\//\\ \\  /\\_\\     __      ____  ');
	    console.log('     \\ \\ \\  /\'__`\\/\\`\'__\\ \\ \\/ /\\ \\/\\ \\ \\ \\ \\ \\/\\ \\  /\'__`\\   /\',__\\ ');
	    console.log('      \\ \\ \\/\\  __/\\ \\ \\/ \\ \\ \\_\\ \\ \\_\\ \\ \\_\\ \\_\\ \\ \\/\\ \\ \\.\\_/\\__, `\\');
	    console.log('       \\ \\_\\ \\____\\\\ \\_\\  \\ \\__\\\\ \\____/ /\\____\\\\ \\_\\ \\__/.\\_\\/\\____/');
	    console.log('        \\/_/\\/____/ \\/_/   \\/__/ \\/___/  \\/____/ \\/_/\\/__/\\/_/\\/___/ ');
	},

	dump: function(obj) {
	    console.log('=============================================');
	    console.log(obj.constructor.name + ' object');
	    console.log('vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv');
	    console.log(obj);
	    console.log('---------------------------------------------');
	}

};
