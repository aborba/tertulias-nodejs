var util = require('../util');
var sql = require('mssql');

var querySelectUser = 'SELECT * FROM Users WHERE sid=@sid;';
var querySelectId = 'SELECT id FROM Users WHERE sid=@sid;';
var queryUpdateUser = 'UPDATE Users (alias) values (@alias) WHERE sid=@sid;';

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
						console.log('recordset:');
						console.log(recordset);
						preparedStatement.unprepare();
						res
						//.sendStatus(200)
						.type('application/json')
						.json(recordset);
            			return next();
					}
				);
			});
		});
	},

	post: function (req, res, next) {
		console.log('body: ' + req.body);
		console.log('alias: ' + req.body.alias);
		var connection = new sql.Connection(util.sqlConfiguration);
		connection.connect(function(err) {
			console.log('control point 1');
			if (err) { rollback(err, res, transaction); return; }
			console.log('control point 2');
			var transaction = new sql.Transaction(connection);
			console.log('control point 3');
			transaction.begin(function(err) {
			console.log('control point 4');
				if (err) { rollback(err, res, transaction); return; }
			console.log('control point 5');
				var sqlRequest = new sql.Request(transaction);
			console.log('control point 6');
				var preparedStatement = new sql.PreparedStatement(connection);
			console.log('control point 7');
				transaction.on('commit', function(succeeded) { preparedStatement.unprepare(); res.sendStatus(200); });
				transaction.on('rollback', function(aborted) { rolledback = true; preparedStatement.unprepare(); res.sendStatus(500); });
			console.log('control point 8');
				preparedStatement.input('sid', sql.NVarChar);
			console.log('control point 9');
				preparedStatement.prepare(querySelectId, function(err) {
			console.log('control point 10');
					if (err) { rollback(err, res, transaction); return; }
			console.log('control point 11');
					preparedStatement.execute({ sid: req.azureMobile.user.id }, 
						function(err, recordset, affected) {
			console.log('control point 12');
							if (err) { rollback(err, res, transaction); return; }
			console.log('control point 13');
							if (typeof recordset != 'undefined' && recordset[0] != null) { transaction.commit(); return; }
			console.log('control point 14');
							preparedStatement.unprepare();
			console.log('control point 15');
							preparedStatement.input('sid', sql.NVarChar);
			console.log('control point 16');
							preparedStatement.input('alias', sql.NVarChar);
			console.log('control point 17');
							preparedStatement.prepare(queryUpdateUser, function(err) {
			console.log('control point 18');
								if (err) { rollback(err, res, transaction); return; }
			console.log('control point 19');
								console.log('alias: ' + req.body.alias);
			console.log('control point 20');
								preparedStatement.execute({
									sid: req.azureMobile.user.id,
									alias: req.body.alias || ""
								}, function(err, recordset, affected) {
			console.log('control point 21');
									if (err) { rollback(err, res, transaction); return; }
			console.log('control point 22');
									commit(res, transaction);
			console.log('control point 23');
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
