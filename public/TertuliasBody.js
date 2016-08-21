function getVoucher(href){return href.substr(href.lastIndexOf("/")+1);};
function signInAndSubscribe(voucher, confirmationQuestion, placeholder, message){
	new WindowsAzure.MobileServiceClient("https://tertulias.azurewebsites.net","309180942544-p7pg44n9uamccukt8caic0jerl2jpmta.apps.googleusercontent.com")
	.login("google")
	.done(function(results){
			var userSid=results.userId;
			var voucher=getVoucher(window.location.href);
			areYouSure(confirmationQuestion, userSid, voucher, placeholder, message);
		},
		function(err){alert("Error: "+err);});
};
function areYouSure(confirmationQuestion, userSid, voucher, placeholder, message){
	if (!confirm(confirmationQuestion)) return;
	subscribe(userSid, voucher);
	console.log('subscribing');
	document.getElementById(placeholder).innerHTML=message+" <strong>"+userSid+"</strong>.";
};
function subscribe(userSid,voucher){
	alert(userSid+" "+voucher);
	var msc = new WindowsAzure.MobileServiceClient("https://tertulias.azurewebsites.net","309180942544-p7pg44n9uamccukt8caic0jerl2jpmta.apps.googleusercontent.com");
	var okCommand = new Windows.UI.Popups.UICommand("OK");
	mobileService.invokeApi("/", {
        body: null,
        method: "get"
    }).done(function (results) {
        var message = results.result.count + " item(s) marked as complete.";
        var dialog = new Windows.UI.Popups.MessageDialog(results);
        dialog.commands.append(okCommand);
        dialog.showAsync().done(function () {
            console.log("done");
        });
    }, function (error) {
        var dialog = new Windows.UI.Popups.MessageDialog(error.message);
        dialog.commands.append(okCommand);
        dialog.showAsync().done();
    });
};
