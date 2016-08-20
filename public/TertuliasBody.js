<script type="application/javascript">

	var bodyText = '';
<h1>Tertulias</h1>

<p>Welcome to Tertulias platform site.</p>
<p>You arrived at this page because you followed a link with a private invitation from a friend of yours, to join a Tertulia managed by him.</p>
<p>Your voucher number is <strong><span id="voucherPlaceHolder"></span></strong>.</p>
<p>In order to join the Tertulia, press the button bellow (You will be asked to authenticate with your authentication provider).</p>

<button onclick="onClickAction()">Subscribe</button>

<p id="userIdMessagePlaceHolder"></p>
<p id="tertuliaMessagePlaceHolder"></p>


</script>

<script type="application/javascript">

	function getVoucher(href) {
		return href.substr(href.lastIndexOf("/") + 1);
	};

	function signInAndSubscribe(voucher) {
		new WindowsAzure.MobileServiceClient("https://tertulias.azurewebsites.net",
			"309180942544-p7pg44n9uamccukt8caic0jerl2jpmta.apps.googleusercontent.com")
		.login("google")
		.done(
			function(results) {
				var userSid = results.userId;
				var voucher = getVoucher(window.location.href);
				document.getElementById("userIdMessagePlaceHolder").innerHTML = "You were assigned user id <strong>" + userSid + "</strong>.";
				subscribe(userSid, voucher);
			},
			function(err) { alert("Error: " + err); }
		);
	};

	function subscribe(userSid, voucher) {
		document.getElementById("userId").innerHTML = userSid;
		alert(userSid + " " + voucher);
		//<p id="userIdMessage">You were assigned user id <strong><span id="userId">____________________________________</span></strong>.</p>
		//<p id="tertuliaMessage">You subscribed to Tertulia <strong><span id="userId">____________________________________</span></strong>.</p>
	};

	function onClickAction() {
		signInAndSubscribe(getVoucher(window.location.href));
	};

</script>

<script type="application/javascript">
	document.getElementById("voucherPlaceHolder").innerHTML = getVoucher(window.location.href);
</script>
