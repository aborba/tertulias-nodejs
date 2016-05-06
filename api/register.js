var util = require('../util');
var u = require('azure-mobile-apps/src/auth/user');
var sql = require('mssql');

var transactionDone = false;

var completeTransaction(err, data) {
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
				var preparedStatement = new sql.PreparedStatement(connection);
				//transaction.on('commit', function(succeeded) { preparedStatement.unprepare(); res.sendStatus(200); });
				//transaction.on('rollback', function(aborted) { rolledback = true; preparedStatement.unprepare(); res.sendStatus(500); });
				preparedStatement.input('sid', sql.NVarChar);
				preparedStatement.prepare(queryString, function(err) {
					if (err) { rollback(err, res, transaction); return; }
					preparedStatement.execute({ sid: req.azureMobile.user.id }, 
						function(err, recordset, affected) {
							if (err) { rollback(err, res, transaction); return; }
							if (typeof recordset != 'undefined' && recordset[0] != null) { transaction.commit(); return; }
							preparedStatement.unprepare();
							queryString = 'INSERT INTO Users (sid) values (@sid);';
							preparedStatement.input('sid', sql.NVarChar);
							preparedStatement.prepare(queryString, function(err) {
								if (err) { rollback(err, res, transaction); return; }
								preparedStatement.execute({ sid: req.azureMobile.user.id }, 
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

    	// FORMER
    	/*
        var query = {
            sql: 'INSERT INTO Terulias(title, subject, schedule, privacy) VALUES (@title, @subject, @schedule, @privacy)',
            parameters: [
                {name: 'title', value: req.body.title || ""},
                {name: 'subject', value: req.body.subject || ""},
                {name: 'schedule', value: req.body.schedule || "0"},
                {name: 'privacy', value: req.body.privacy || "0"},
            ]
        };
        console.log(query);
        var query1 = {
            sql: 'SELECT id FROM Users WHERE sid=@userSid',
            parameters: [
                {name: 'userSid', value: req.azureMobile.user.id }
            ]
        };
        console.log(query1);
        var query2 = {
            sql: 'SELECT id FROM Roles WHERE roleName=@roleName',
            parameters: [
                {name: 'roleName', value: 'admin' }
            ]
        };
        console.log(query2);
        var query3 = {
            sql: 'INSERT INTO Members(tertulia, usr, role) VALUES (@tertuliaId, @usrId, @roleId)',
            parameters: [
                {name: 'tertuliaId', value: req.body.title || ""},
                {name: 'usrId', value: req.body.subject || ""},
                {name: 'roleId', value: req.body.schedule || "0"},
            ]
        };
        console.log(query3);
        res.sendStatus(200);
        return next();
        */
    }
};

api.access = 'authenticated';
module.exports = api;
