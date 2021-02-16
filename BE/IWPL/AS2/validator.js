//live validate for blank input
function liveValidateBlank(arg){
    var data = document.getElementById(arg).value;
    if(data.length==0){
        document.getElementById(arg).style.border = "1px solid red";
        return false;
    }else{
        document.getElementById(arg).style.border = "1px solid green";
        return true;
    }
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
    
    if(dat.length < length)
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

function validateLoginForm(){

    if(!liveValidateBlank("login") || !liveValidateBlank("password")){
        alert("All fields are required!");
        liveValidateBlank("login");
        liveValidateBlank("password");
        return false;
    }else{
        alert("Login success!")
    }
}

function validateRegisterForm(){

    if(!liveValidateBlank("login") || !liveValidateBlank("password") || !liveValidateBlank("mail") || !liveValidateBlank("repassword")){
        alert("All fields are required!");
        liveValidateBlank("login");
        liveValidateBlank("password");
        liveValidateBlank("mail");
        liveValidateBlank("repassword");
        return false;
    }else{
        if(password != repassword){
            alert("password and retyped passwords dont match");
            document.getElementById("password").style.border = "1px solid red";
            document.getElementById("repassword").style.border = "1px solid red";
            return false;
        }
    }
    alert("Sucess!");
    return true;
}

function validateSelect(arg){
    var dat = document.getElementById(arg).value;
    if(dat!="select")
        document.getElementById(arg).style.border = "1px solid green";
    else
        document.getElementById(arg).style.border = "1px solid red";
}

function validateAddExp(){
    //entire form validation

    if(!liveValidateBlank("jobTitle") || !liveValidateBlank("company") || !liveValidateBlank("application") || !liveValidateBlank("technical") || !liveValidateBlank("hr") || !liveValidateBlank("tips") ){
        liveValidateBlank("jobTitle");
        liveValidateBlank("company");
        liveValidateBlank("application");
        liveValidateBlank("technical");
        liveValidateBlank("hr");
        liveValidateBlank("tips");
        alert("All fields are required!");
        return false;
    }
    alert("Sucess!");
    return true;
}