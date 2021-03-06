function getVoucher(href){return href.substr(href.lastIndexOf("/")+1);};
function signInAndSubscribe(client, voucher, confirmationQuestion, placeholder, message){
	client.login("google")
	.done(function(results){
			var userSid=results.userId;
			var voucher=getVoucher(window.location.href);
			areYouSure(client, confirmationQuestion, userSid, voucher, placeholder, message);
		},
		function(err){alert("Error: "+err);});
};
function areYouSure(client, confirmationQuestion, userSid, voucher, placeholder, message){
	if (!confirm(confirmationQuestion)) return;
	document.getElementById(placeholder).innerHTML=message+" <strong>"+userSid+"</strong>.";
	console.log('subscribing');
	subscribe(client, userSid, voucher);
};
function subscribe(client,voucher,fOk,fErr){
	client.invokeApi("/", {
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
    	if ( ! href) {
    		alert("An internal error ocurred, please try again later.");
    		return;
    	}
    	client.invokeApi(href, {
    		body: { voucher: voucher },
    		method: method
    	}).done(function(results){
    		fOk(results.result);
    	}, function (err) {
    		fErr(err.message);
    	});
    }, function (err) {
		fErr(err.message);
    });
};
