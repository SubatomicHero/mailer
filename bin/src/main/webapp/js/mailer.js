/**
 * This method gets the data from the form and processes it
 */
function SendData() {
	// variables for the email details panel
	var er = $("#emailRecipient").val();
	var st = $("#subjectText").val();
	var bt = $("#bodyText").val();
	var sys = $("#systemName").val();
	var ef = $('input[name=emailFormat]:checked').val();
	var et = $("#emailType").val();
	var ug = $('input[name=urgency]:checked').val();
	
	// variables for the user details panel
	var sal = $("#salutations").val();
	var fn = $("#firstName").val();
	var sn = $("#surName").val();
	var ad1 = $("#address1").val();
	var ad2 = $("#address2").val();
	var ad3 = $("#address3").val();
	var cc = $("#countryCode").val();
	var lc = $("#languageCode").val();
	var pc = $("#postCode").val();
	var tn = $("#telNo").val();
	if (sal == "None") {
		sal = "";
	}
	
	// the json string built from the input details
	var json = '{"recipients": [{"emailRecipient":"' + er + '","salutation":"' + sal + '","firstName":"' + fn + '","surname":"' + sn + '","address1":"' + ad1 + '","address2":"' + ad2
	+ '","address3":"' + ad3 + '","countryId":"' + cc + '","languageId":"' + lc + '","postCode":"' + pc + '","telephoneNumber":"' + tn + '"}], "subjectText":"' + st
	+ '","bodyText":"' + bt + '","emailFormat":"' + ef + '","systemName":"' + sys + '","emailType":"' + et + '","createdBy":"Phil","priority":"' + ug + '"}'; // TODO Phil M is temp
	
	$.ajax({
        type: 'POST',
        url: 'rest/mailer/addjob',
        data: json,
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function (data) {
        	$("#replyMessage").html("<b>" + data.JobStatus + ": " + data.JobMessage + "</b>");
        },
        error: function (xhr, status, error) {
            // check for errors  ex. alert(xhr.statusText);
        	alert(xhr.statusText);
        }
    });

}

/** This method sends the email address into the blacklist table */
function SendEmail() {
	var id = GetURLParameter('uuid');
	var language = navigator.userLanguage || navigator.language;
	if (language == "en-US") {
		language = "en";	// to make sure any en user see's english translations
	}
	var json = '{"language":"' + language + '","uuid":"' + id + '"}';
	$.ajax({
		type: 'POST',
		url: 'rest/mailer/unsubscribe',
		data: json,
		dataType: 'json',
		contentType: 'application/json; charset=utf-8',
		success : function (data) {
			$("#responseMessage").text(data.Message);
		},
		error: function (xhr, status, error) {
			alert(data);
			//			alert(xhr.statusText);
		}
	});
}

/** This method takes the value from a parameter in a url */
function GetURLParameter(sParam)
{
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++)
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam)
        {
            return sParameterName[1];
        }
    }
}