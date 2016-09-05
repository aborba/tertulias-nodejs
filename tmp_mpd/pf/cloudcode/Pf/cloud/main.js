// New comment
Parse.Cloud.define("subscribe", function(request, response) {
  var currentUser = Parse.User.current();
  var installationId = request.installationId;
  if (currentUser) {
    var isEmv = currentUser.get('emailVerified');
    if (isEmv) {
      var query = new Parse.Query(Parse.Installation);
      var channels = query.equalTo('installationId', installationId).get('channels');
      response.success("Ok");
    } else {
      response.error("User email not verified.");
    }
  } else {
    response.error("No user logged in.");
  }
});
