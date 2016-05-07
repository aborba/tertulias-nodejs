var util = require('../util');
var sql = require('mssql');

var transactionDone = false;

var api = {

	get: function (req, res, next) {
		console.log('in profile GET');
		var connection = new sql.Connection(util.sqlConfiguration);
		connection.connect(function(err) {
			if (err) { res.sendStatus(500); return; }
			var sqlRequest = new sql.Request(connection);
			var queryString = 'SELECT sid, alias FROM Users WHERE sid=@sid;';
			var preparedStatement = new sql.PreparedStatement(connection);
			preparedStatement.input('sid', sql.NVarChar);
			preparedStatement.prepare(queryString, function(err) {
				if (err) { res.sendStatus(500); return; }
				preparedStatement.execute({ sid: req.azureMobile.user.id }, 
					function(err, recordset, affected) {
						if (err || (typeof recordset != 'undefined' && recordset[0] != null)) {
							res.sendStatus(500); return;
						}
						preparedStatement.unprepare();
						console.log('recordset');
						console.log(recordset);
						res.sendStatus(200).type('application/json').json(recordset);
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
				var queryString = 'SELECT id FROM Users WHERE sid=@sid;';
				var preparedStatement = new sql.PreparedStatement(connection);
				transaction.on('commit', function(succeeded) { preparedStatement.unprepare(); res.sendStatus(200); });
				transaction.on('rollback', function(aborted) { rolledback = true; preparedStatement.unprepare(); res.sendStatus(500); });
				preparedStatement.input('sid', sql.NVarChar);
				preparedStatement.prepare(queryString, function(err) {
					if (err) { rollback(err, res, transaction); return; }
					preparedStatement.execute({ sid: req.azureMobile.user.id }, 
						function(err, recordset, affected) {
							if (err) { rollback(err, res, transaction); return; }
							if (typeof recordset != 'undefined' && recordset[0] != null) { transaction.commit(); return; }
							preparedStatement.unprepare();
							queryString = 'UPDATE Users (alias) values (@alias) WHERE sid=@sid;';
							preparedStatement.input('sid', sql.NVarChar);
							preparedStatement.input('alias', sql.NVarChar);
							preparedStatement.prepare(queryString, function(err) {
								if (err) { rollback(err, res, transaction); return; }
								preparedStatement.execute({
									sid: req.azureMobile.user.id,
									alias: 'aborba'
								}, function(err, recordset, affected) {
									if (err) { rollback(err, res, transaction); return; }
									commit(res, transaction);
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
