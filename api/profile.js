var util = require('../util');
var sql = require('mssql');

var querySelectUser = 'SELECT * FROM Users WHERE sid=@sid;';
var querySelectId = 'SELECT id FROM Users WHERE sid=@sid;';

var tranDone = false;

var api = {

	get: function (req, res, next) {
		var connection = new sql.Connection(util.sqlConfiguration);
		connection.connect(function(err) {
			//if (err) { console.log(err); res.sendStatus(500); return; }
			var sqlRequest = new sql.Request(connection);
			var preparedStatement = new sql.PreparedStatement(connection);
			preparedStatement.input('sid', sql.NVarChar);
			preparedStatement.prepare(querySelectUser, function(err) {
				//if (err) { console.log(err); res.sendStatus(500); return; }
				preparedStatement.execute({ sid: req.azureMobile.user.id }, 
					function(err, recordset, affected) {
						//if (err) { console.log(err); res.sendStatus(500); return; }
						cosole.log('recordset:');
						cosole.log(recordset);
						preparedStatement.unprepare();
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
