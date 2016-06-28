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

    router.get('/', (req, res, next) => {
		var route = '/tertulias';
		req['tertulias'] = {};
		req.tertulias['resultsTag'] = 'tertulias';
		req.tertulias['query'] = queryTertulias;
	    req.tertulias['paramsTypes'] = { 'sid': sql.NVarChar }; // String -> sql.NVarChar; Number -> sql.Int; Boolean -> sql.Bit; Date -> sql.DateTime; Buffer -> sql.VarBinary; sql.Table -> sql.TVP
	    req.tertulias['paramsValues'] = { 'sid': req.azureMobile.user.id };
	    req.tertulias['links'] = '[ ' +
			'{ "rel": "self", "method": "GET", "href": "' + route + '" }, ' +
			'{ "rel": "create", "method": "POST", "href": "' + route + '" }, ' +
			'{ "rel": "searchPublic", "method": "GET", "href": "' + route + '/publicsearch" } ' +
		']';
	    req.tertulias['itemLinks'] = '[ ' +
			'{ "rel": "self", "method": "GET", "href": "' + route + '/:id" }, ' +
			'{ "rel": "update", "method": "PUT", "href": "' + route + '/:id" }, ' +
			'{ "rel": "delete", "method": "DELETE", "href": "' + route + '/:id" } ' +
		']';
	    goGet(req, res, next);
	});

	var completeError = function(err, res) {
	    if (err) {
	        console.error(err);
	        if (res) res.sendStatus(500);
	    }
	};

	var goGet = function(req, res, next) {
		var query = req.tertulias.query;
		var resultsTag = req.tertulias.resultsTag;
	    var paramsTypes = req.tertulias.paramsTypes;
	    var paramsValues = req.tertulias.paramsValues;
	    var links = req.tertulias.links;
	    var itemLinks = req.tertulias.itemLinks;

		var connection = new sql.Connection(util.sqlConfiguration);
	    connection.connect(function(err) {
	        var preparedStatement = new sql.PreparedStatement(connection);
	        for (var key in paramsTypes)
	        	preparedStatement.input(key, paramsTypes[key]);
	        preparedStatement.prepare(query, function(err) {
	            if (err) {
	            	completeError(err, res);
	            	return;
	            }
	            preparedStatement.execute(paramsValues, 
	                function(err, recordset, affected) {
	                    if (err) {
	                    	completeError(err, res);
	                    	return;
	                    }
	                    res.type('application/json');
                    	if (typeof itemLinks !== typeof undefined) {
		                    recordset.forEach(function(elem) {
		                    	elem['links'] = JSON.parse(itemLinks.replace(/:id/g, elem.id));
                    		});
	                    };
	                    preparedStatement.unprepare();
	                    var results = {};
	                    results[resultsTag] = recordset;
	                    results['links'] = JSON.parse(links);
	                    console.log(results);
	                    res.json(results);
	                    next();
	                }
	            );
	        });
	    });
	};

    return router;

}
