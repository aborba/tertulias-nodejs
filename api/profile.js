var util = require('../util');
var sql = require('mssql');

var querySelectUser = 'SELECT * FROM Users WHERE sid=@sid;';
var querySelectId = 'SELECT id FROM Users WHERE sid=@sid;';
var queryUpdateUser = 'UPDATE Users SET alias=@alias WHERE sid=@sid;';

var tranDone = false;

var api = {

	get: function (req, res, next) {
		var connection = new sql.Connection(util.sqlConfiguration);
		connection.connect(function(err) {
			if (err) { completeError(err, res); return; }
			var sqlRequest = new sql.Request(connection);
			var preparedStatement = new sql.PreparedStatement(connection);
			preparedStatement.input('sid', sql.NVarChar);
			preparedStatement.prepare(querySelectUser, function(err) {
				if (err) { completeError(err, res); return; }
				preparedStatement.execute({ sid: req.azureMobile.user.id }, 
					function(err, recordset, affected) {
						if (err) { completeError(err, res); return; }
						console.log('recordset:');
						console.log(recordset);
						preparedStatement.unprepare();
						res.type('application/json').json(recordset);
            			return next();
					}
				);
			});
		});
	},

	post: function (req, res, next) {
		var connection = new sql.Connection(util.sqlConfiguration);
		connection.connect(function(err) {
			if (err) { rollback(err, res, transaction); return; }
			var transaction = new sql.Transaction(connection);
			transaction.begin(function(err) {
				if (err) { rollback(err, res, transaction); return; }
				var sqlRequest = new sql.Request(transaction);
				var psSelectId = new sql.PreparedStatement(connection);
				transaction.on('commit', function(succeeded) { psSelectId.unprepare(); res.sendStatus(200); });
				transaction.on('rollback', function(aborted) { rolledback = true; psSelectId.unprepare(); res.sendStatus(500); });
				psSelectId.input('sid', sql.NVarChar);
				psSelectId.prepare(querySelectId, function(err) {
					if (err) { rollback(err, res, transaction); return; }
					psSelectId.execute({ sid: req.azureMobile.user.id }, 
						function(err, recordset, affected) {
							if (err) { rollback(err, res, transaction); return; }
							psSelectId.unprepare();
							var psUpdateUser = new sql.PreparedStatement(connection);
							psUpdateUser.input('sid', sql.NVarChar);
							psUpdateUser.input('alias', sql.NVarChar);
							psUpdateUser.prepare(queryUpdateUser, function(err) {
								if (err) { rollback(err, res, transaction); return; }
								psUpdateUser.execute({
									sid: req.azureMobile.user.id,
									alias: req.body.alias || ""
								}, function(err, recordset, affected) {
									if (err) { rollback(err, res, transaction); return; }
									commit(res, transaction);
									psUpdateUser.unprepare();
								});
							});
						}
					);
				});
			});
		});
    }
};

api.access = 'authenticated';

var completeError = function(err, res) {
	if (err) {
		console.error(err);
		if (res) res.sendStatus(500);
	}
}

var completeTransaction = function(err, data) {
	if (err) {
		console.error(err);
		if (!data) return;
		if (!data.tranDone) {
			data.tranDone = true;
			if (data.action && util.isFunction(data.action)) data.action();
			if (data.res && data.sendStatus && util.isFunction(data.res.sendStatus)) data.res.sendStatus(data.sendStatus);
		}
	}
}

var rollback = function(err, res, transaction) {
	completeTransaction(err, {
		tranDone: tranDone, 
		action: transaction.rollback,
		res: res,
		sendStatus: 500
	});
}

var commit = function(res, transaction) {
	completeTransaction(undefined, {
		tranDone: tranDone, 
		action: transaction.commit,
		res: res,
		sendStatus: 200
	});
}

module.exports = api;
