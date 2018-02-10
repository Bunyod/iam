$('.message a').click(function() {
  $('form').animate(
    { height: "toggle", opacity: "toggle" },
    "slow"
  );
});

//$('#regpwd').keyup(function() {
//  var pwd = $(this).val();
//  // validate the length
//  if(pwd.length < 8) {
//    $(this).removeClass('valid').addClass('invalid');
//  } else $(this).removeClass('invalid').addClass('valid');
//  // validate letter
//  if(pwd.match(/[A-z]/)) {
//    $(this).removeClass('invalid').addClass('valid');
//  } else $(this).removeClass('valid').addClass('invalid');
//  // validate capital letter
//  if(pwd.match(/[A-Z]/)) {
//    $(this).removeClass('invalid').addClass('valid');
//  } else $(this).removeClass('valid').addClass('invalid');
//  // validate number
//  if(pwd.match(/\d/)) {
//    $(this).removeClass('invalid').addClass('valid');
//  } else $(this).removeClass('valid').addClass('invalid');
//  // validate space
//  if(pwd.match(/[^a-zA-Z0-9\-\/]/)) {
//    $(this).removeClass('invalid').addClass('valid');
//  } else $(this).removeClass('valid').addClass('invalid');
//});

$('.register-form button').click(function() {
  $.ajax({
    method: "POST"
  , url: "https://localhost:9443/register"
  , dataType : "json"
  , contentType: "application/json"
  , processData: false
  , data: JSON.stringify({
      'username' : $('#regi').val().trim()
    , 'password' : $('#regp').val().trim()
    , 'forename' : $('#forename').val().trim()
    , 'surname' : $('#surname').val().trim()
    , 'phone' : $('#phone').val().trim()
    })
  }).done(function(data, textStatus, jqXHR) {
    if(data.hasOwnProperty('error')) {
      var msg = "Status (Code | Text): " + jqXHR.status + " | " + jqXHR.statusText + "\n";
      msg += "Message: " + jqXHR.responseText + "\n";
      msg += "Data: " + data.error;
      console.log(msg);
    }
    else window.location.href = "/"
  }).fail(function(jqXHR, textStatus, errorThrown) {
    var msg = "Status (Code | Text): " + jqXHR.status + " | " + jqXHR.statusText + "\n";
    msg += "Message: " + jqXHR.responseText;
    console.log(msg);
  })
});