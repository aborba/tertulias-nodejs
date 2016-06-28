var express = require('express'),
	bodyParser = require('body-parser'),
    authenticate = require('azure-mobile-apps/src/express/middleware/authenticate'),
    authorize = require('azure-mobile-apps/src/express/middleware/authorize');

var sql = require('mssql');
var util = require('../util');

const queryTertulias = 'SELECT' +
' tr_id         AS id,' +
' tr_name       AS name,' +
' tr_subject    AS subject,' +
' ev_targetdate AS nextEventDate,' +
' lo_name       AS nextEventLocation,' +
' no_count      AS messages,' +
' nv_name       AS role ' +
' FROM Tertulias' +
' INNER JOIN Members ON mb_tertulia = tr_id' +
' INNER JOIN Enumvalues ON mb_role = nv_id' +
' INNER JOIN Users ON mb_user = us_id' +
' LEFT JOIN' +
' (SELECT * FROM' +
' (SELECT RANK() OVER(PARTITION BY ev_tertulia ORDER BY ev_targetdate DESC) AS "rank", * FROM Events' +
'  INNER JOIN Locations on ev_location = lo_id WHERE ev_targetdate > GETDATE()) AS a WHERE a.rank = 1) AS b' +
' ON ev_tertulia = tr_id' +
' LEFT JOIN (SELECT no_tertulia, count(*) AS no_count FROM Notifications WHERE no_id NOT IN' +
' (SELECT no_id FROM Notifications INNER JOIN Readnotifications ON rn_notification = no_id' +
' INNER JOIN Users ON rn_user = us_id WHERE us_sid = @sid)' +
' GROUP BY no_tertulia) AS c ON no_tertulia = tr_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid';

module.exports = function (configuration) {
    var router = express.Router();

	var completeError = function(err, res) {
	    if (err) {
	        console.error(err);
	        if (res) res.sendStatus(500);
	    }
	};

    router.get('/', (req, res, next) => {
		var route = '/tertulias';
		req['tertulias'] = {};
		req['route'] = route;
		req['resultsTag'] = 'tertulias';
		req.tertulias['query'] = queryTertulias;
	    req.tertulias['paramsTypes'] = { 'sid': sql.NVarChar }; // String -> sql.NVarChar; Number -> sql.Int; Boolean -> sql.Bit; Date -> sql.DateTime; Buffer -> sql.VarBinary; sql.Table -> sql.TVP
	    req.tertulias['paramsValues'] = { 'sid': req.azureMobile.user.id };
	    req.tertulias['links'] = '[ ' +
			'{ rel: "self", method: "GET", href: "' + route + '" }, ' +
			'{ rel: "create", method: "POST", href: "' + route + '" }, ' +
			'{ rel: "searchPublic", method: "GET", href: "' + route + '/publicsearch" } ' +
		']';
	    req.tertulias['itemLinks'] = '[ ' +
			'{ rel: "self", method: "GET", href: "' + route + '/:tertulia" }, ' +
			'{ rel: "update", method: "PUT", href: "' + route + '/:tertulia" }, ' +
			'{ rel: "delete", method: "DELETE", href: "' + route + '/:tertulia" } ' +
		']';
	    goGet(req, res, next);
    	/*
		*/
	});

	var goGet = function(req, res, next) {
		console.log('VAMOS AQUI: 1');
		var query = req.tertulias.query;
		var resultsTag = req.tertulias.resultsTag;
		var route = req.tertulias.route;
	    var paramsTypes = req.tertulias.paramsTypes;
	    var paramsValues = req.tertulias.paramsValues;
	    var links = req.tertulias.links;
	    var itemLinks = req.tertulias.itemLinks;
		console.log('VAMOS AQUI: 2');

		var connection = new sql.Connection(util.sqlConfiguration);
	    connection.connect(function(err) {
			console.log('VAMOS AQUI: 3');
	        var preparedStatement = new sql.PreparedStatement(connection);
	        for (var key in paramsTypes)
	        	preparedStatement.input(key, paramsTypes[key]);
			console.log('VAMOS AQUI: 4');
	        preparedStatement.prepare(query, function(err) {
	            if (err) {
	            	completeError(err, res);
	            	return;
	            }
				console.log('VAMOS AQUI: 5');
	            preparedStatement.execute(paramsValues, 
	                function(err, recordset, affected) {
						console.log('VAMOS AQUI: 6');
	                    if (err) {
	                    	completeError(err, res);
	                    	return;
	                    }
						console.log('VAMOS AQUI: 7');
	                    res.type('application/json');
                    	if (typeof itemLinks !== typeof undefined) {
							console.log(itemLinks);
		                    recordset.forEach(function(elem) {
								console.log(elem);
		                    	elem['links'] = JSON.parse(itemLinks.replace(/:tertulia/g, elem.id));
                    		});
	                    };
	                    preparedStatement.unprepare();
						console.log('VAMOS AQUI: 8');
	                    var results = {};
	                    results[resultsTag] = recordset;
						console.log('VAMOS AQUI: 9');
	                    results['links'] = links;
	                    console.log(results);
	                    res.json(results);
	                    next();
	                }
	            );
	        });
	    });
		/*
	    */
	}

    return router;

}
