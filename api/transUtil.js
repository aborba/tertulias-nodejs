module.exports = {

	completeError: function(err, res) {
		if (err) {
			console.error(err);
			if (res) res.sendStatus(500);
		}
	},

	completetran: function(err, data) {
		if (err) {
			console.log(err);
			if (!data) return;
			if (!data.tranDone) {
				data.tranDone = true;
				if (data.action && util.isFunction(data.action)) data.action();
				if (data.res && data.sendStatus && util.isFunction(data.res.sendStatus)) data.res.sendStatus(data.sendStatus);
			}
		}
	},

	rollback500: function(err, res, tran) {
		completetran(err, {
			tranDone: tranDone, 
			action: tran.rollback,
			res: res,
			sendStatus: 500
		});
	},

	rollback200: function(res, tran) {
		completetran(undefined, {
			tranDone: tranDone, 
			action: tran.rollback,
			res: res,
			sendStatus: 200
		});
	},

	commit200: function(res, tran) {
		completetran(undefined, {
			tranDone: tranDone, 
			action: tran.commit,
			res: res,
			sendStatus: 200
		});
	}

};
