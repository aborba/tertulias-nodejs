var azureMobileApps = require('azure-mobile-apps');

var table = azureMobileApps.table();

table.columns = {
	"userId": "string",
    "text": "string",
    "complete": "boolean"
};

table.dynamicSchema = false;

table.access = 'anonymous'; // anonymous | authenticated | disabled


// table.read(function (context) {
//   context.query.where({ userId: context.user.id });
//   return context.execute();
// });

// table.insert(function (context) {
//   context.item.userId = context.user.id;
//   return context.execute();
// });

//table.update(function (context) {
//  return context.execute();
//});

//table.delete(function (context) {
//  return context.execute();
//});

module.exports = table;
