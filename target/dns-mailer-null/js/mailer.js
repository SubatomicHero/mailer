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
	var tp = $("#template").val();
	
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
//	var json = '{"recipients": [{"emailRecipient":"' + er + '","salutation":"' + sal + '","firstName":"' + fn + '","surname":"' + sn + '","address1":"' + ad1 + '","address2":"' + ad2
//	+ '","address3":"' + ad3 + '","countryId":"' + cc + '","languageId":"' + lc + '","postCode":"' + pc + '","telephoneNumber":"' + tn + '"}], "subjectText":"' + st
//	+ '","bodyText":"' + bt + '","emailFormat":"' + ef + '","systemName":"' + sys + '","emailType":"' + et + '","createdBy":"Phil","priority":"' + ug
//	+ '","templateId":"2"}';
	
//	var json = '{"templateId":"2","emailType":"marketing","createdBy":"NSCFR","recipients":[{"emailRecipient": "martin.whittington@snapon.com","salutation": "Mr","firstName": "Martin","surname": "Whittington","address1": "WhiteHouse","address2": "Washington","address3": "USA","countryId": "74","languageId": "25","postCode": "90210","telephoneNumber": "01818118181"}],"priority":"MEDIUM","emailTags":[{"TEXT:SUBJECTTEXT":"khjkhjkhjkjhkhj"}, {"TEXT:SALUTATION":"Mr"}, {"TEXT:FIRSTNAME":"Martin"}, {"TEXT:SURNAME":"Whittington"}, {"TEXT:BODYTEXT":"kjhkhjkhjkhj<br><br>khjkhjkhj"}, {"IMAGE:HEADER":""}],"systemName":"PRM_NISSAN","subjectText":"khjkhjkhjkjhkhj","bodyText":"kjhkhjkhjkhj<br><br>khjkhjkhj","emailFormat":"html"}';
//	var json = '{"templateId":"2","emailType":"marketing","createdBy":"NSCFR","recipients":[{"emailRecipient": "martin.whittington@snapon.com","text:salutation": "Mr","text:firstName": "Martin","text:surname": "Whittington","text:address1": "WhiteHouse","text:address2": "Washington","text:address3": "USA","text:countryId": "74","text:languageId": "25","text:postCode": "90210","text:telephoneNumber": "01818118181"}],"priority":"MEDIUM","emailTags":[{"IMAGE:HEADER":""}, {"TEXT:SUBJECTTEXT":"hgfhgfhgfh"}, {"TEXT:SALUTATION":"Mr"}, {"TEXT:FIRSTNAME":"Martin"}, {"TEXT:SURNAME":"Whittington"}, {"TEXT:BODYTEXT":"gfhgfh<br><br>gfhgfhgfhgfhgf"}],"systemName":"PRM_NISSAN","text:bodyText":"gfhgfh<br><br>gfhgfhgfhgfhgf","text:subjectText":"hgfhgfhgfh","emailFormat":"html"}';
//	var json = '{"templateId":"0","emailType":"marketing","createdBy":"NSCFR","recipients":[{"text:emailRecipient": "martin.whittington@snapon.com","text:salutation": "Mr","text:firstName": "Martin","text:surname": "Whittington","text:address1": "WhiteHouse","text:address2": "Washington","text:address3": "USA","text:countryId": "74","text:languageId": "25","text:postCode": "90210","text:telephoneNumber": "01818118181"}],"priority":"MEDIUM","emailTags":[{"IMAGE:HEADER":""}, {"TEXT:SUBJECTTEXT":"hgfhgfhgfhgfhgf"}, {"TEXT:SALUTATION":"Mr"}, {"TEXT:FIRSTNAME":"Martin"}, {"TEXT:SURNAME":"Whittington"}, {"TEXT:BODYTEXT":"Welcome to CCM!"}],"systemName":"PRM_NISSAN","text:bodyText":"{TEXT:SUBJECTTEXT}<br><br>Welcome to CCM {TEXT:FIRSTNAME}","text:subjectText":"hgfhgfhgfhgfhgf","emailFormat":"html"}';
//	var json = '{"templateId":"0","emailType":"generic","createdBy":"NSC","recipients":[{"text:emailRecipient": "martin.whittington@snapon.com","text:salutation": "Mrs","text:firstName": "Iryna","text:surname": "Torjanik","text:address1": "ул.Инструментальная 2а","text:address2": "","text:address3": "Чернигов","text:countryId": "231","text:languageId": "440","text:postCode": "","text:telephoneNumber": "+38 (097) 774-78-46"}],"priority":"HIGH","emailTags":[], "systemName":"RENEU","text:bodyText":"gdfgdfgdfg<div>{TEXT:FIRSTNAME}</div><br><br>null","text:subjectText":"gfdgdgdfgdf","emailFormat":"html"}';
	var json = '{"emailFrom":"do-not-reply@snapon.com","text:subjectText":"Data Quality for Nottelmanns Autohandel Aps  for October   submission","text:bodyText":"Hello.  Thank you for your submission for October  . While analysing the data, I found some values, which I would like you ask you to check. Please confirm or, if necessary, correct the following data:","systemName":"OPS","emailType":"generic","createdBy":"NSC","recipients":[{"text:emailRecipient":"malcolm.horne@snapon.com","uuid":90491822163"}],"templateId":"0","priority":"HIGH","emailTags":[]}';
	
	$.ajax({
        type: 'POST',
        url: 'rest/mailer/addjob',
        data: json,
        dataType: 'json',
        contentType: 'application/json',
        success: function (data) {
        	$("#replyMessage").html("<b>" + data.jobMessage + "</b>");
        },
        error: function (xhr, status, error) {
            // check for errors  ex. alert(xhr.statusText);
        	alert(xhr.statusText);
        }
    });

}

/** This method sends the email address into the blacklist table */
function unSubscribe() {
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