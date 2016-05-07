var util = requi re('../util');
var sql = require('mssql');

var querySelectId = 'SELECT id FROM Users WHERE sid=@sid;';
var queryInsertSid = 'INSERT INTO Users (sid) values (@sid);';

var tranDone = false;

var completeError = function(err, res) {
	if (err) {
		console.log(err);
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

var api = {
	post: function (req, res, next) {
		var conn = new sql.Connection(util.sqlConfiguration);
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
								/*
							userName(req.azureMobile.user, function() {
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
							});
								*/
							rollback200(res, tran);
						}
					);
				});
			});
		});
    }
};

api.access = 'authenticated';

/*
var userName = function(user, next) {
    user.getIdentity().then(function(_identity){
    	console.log(_identity);
    	next();
    });
};
*/


module.exports = api;
