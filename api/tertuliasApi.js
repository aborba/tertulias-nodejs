var express = require('express'),
bodyParser = require('body-parser'),

    authenticate = require('azure-mobile-apps/src/express/middleware/authenticate'),
    authorize = require('azure-mobile-apps/src/express/middleware/authorize');

const queryTertulias = 'SELECT DISTINCT tr_id, tr_name, tr_subject, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude, sc_recurrency, tr_is_private, nv_name' +
' FROM Tertulias' +
' INNER JOIN Locations  ON tr_location = lo_id' +
' INNER JOIN Schedules  ON tr_schedule = sc_id' +
' INNER JOIN Members    ON mb_tertulia = tr_id' +
' INNER JOIN Users      ON mb_user = us_id' +
' INNER JOIN EnumValues ON mb_role = nv_id' +
' WHERE tr_is_cancelled = 0 AND us_sid = @sid';

module.exports = function (configuration) {
    var router = express.Router();

    router.get('/', (req, res, next) => {
    	req.azureMobile.tables('Tertulias')
            .read()
            .then(results => res.json(results))
            .catch(next); // it is important to catch any errors and log them
    });

    return router;

}

