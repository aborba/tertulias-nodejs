var express = require('express'),
	bodyParser = require('body-parser'),
    authenticate = require('azure-mobile-apps/src/express/middleware/authenticate'),
    authorize = require('azure-mobile-apps/src/express/middleware/authorize');

var sql = require('mssql');
var util = require('../util');

const queryTertulias = 'SELECT tr_id, tr_name, tr_subject, ev_targetdate, nv_name, no_count' +
' FROM Tertulias' +
' INNER JOIN Members ON mb_tertulia = tr_id' +
' INNER JOIN Enumvalues ON mb_role = nv_id' +
' INNER JOIN Users ON mb_user = us_id' +
' LEFT JOIN' +
' (SELECT * FROM' +
' (SELECT RANK() OVER(PARTITION BY ev_tertulia ORDER BY ev_targetdate DESC) AS "rank", * FROM Events) AS a WHERE a.rank = 1) AS b' +
' ON ev_tertulia = tr_id' +
' LEFT JOIN (SELECT no_tertulia, count(*) AS no_count FROM Notifications where no_id not in' +
' (SELECT no_id FROM Notifications INNER JOIN Readnotifications ON rn_notification = no_id INNER JOIN Users ON rn_user = us_id WHERE us_sid = @sid)' +
' GROUP BY no_tertulia) AS c ON no_tertulia = tr_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid';

const queryTertuliaX = 'SELECT DISTINCT' +
	' tr_id, tr_name, tr_subject, ' + // Tertulia
	' lo_name, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude, ' + // Location
	' sc_type, _Schedule.nv_name AS schedule, _Schedule.nv_description AS description,' + // Schedule
	' tr_is_private, ' +
	' _Member.nv_name AS nv_name' + // Role
' FROM Tertulias' +
' INNER JOIN Locations  ON tr_location = lo_id' +
' INNER JOIN Schedules  ON tr_schedule = sc_id' +
' INNER JOIN Members    ON mb_tertulia = tr_id' +
' INNER JOIN Users      ON mb_user = us_id' +
' INNER JOIN EnumValues AS _Member ON mb_role = _Member.nv_id' +
' INNER JOIN EnumValues AS _Schedule ON sc_type = _Schedule.nv_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid' +
' AND tr_id = @tertulia';

const queryScheduleMonthlyW = 'SELECT mw_id, mw_dow, mw_weeknr, mw_is_fromstart, mw_skip' +
' FROM MonthlyW' +
' INNER JOIN Schedules  ON mw_schedule = sc_id' +
' INNER JOIN Tertulias  ON sc_tertulia = tr_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid' +
' AND tr_id = @tertulia';

module.exports = function (configuration) {
    var router = express.Router();

	var completeError = function(err, res) {
	    if (err) {
	        console.error(err);
	        if (res) res.sendStatus(500);
	    }
	};

    router.get('/', (req, res, next) => {
		req.selectedQuery = queryTertulias;
	    req.paramsT = { 'sid': sql.NVarChar }; // String -> sql.NVarChar; Number -> sql.Int; Boolean -> sql.Bit; Date -> sql.DateTime; Buffer -> sql.VarBinary; sql.Table -> sql.TVP
	    req.paramsV = { 'sid': req.azureMobile.user.id };
	    goGet(req, res, next);
	});

    router.get('/:tertulia', (req, res, next) => {
		req.selectedQuery = queryTertuliaX;
	    req.paramsT = { sid: sql.NVarChar, tertulia: sql.NVarChar };
	    req.paramsV = { sid: req.azureMobile.user.id, tertulia: req.params.tertulia };
	    req.t_links = { edit: 'edit', members: 'members', messages: 'messages', events: 'events', nextevent: 'nextevent' };
	    goGet(req, res, next);
	});

	router.post('/', (req, res, next) => {
		console.log(req.body);
		goPost(req, res, next);
		res.type('application/json')
			.json({id: '1'});
		next();
	})

	var goGet = function(req, res, next) {
		var selectedQuery = req.selectedQuery;
	    var paramsT = req.paramsT;
	    var paramsV = req.paramsV;

		var connection = new sql.Connection(util.sqlConfiguration);
	    connection.connect(function(err) {
	        var sqlRequest = new sql.Request(connection);
	        var preparedStatement = new sql.PreparedStatement(connection);
	        for (var key in paramsT) preparedStatement.input(key, paramsT[key]);
	        preparedStatement.prepare(selectedQuery, function(err) {
	            if (err) { completeError(err, res); return; }
	            preparedStatement.execute(paramsV, 
	                function(err, recordset, affected) {
	                    if (err) { completeError(err, res); return; }
	                    res.type('application/json');
	                    recordset.forEach(function(elem) {
	                    	console.log(elem.tr_id);
	                    	elem['_links'] = { self: { href : 'tertulias/' + elem.tr_id } };
	                    	if (typeof req.t_links !== typeof undefined)
	                			for (var key in req.t_links)
	                				elem['_links'][key] = { href : 'tertulias/' + elem.tr_id + '/' + req.t_links[key]};
	                    	console.log(elem);
	                    	console.log(elem._links);
	                    });
	                    preparedStatement.unprepare();
	                    res.json(recordset);
	                    next();
	                }
	            );
	        });
	    });
	}

/*
CREATE PROCEDURE sp_insertTertulia_MonthlyW
	@name VARCHAR(40), @subject VARCHAR(80), 
	@userId INTEGER, 
	@weekDay VARCHAR(20), @weekNr INTEGER, 
	@fromStart BIT, @skip INTEGER, 
	@locationName VARCHAR(40),
	@locationAddress VARCHAR(80),
	@locationZip VARCHAR(40),
	@locationCountry VARCHAR(40),
	@locationLatitude VARCHAR(12),
	@locationLongitude VARCHAR(12),
	@isPrivate INTEGER
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_insertTertulia_MonthlyW
BEGIN TRY
	DECLARE @scheduleType INTEGER, @location INTEGER, @schedule INTEGER, @tertulia INTEGER, @owner INTEGER, @dow INTEGER;

	SET @scheduleType = dbo.fnGetEnum('Schedule', 'MonthlyW');
	EXEC @location = dbo.sp_getId 'lo', 'Locations', 'Dummy';

	SET @dow = dbo.fnGetEnum('WeekDays', @weekDay);

	INSERT INTO Schedules (sc_type) VALUES (@scheduleType);
	SET @schedule = SCOPE_IDENTITY();

	INSERT INTO MonthlyW (mw_schedule, mw_dow, mw_weeknr, mw_is_fromstart, mw_skip) 
	VALUES (@schedule, @dow, @weekNr, @fromStart, @skip);

    INSERT INTO Tertulias (tr_name, tr_subject, tr_location, tr_schedule, tr_is_private) 
    VALUES (@name, @subject, @location, @schedule, @isPrivate);
    SET @tertulia = SCOPE_IDENTITY();

    INSERT INTO Locations (lo_name, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude, lo_tertulia)
    VALUES (@locationName, @locationAddress, @locationZip, @locationCountry, @locationLatitude, @locationLongitude, @tertulia);
    SET @location = SCOPE_IDENTITY();

    UPDATE Tertulias SET tr_location = @location WHERE tr_id = @tertulia;

    SET @owner = dbo.fnGetEnum('Roles', 'owner');
	INSERT INTO Members (mb_tertulia, mb_user, mb_role) VALUES (@tertulia, @userId, @owner);
	COMMIT TRANSACTION tran_sp_insertTertulia_MonthlyW
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION tran_sp_insertTertulia_MonthlyW
END CATCH
GO
*/
	var goPost = function(req, res, next) {
		var selectedQuery = req.selectedQuery;
	    var paramsT = req.paramsT;
	    var paramsV = req.paramsV;

		var connection = new sql.Connection(util.sqlConfiguration);
	    connection.connect(function(err) {
	    	connection.beginTransaction();
	    	var SQL = 'SELECT nv_id FROM EnumTypes INNER JOIN EnumValues ON nv_type = nt_id '+
	    	'WHERE nt_name = @enumtype AND nv_name = @name'
	        var sqlRequest = new sql.Request(connection);
	        var preparedStatement = new sql.PreparedStatement(connection);
	        preparedStatement.input(enumtype, sql.NVarChar);
	        preparedStatement.input(name, sql.NVarChar);
	        preparedStatement.execute({
	        	enumtype: 'Schedule',
	        	name: 'MonthlyW'
	        }, 
	        function(err, recordset, affected) {
	        	console.log('done');
        		console.log(json(recordset));
            });

	    	connection.rollback();

	        for (var key in paramsT) preparedStatement.input(key, paramsT[key]);
	        preparedStatement.prepare(selectedQuery, function(err) {
	            if (err) { completeError(err, res); return; }
	            preparedStatement.execute(paramsV, 
	                function(err, recordset, affected) {
	                    if (err) { completeError(err, res); return; }
	                    res.type('application/json');
	                    recordset.forEach(function(elem) {
	                    	console.log(elem.tr_id);
	                    	elem['_links'] = { self: { href : 'tertulias/' + elem.tr_id } };
	                    	if (typeof req.t_links !== typeof undefined)
	                			for (var key in req.t_links)
	                				elem['_links'][key] = { href : 'tertulias/' + elem.tr_id + '/' + req.t_links[key]};
	                    	console.log(elem);
	                    	console.log(elem._links);
	                    });
	                    preparedStatement.unprepare();
	                    res.json(recordset);
	                    next();
	                }
	            );
	        });
	    });
	}

    return router;

}
