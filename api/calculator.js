var express = require('express');
var util = require('../util');
var u = require('azure-mobile-apps/src/auth/user');

var completeError = function(err, res) {
    if (err) {
        console.error(err);
        if (res) res.sendStatus(500);
    }
};

var api = {

    all: function (req, res, next) {
        console.log('In: api/calculator');
        return next();
    },

    get: function (req, res, next) {
        console.log('In: GET api/calculator');
    }

};

api.access = 'authenticated';
module.exports = api;
