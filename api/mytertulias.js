var express = require('express');
var util = require('../util');
var u = require('azure-mobile-apps/src/auth/user');
var sql = require('mssql');

var queryTertulias = 'SELECT DISTINCT tr_id, tr_name, tr_subject, tr_location, tr_schedule, tr_is_private' +
' FROM Tertulias' +
' INNER JOIN Members ON tr_id = mb_tertulia' +
' INNER JOIN Users ON mb_user = us_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid';

var queryTertuliaX = queryTertulias; //+ ' AND tr_id = @tertulia';

var queryLocations = 'SELECT DISTINCT lo_id, lo_name, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude' +
' FROM Locations' +
' INNER JOIN Tertulias ON lo_tertulia = tr_id' +
' INNER JOIN Members ON mb_tertulia = tr_id' +
' INNER JOIN Users ON mb_user = us_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid' +
' AND tr_id = @tertulia';

var queryDefaultLocation = 'SELECT lo_id, lo_name, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude' +
' FROM Locations' +
' INNER JOIN Tertulias ON lo_tertulia = tr_id' +
' INNER JOIN Members ON mb_tertulia = tr_id' +
' INNER JOIN Users ON mb_user = us_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid' +
' AND tr_id = @tertulia'
' AND lo_id = (SELECT tr_location FROM Tertulias WHERE tr_id = @tertulia)';

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
        //console.log(req);

        var selectedQuery;
        var paramsT = [];
        var paramsV = [];
        var id = req.query.id;
        console.log('id: ' + id);
        if (typeof id === typeof undefined) {
            console.log('Preparing to get all my Tertulias');
            selectedQuery = queryTertulias;
            paramsT['sid'] = sql.NVarChar;
            paramsV.push({ 'sid': req.azureMobile.user.id });
        } else {
            var sub = req.query.sub;
            if (typeof sub === typeof undefined) {
                console.log('Preparing to get my Tertulia with id: ' + id);
                selectedQuery = queryTertuliaX;
            } else {
                if (sub === 'locations') {
                    console.log('Preparing to get all the ' + sub + ' of my Tertulia with id: ' + id);
                    selectedQuery = queryLocations;
                } else {
                    res.sendStatus(500);
                    return;
                }
            }
        }

        var connection = new sql.Connection(util.sqlConfiguration);
        connection.connect(function(err) {
            var sqlRequest = new sql.Request(connection);
            var preparedStatement = new sql.PreparedStatement(connection);
            // String -> sql.NVarChar; Number -> sql.Int; Boolean -> sql.Bit; Date -> sql.DateTime; Buffer -> sql.VarBinary; sql.Table -> sql.TVP
            //preparedStatement.input('sid', sql.NVarChar);
            console.log('begin keys');
            for (var key in paramsT) {
                console.log('key: ' + key);
                //preparedStatement.input(key, paramsT[key]);
                preparedStatement.input('sid', paramsT[key]);
            }
            console.log('end keys');
            preparedStatement.prepare(selectedQuery, function(err) {
                if (err) { completeError(err, res); return; }
                preparedStatement.execute({ sid: req.azureMobile.user.id }, 
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

/*
var api = {

    all: function (req, res, next) {
        console.log('In: api/tertulias');
        return next();
    },

    get: function (req, res, next) {
        var connection = new sql.Connection(util.sqlConfiguration);
        connection.connect(function(err) {
            var sqlRequest = new sql.Request(connection);
            var preparedStatement = new sql.PreparedStatement(connection);
            // String -> sql.NVarChar; Number -> sql.Int; Boolean -> sql.Bit; Date -> sql.DateTime;
            // Buffer -> sql.VarBinary; sql.Table -> sql.TVP
            preparedStatement.input('sid', sql.NVarChar);
            preparedStatement.prepare(queryTertulias, function(err) {
                if (err) { completeError(err, res); return; }
                preparedStatement.execute({ sid: req.azureMobile.user.id }, 
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
*/

api.access = 'authenticated';
module.exports = api;
