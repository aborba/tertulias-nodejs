var util = require('../util');
var sql = require('mssql');

var transactionDone = false;

var api = {

	post: function (req, res, next) {
		var connection = new sql.Connection(util.sqlConfiguration);
		connection.connect(function(err) {
			if (err) { rollback(err, res, transaction); return; }
			var transaction = new sql.Transaction(connection);
			transaction.begin(function(err) {
				if (err) { rollback(err, res, transaction); return; }
				var sqlRequest = new sql.Request(transaction);
				var queryString = 'SELECT id FROM Users WHERE sid=@sid;';
				var preparedStatement_0 = new sql.PreparedStatement(connection);
				//transaction.on('commit', function(succeeded) { preparedStatement.unprepare(); res.sendStatus(200); });
				//transaction.on('rollback', function(aborted) { rolledback = true; preparedStatement.unprepare(); res.sendStatus(500); });
				preparedStatement_0.input('sid', sql.NVarChar);
				preparedStatement_0.prepare(queryString, function(err) {
					if (err) { rollback(err, res, transaction); return; }
					preparedStatement_0.execute({ sid: req.azureMobile.user.id }, 
						function(err, recordset, affected) {
							if (err) { rollback(err, res, transaction); return; }
							if (typeof recordset != 'undefined' && recordset[0] != null) { transaction.commit(); return; }
							preparedStatement_0.unprepare();
							var preparedStatement_1 = new sql.PreparedStatement(connection);
							queryString = 'INSERT INTO Users (sid) values (@sid);';
							preparedStatement_1.input('sid', sql.NVarChar);
							preparedStatement_1.prepare(queryString, function(err) {
								if (err) { rollback(err, res, transaction); return; }
								preparedStatement_1.execute({ sid: req.azureMobile.user.id }, 
									function(err, recordset, affected) {
										if (err) { rollback(err, res, transaction); return; }
										commit(res, transaction);
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

var completeTransaction = function(err, data) {
	if (err) {
		console.log(err);
		if (!data) return;
		if (!data.transactionDone) {
			data.transactionDone = true;
			if (data.action && util.isFunction(data.action)) data.action();
			if (data.res && data.sendStatus && util.isFunction(data.res.sendStatus)) data.res.sendStatus(data.sendStatus);
		}
	}
}

var rollback = function(err, res, transaction) {
	completeTransaction(err, {
		transactionDone: transactionDone, 
		action: transaction.rollback,
		res: res,
		sendStatus: 500
	});
}

var commit = function(res, transaction) {
	completeTransaction(undefined, {
		transactionDone: transactionDone, 
		action: transaction.commit,
		res: res,
		sendStatus: 200
	});
}

module.exports = api;
