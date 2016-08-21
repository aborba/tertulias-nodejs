function getVoucher(href){return href.substr(href.lastIndexOf("/")+1);};
function signInAndSubscribe(voucher){
	new WindowsAzure.MobileServiceClient("https://tertulias.azurewebsites.net","309180942544-p7pg44n9uamccukt8caic0jerl2jpmta.apps.googleusercontent.com")
	.login("google")
	.done(function(results){
			var userSid=results.userId;
			var voucher=getVoucher(window.location.href);
			document.getElementById("userIdMessagePlaceHolder").innerHTML="You were assigned user id <strong>"+userSid+"</strong>.";
			subscribe(userSid, voucher);},
		function(err){alert("Error: "+err);});
};
function subscribe(userSid,voucher){
	document.getElementById("userId").innerHTML=userSid;
	alert(userSid+" "+voucher);
};
function onClickAction(){signInAndSubscribe(getVoucher(window.location.href));};
