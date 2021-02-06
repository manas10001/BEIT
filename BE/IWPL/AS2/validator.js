//live validate for blank input
function liveValidateBlank(arg){
    var data = document.getElementById(arg).value;
    if(data.length==0)
        document.getElementById(arg).style.border = "1px solid red";
    else
        document.getElementById(arg).style.border = "1px solid green";
}
//live validation for strings
function liveValidateStr(arg){
    var dat = document.getElementById(arg).value;
    var reg=/^[A-Za-z][A-Za-z\s]*$/;
    if(reg.test(dat)==true)
        document.getElementById(arg).style.border = "1px solid green";
    else
        document.getElementById(arg).style.border = "1px solid red";
}

//validates size 2nd parameter should be length of 1st parameter
function lengthValidate(arg, length){
    var dat = document.getElementById(arg).value;
    
    if(dat.length != length)
        document.getElementById(arg).style.border = "1px solid red";
    else
        document.getElementById(arg).style.border = "1px solid green";
}

//validates email with regexp
function validateEmail(arg){
    var dat = document.getElementById(arg).value;
		var reg = /^([A-Za-z0-9_\-\.])+@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
		if(reg.test(dat)==true)
			document.getElementById(arg).style.border = "1px solid green";
		else
			document.getElementById(arg).style.border = "1px solid red";
}