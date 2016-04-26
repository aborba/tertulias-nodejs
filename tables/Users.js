var azureMobileApps = require('azure-mobile-apps');

var table = azureMobileApps.table();

table.columns = {
	"userId": "string",
    "alias": "string",
    "email": "string"
};

table.dynamicSchema = false;

table.access = 'authenticated'; 

table.read(function(context) {
	context.query.where({ userId: context.user.id });
	return context.execute();
});

table.insert(function(context) {
	context.item.userId = context.user.id;
	return context.execute();
});

table.update(function(context) {
	context.item.userId = context.user.id;
	return context.execute();
});

table.delete(function(context) {
	context.query.where({ userId: context.user.id });
	return context.execute();
});

module.exports = table;
