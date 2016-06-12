var express = require('express');
var util = require('../util');
var u = require('azure-mobile-apps/src/auth/user');
var sql = require('mssql');

var queryTertulias = 'SELECT DISTINCT tr_id, tr_name, tr_subject, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude, sc_recurrency, tr_is_private, nv_name' +
' FROM Tertulias' +
' INNER JOIN Locations  ON tr_location = lo_id' +
' INNER JOIN Schedules  ON tr_schedule = sc_id' +
' INNER JOIN Members    ON mb_tertulia = tr_id' +
' INNER JOIN Users      ON mb_user = us_id' +
' INNER JOIN EnumValues ON mb_role = nv_id' +
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

var queryMembers = 'SELECT us_sid, us_alias, us_firstName, us_lastName, us_email, us_picture, nv_name' +
' FROM Members' +
' INNER JOIN Tertulias ON mb_tertulia = tr_id' +
' INNER JOIN Users ON mb_user = us_id' +
' INNER JOIN EnumValues ON mb_role = nv_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid' +
' AND tr_id = @tertulia';

var queryScheduleType = 'SELECT nv_name' +
' FROM Tertulias' +
' INNER JOIN Schedules ON tr_schedule = sc_id' +
' INNER JOIN EnumValues ON sc_recurrency = nv_id' +
' INNER JOIN Members ON mb_tertulia = tr_id' +
' INNER JOIN Users ON mb_user = us_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid' +
' AND tr_id = @tertulia';

var queryScheduleMonthlyW = 'SELECT mw_dow, mw_weeknr, mw_is_fromstart, mw_skip' +
' FROM MonthlyW' +
' INNER JOIN Schedules ON mw_schedule = sc_id' +
' INNER JOIN Tertulias ON tr_schedule = sc_id' +
' INNER JOIN Members ON mb_tertulia = tr_id' +
' INNER JOIN Users ON mb_user = us_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid' +
' AND tr_id = @tertulia';

var queryItems = 'SELECT it_id, it_name' +
' FROM Items' +
' INNER JOIN Tertulias ON it_tertulia = tr_id' +
' INNER JOIN Members ON mb_tertulia = tr_id' +
' INNER JOIN Users ON mb_user = us_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid' +
' AND tr_id = @tertulia';

var queryTemplates = 'SELECT tp_id, tp_name' +
' FROM Templates' +
' INNER JOIN Tertulias ON tp_tertulia = tr_id' +
' INNER JOIN Members ON mb_tertulia = tr_id' +
' INNER JOIN Users ON mb_user = us_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid' +
' AND tr_id = @tertulia';

var queryTemplate = 'SELECT it_id, it_name' +
' FROM QuantifiedItems' +
' INNER JOIN Items ON qi_item = it_id' +
' INNER JOIN Templates ON qi_template = tp_id' +
' INNER JOIN Tertulias ON tp_tertulia = tr_id' +
' INNER JOIN Members ON mb_tertulia = tr_id' +
' INNER JOIN Users ON mb_user = us_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid' +
' AND tr_id = @tertulia'
' AND tp_name = @template';

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
        if (typeof tr_id === typeof undefined) { // /tertulias
            console.log('Preparing to get all my Tertulias');
            selectedQuery = queryTertulias;
            paramsT['sid'] = sql.NVarChar; // String -> sql.NVarChar; Number -> sql.Int; Boolean -> sql.Bit; Date -> sql.DateTime; Buffer -> sql.VarBinary; sql.Table -> sql.TVP
            paramsV = {'sid': req.azureMobile.user.id };
        } else { 
            var sub = req.query.sub;
            paramsT['sid'] = sql.NVarChar; paramsT['tertulia'] = sql.NVarChar;
            paramsV = {'sid': req.azureMobile.user.id, 'tertulia': tr_id };
            if (typeof sub === typeof undefined) { // /tertulias?id=[0-9]
                selectedQuery = queryTertuliaX;
            } else {
                switch (sub) {
                    case 'locations': // /tertulias?id=[0-9]&sub=locations
                        selectedQuery = queryLocations;
                        break;
                    case 'defaultLocation': // /tertulias?id=[0-9]&sub=defaultLocation
                        console.log('Preparing to get the ' + sub + ' of my Tertulia with id: ' + tr_id);
                        selectedQuery = queryDefaultLocation;
                        break;
                    case 'scheduleType': // /tertulias?id=[0-9]&sub=scheduleType
                        console.log('Preparing to get the ' + sub + ' of my Tertulia with id: ' + tr_id);
                        selectedQuery = queryScheduleType;
                        break;
                    case 'members': // /tertulias?id=[0-9]&sub=members
                        console.log('mPreparing to get the ' + sub + ' of my Tertulia with id: ' + tr_id);
                        selectedQuery = queryMembers;
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
                            case 'MonthlyW': // /tertulias?id=[0-9]&sub=schedule&sel=MonthlyW
                                selectedQuery = queryScheduleMonthlyW;
                                break;
                            default:
                                console.log('Bum - de dentro.');
                                res.sendStatus(400);
                                return;
                        }
                        break;
                    case 'items': // /tertulias?id=[0-9]&sub=items
                        console.log('iPreparing to get the ' + sub + ' of my Tertulia with id: ' + tr_id);
                        selectedQuery = queryItems;
                        break;
                    case 'templates': // /tertulias?id=[0-9]&sub=templates
                        console.log('Preparing to get the ' + sub + ' of my Tertulia with id: ' + tr_id);
                        selectedQuery = queryTemplates;
                        break;
                    case 'template': // /tertulias?id=[0-9*]&sub=template&value=[A-z*]
                        console.log('Preparing to get the ' + sub + ' of my Tertulia with id: ' + tr_id);
                        var template = req.query.value;
                        console.log('Template: ' + template);
                        if (typeof template === typeof undefined) {
                            res.sendStatus(400);
                            return;
                        }
                        console.log('Preparing to get the ' + sub + ' ' + template + ' of my Tertulia with id: ' + tr_id);
                        selectedQuery = queryTemplate;
                        paramsT['template'] = sql.NVarChar;
                        paramsV['template'] = template;
                        break;
                    default:
                        console.log('Bum - de fora.');
                        res.sendStatus(400);
                        return;
                }
            },

            register: function (api) {
                api.get(‘*’, getImplementation);
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

exports.register: function (api) {
    api.get(‘*’, getImplementation);
}

function getImplementation(req, res) {
    console.log('In calculator');
    console.log('operation = ' + operation);
    console.log('x = ' + x);
    console.log('y = ' + y);
    var x = parseInt(req.query.x, 10);
    var y = parseInt(req.query.y, 10);
    var path = req.path;
    var operation = path.substring(‘/api/calculator/’.length);
    calculateAndRespond(x, y, operation, res);
}

function calculateAndRespond(x, y, op, res) {
    var result;
    if (op === ‘add’) {
        result = x + y;
    } elseif (op == ‘sub’) {
        result = x – y;
    } else {
        res.send(400, { error: ‘Operation “‘ + op + ‘” not supported’ });
    }
    res.send(200, { result: result });
}
