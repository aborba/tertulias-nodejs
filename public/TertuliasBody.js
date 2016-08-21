function getVoucher(href){return href.substr(href.lastIndexOf("/")+1);};
function signInAndSubscribe(voucher, confirmationQuestion, placeholder, message){
	new WindowsAzure.MobileServiceClient("https://tertulias.azurewebsites.net","309180942544-p7pg44n9uamccukt8caic0jerl2jpmta.apps.googleusercontent.com")
	.login("google")
	.done(function(results){
			var userSid=results.userId;
			var voucher=getVoucher(window.location.href);
			document.getElementById(placeholder).innerHTML=message+" <strong>"+userSid+"</strong>.";
			areYouSure(confirmationQuestion, userSid, voucher);
		},
		function(err){alert("Error: "+err);});
};
function areYouSure(confirmationQuestion, userSid, voucher){
	if (!confirm(confirmationQuestion)) return;
	subscribe(userSid, voucher);
	console.log('subscribing');
};
function subscribe(userSid,voucher){
	document.getElementById("userId").innerHTML=userSid;
	alert(userSid+" "+voucher);
};
