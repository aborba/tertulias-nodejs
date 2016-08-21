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
	document.getElementById(placeholder).innerHTML=message+" <strong>"+userSid+"</strong>.";
	console.log('subscribing');
	subscribe(userSid, voucher);
};
function subscribe(userSid,voucher){
	var msc = new WindowsAzure.MobileServiceClient("https://tertulias.azurewebsites.net","309180942544-p7pg44n9uamccukt8caic0jerl2jpmta.apps.googleusercontent.com");
	msc.invokeApi("/", {
        body: null,
        method: "get"
    }).done(function(results) {
    	var links = results.result.links;
    	for (var i = 0; i < links.length; i++) {
    		if (links[i].rel == "accept_invitation") {
    			var method = links[i].method;
    			var href = links[i].href;
    			break;
    		}
    	}
    	alert(href);
    	msc.invokeApi(href, {
    		body: { userSid: userSid, voucher: voucher },
    		method: method
    	}).done(function(results){
    		alert("Tertulia subscription completed successfuly.\nInstall the Tertulias app from your app store and enjoy.");
    	}, function (error) {
    		alert(error.message);
    	});
    }, function (error) {
		alert(error.message);
    });
};
