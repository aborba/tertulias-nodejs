var express = require('express');
var util = require('../util');
var u = require('azure-mobile-apps/src/auth/user');
var sql = require('mssql');

var queryTertulias = 'SELECT DISTINCT tr_id, tr_name, tr_subject, tr_location, tr_schedule, tr_is_private' +
' FROM Tertulias' +
' INNER JOIN Members ON tr_id = mb_tertulia' +
' INNER JOIN Users ON mb_user = us_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid';

var queryTertuliaX = queryTertulias + ' AND tr_id = @tertulia';

var queryLocations = 'SELECT DISTINCT lo_id, lo_name, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude' +
' FROM Locations' +
' INNER JOIN Tertulias ON lo_tertulia = tr_id' +
' INNER JOIN Members ON mb_tertulia = tr_id' +
' INNER JOIN Users ON mb_user = us_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid' +
' AND tr_id = @tertulia';

var queryDefaultLocation = queryLocations +
' AND lo_id = (SELECT tr_location FROM Tertulias WHERE tr_id = @tertulia)';

var queryScheduleType = 'SELECT nv_name' +
' FROM Tertulias' +
' INNER JOIN Schedules ON tr_schedule = sc_id' +
' INNER JOIN EnumValues ON sc_recurrency = nv_id' +
' INNER JOIN Members ON mb_tertulia = tr_id' +
' INNER JOIN Users ON mb_user = us_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid' +
' AND tr_id = @tertulia';

var queryScheduleMonthlyW = 'SELECT *' +
' FROM MonthlyW' +
' INNER JOIN Schedules ON mw_schedule = sc_id' +
' INNER JOIN Tertulias ON tr_schedule = sc_id' +
' INNER JOIN Members ON mb_tertulia = tr_id' +
' INNER JOIN Users ON mb_user = us_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid' +
' AND tr_id = @tertulia';

var completeError = function(err, res) {
    if (err) {
        console.error(err);
        if (res) res.sendStatus(500);
    }
};

var api = {

    all: function (req, res, next) {
        console.log('In: api/tertulias');
        return next();
    },

    get: function (req, res, next) {
        var selectedQuery;
        var paramsT = [];
        var paramsV = {};
        var tr_id = req.query.id;
        if (typeof tr_id === typeof undefined) {
            console.log('Preparing to get all my Tertulias');
            selectedQuery = queryTertulias;
            paramsT['sid'] = sql.NVarChar; // String -> sql.NVarChar; Number -> sql.Int; Boolean -> sql.Bit; Date -> sql.DateTime; Buffer -> sql.VarBinary; sql.Table -> sql.TVP
            paramsV = {'sid': req.azureMobile.user.id };
        } else {
            var sub = req.query.sub;
            paramsT['sid'] = sql.NVarChar; paramsT['tertulia'] = sql.NVarChar;
            paramsV = {'sid': req.azureMobile.user.id, 'tertulia': tr_id };
            if (typeof sub === typeof undefined) {
                selectedQuery = queryTertuliaX;
            } else {
                switch (sub) {
                    case 'locations':
                        selectedQuery = queryLocations;
                        break;
                    case 'defaultLocation':
                        console.log('Preparing to get the ' + sub + ' of my Tertulia with id: ' + tr_id);
                        selectedQuery = queryDefaultLocation;
                        break;
                    case 'scheduleType':
                        console.log('Preparing to get the ' + sub + ' of my Tertulia with id: ' + tr_id);
                        selectedQuery = queryScheduleType;
                        break;
                    case 'schedule':
                        console.log('Preparing to get the ' + sub + ' of my Tertulia with id: ' + tr_id);
                        var schedule_type = req.query.sel;
                        console.log('schedule type: ' + schedule_type);
                        if (typeof schedule_type === typeof undefined) {
                            res.sendStatus(400);
                            return;
                        }
                        console.log('Preparing to get the ' + schedule_type + ' ' + sub + ' of my Tertulia with id: ' + tr_id);
                        switch (schedule_type) {
                            case 'MonthlyW':
                                selectedQuery = queryScheduleMonthlyW;
                                break;
                            default:
                                console.log('Bum - de dentro.');
                                res.sendStatus(400);
                                return;
                        }
                        break;
                    default:
                        console.log('Bum - de fora.');
                        res.sendStatus(400);
                        return;
                }
            }
        }

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
                        console.log(recordset);
                        preparedStatement.unprepare();
                        res.type('application/json').json(recordset);
                    }
                );
            });
        });
    }
};

api.access = 'authenticated';
module.exports = api;
