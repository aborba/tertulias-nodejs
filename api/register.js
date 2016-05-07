var util = require('../util');
var sql = require('mssql');

var querySelectId = 'SELECT id FROM Users WHERE sid=@sid;';
var queryInsertSid = 'INSERT INTO Users (sid) values (@sid);';

var tranDone = false;

var api = {
	post: function (req, res, next) {
		var conn = new sql.Connection(util.sqlConfiguration);
		var usrName = userName(req.azureMobile.user);
		console.log(usrName);
		conn.connect(function(err) {
			if (err) { completeError(err, res); return; }
			var tran = new sql.Transaction(conn);
			tran.begin(function(err) {
				if (err) { rollback500(err, res, tran); return; }
				var sqlRequest = new sql.Request(tran);
				var psSelectId = new sql.PreparedStatement(conn);
				psSelectId.input('sid', sql.NVarChar);
				psSelectId.prepare(querySelectId, function(err) {
					if (err) { rollback500(err, res, tran); return; }
					psSelectId.execute({ sid: req.azureMobile.user.id }, 
						function(err, recordset, affected) {
							if (err) { rollback500(err, res, tran); return; }
							if (typeof recordset != 'undefined' && recordset[0] != null) { rollback200(res, tran); return; }
							psSelectId.unprepare();
							var psInsertSid = new sql.PreparedStatement(conn);
							psInsertSid.input('sid', sql.NVarChar);
							psInsertSid.prepare(queryInsertSid, function(err) {
								if (err) { rollback500(err, res, tran); return; }
								psInsertSid.execute({ sid: req.azureMobile.user.id }, 
									function(err, recordset, affected) {
										if (err) { rollback500(err, res, tran); return; }
										commit200(res, tran);
									}
								);
							});
						}
					);
				});
			});
		});
    }
};

api.access = 'authenticated';

var userName = function(user) {
	var item = {};
	/*
    item.UserName = "";
    user.getIdentities({
        success: function (identities) {
            var req = require('request');
            if (identities.google) {
                var googleAccessToken = identities.google.accessToken;
                var url = 'https://www.googleapis.com/oauth2/v3/userinfo?access_token=' + googleAccessToken;
                req(url, function (err, resp, body) {
                    if (err || resp.statusCode !== 200) {
                        console.error('Error sending data to Google API: ', err);
                    } else {
                        try {
                            var userData = JSON.parse(body);
                            console.log(userData.name);
                            item.UserName = userData.name;
                        } catch (ex) {
                            console.error('Error parsing response from Google API: ', ex);
                        }
                    }
                });
            }
            return item;
        }
    });
    */
    return item;
}

var completeError(err, res) {
	if (err) {
		console.error(err);
		if (res) res.sendStatus(500);
	}
}

var completetran = function(err, data) {
	if (err) {
		console.log(err);
		if (!data) return;
		if (!data.tranDone) {
			data.tranDone = true;
			if (data.action && util.isFunction(data.action)) data.action();
			if (data.res && data.sendStatus && util.isFunction(data.res.sendStatus)) data.res.sendStatus(data.sendStatus);
		}
	}
}

var rollback500 = function(err, res, tran) {
	completetran(err, {
		tranDone: tranDone, 
		action: tran.rollback,
		res: res,
		sendStatus: 500
	});
}

var rollback200 = function(res, tran) {
	completetran(undefined, {
		tranDone: tranDone, 
		action: tran.rollback,
		res: res,
		sendStatus: 200
	});
}

var commit200 = function(res, tran) {
	completetran(undefined, {
		tranDone: tranDone, 
		action: tran.commit,
		res: res,
		sendStatus: 200
	});
}

module.exports = api;
