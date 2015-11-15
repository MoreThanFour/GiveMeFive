// Method used to send Push Notifications to each user who have friends close to him.

Parse.Cloud.define("hello", function(request, response)
{
    // And we push notification to the current user's Installation.
    var queryInstallation = new Parse.Query(Parse.Installation);
    queryInstallation.equalTo('deviceToken', request.object.get('deviceToken'));

    Parse.Push.send({
        where: queryInstallation,
        data: {
            alert: "Hey! " + request.object.get('name') + " is really close to you. Go give him five!";
        },
        success: function()
        {
            // Push was successful
            alert("It works");
        },
        error: function(error)
        {
            // Handle error
            alert("Hummm, I miss something");
        }
    })

    response.success();
}
