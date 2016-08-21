var express         = require('express'),
	bodyParser      = require('body-parser');

var sql             = require('mssql'),
	util            = require('../util');

module.exports      = function (configuration) {
	var router      = express.Router(),
	authenticate    = require('azure-mobile-apps/src/express/middleware/authenticate')(configuration),
	authorize       = require('azure-mobile-apps/src/express/middleware/authorize');

	var completeError = function(err, res) {
		if (err) {
			console.error(err);
			if (res) res.sendStatus(500);
		}
	};

	router.get('/:voucher', (req, res, next) => {
		console.log('in GET /private_invitation/:voucher');
		console.log(configuration);
		var voucher = req.params.voucher;
		console.log(voucher);
		var body = `
<link rel="stylesheet" type="text/css" href="/tertulias.css">
<script type="application/javascript" src="/MobileServices.Web.min.js"></script>
<script type="application/javascript" src="/TertuliasBody.js"></script>

<h1>Tertulias</h1>
	<p>Welcome to Tertulias platform site.</p>
	<p>You arrived at this page because you followed a link with a private invitation from a friend of yours, to join a Tertulia managed by him.</p>
	<p>Your voucher number is <strong><span id='voucherPlaceHolder'></span></strong>.</p>
	<p>In order to join the Tertulia, press the button bellow (You will be asked to authenticate with your authentication provider).</p>

	<button id='action' onclick="onClickAction('userIdMessagePlaceHolder')">Subscribe</button>

	<p id='userIdMessagePlaceHolder'></p>
	<p id='tertuliaMessagePlaceHolder'></p>

<script type="application/javascript">
	var voucher = getVoucher(window.location.href);
	function onClickAction() { signInAndSubscribe(
		voucher,
		'Are you sure you want to subscribe tertulia "XXX" with user "YYY"?',
		'userIdMessagePlaceHolder', 'Your user id is:'); };
	document.getElementById('voucherPlaceHolder').innerHTML = voucher;
</script>
`;
		res.send(body);
		next();
	});
	return router;
};
