//FUNCTION TO RESET A SINGLE FIELD
	function resetThis(arg) {
		//console.log(arg);
		for(i=0;i<arg.length;i++)
		{
			document.getElementById(arg[i]).value=null;
		}
	}

	//live validate mobilenumber
	function liveValidateMob(){
		var dat = document.getElementById("mob").value;
		var reg = /^\d{10}$/;
		if(reg.test(dat)==true)
			document.getElementById("mob").style.border = "1px solid green";
		else
			document.getElementById("mob").style.border = "1px solid red";
	}	
	//live validate email
	function liveValidateMail(){
		var dat = document.getElementById("email").value;
		var reg = /^([A-Za-z0-9_\-\.])+@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
		if(reg.test(dat)==true)
			document.getElementById("email").style.border = "1px solid green";
		else
			document.getElementById("email").style.border = "1px solid red";
	}
	//live validate pincode
	function liveValidatePin(){
		var pin = document.getElementById("pin").value;
		var pinreg = /^\d{6}$/;
		if(pinreg.test(pin)==true)
			document.getElementById("pin").style.border = "1px solid green";
		else
			document.getElementById("pin").style.border = "1px solid red";
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


	// //FUNCTION TO VALIDATE INPUTS ON SUBMIT
	// function validate(){
	// 	var name = document.getElementById("name").value;
	// 	var gender = document.getElementById("gender").value;
	// 	var login = document.getElementById("login").value;
	// 	var city = document.getElementById("city").value;
	// 	var pas = document.getElementById("password").value;
	// 	var pas2 = document.getElementById("repassword").value;
	// 	var mail = document.getElementById("email").value;
	// 	var mob = document.getElementById("mob").value;
	// 	var pin = document.getElementById("pin").value;
	// 	var dat = document.getElementById("date").value;

	// 	//console.log("in validate");

	// 	// no field should be blank
	// 	// if(dat.length==0 || name.length==0 || gender.length==0 ||login.length==0||city.length==0 || pas.length==0 || pas2.length==0 || mail.length==0 || mob.length==0 || pin.length==0)
	// 	// {
	// 	// 	alert("All fields required");
	// 	// 	return false;
	// 	// } 

	// 	if(name.length==0)
	// 	{
	// 		alert("All fields required");
	// 		document.getElementById("name").style.border = "1px solid red";
	// 		document.getElementById("name").focus();
	// 		return false;
	// 	}
	// 	else if( gender.length==0 )
	// 	{
	// 		alert("All fields required");
	// 		document.getElementById("gender").style.border = "1px solid red";
	// 		document.getElementById("gender").focus();
	// 		return false;
	// 	}
	// 	else if( dat.length==0 )
	// 	{
	// 		alert("All fields required");
	// 		document.getElementById("date").style.border = "1px solid red";
	// 		document.getElementById("date").focus();
	// 		return false;
	// 	}
	// 	else if( city.length==0 )
	// 	{
	// 		alert("All fields required");
	// 		document.getElementById("city").style.border = "1px solid red";
	// 		document.getElementById("city").focus();
	// 		return false;
	// 	}
	// 	else if( pin.length==0)
	// 	{
	// 		alert("All fields required");
	// 		document.getElementById("pin").style.border = "1px solid red";
	// 		document.getElementById("pin").focus();
	// 		return false;
	// 	}
	// 	else if( mail.length==0 )
	// 	{
	// 		alert("All fields required");
	// 		document.getElementById("email").style.border = "1px solid red";
	// 		document.getElementById("email").focus();
	// 		return false;
	// 	}
	// 	else if( mob.length==0 )
	// 	{
	// 		alert("All fields required");
	// 		document.getElementById("mob").style.border = "1px solid red";
	// 		document.getElementById("mob").focus();
	// 		return false;
	// 	}
	// 	else if( login.length==0 )
	// 	{
	// 		alert("All fields required");
	// 		document.getElementById("login").style.border = "1px solid red";
	// 		document.getElementById("login").focus();
	// 		return false;
	// 	}
	// 	else if( pas.length==0 )
	// 	{
	// 		alert("All fields required");
	// 		document.getElementById("password").style.border = "1px solid red";
	// 		document.getElementById("password").focus();
	// 		return false;
	// 	}
	// 	else if( pas2.length==0 )
	// 	{
	// 		alert("All fields required");
	// 		document.getElementById("repassword").style.border = "1px solid red";
	// 		document.getElementById("repassword").focus();
	// 		return false;
	// 	}		


	// 	//FIELD VALIDATIONS
	// 	//validate name being string only, allow space
	// 	 var regstr=/^[A-Za-z][A-Za-z\s]*$/;
	// 	if(regstr.test(name)==false){
	// 		alert("Name should be string only!");
	// 		document.getElementById("name").style.border = "1px solid red";
	// 		document.getElementById("name").focus();
	// 		return false;
	// 	}	

	// 	//validate city name being string only, allow space
	// 	var regstr=/^[A-Za-z][A-Za-z\s]*$/;
	// 	if(regstr.test(city)==false){
	// 		alert("City name should be string only!");
	// 		document.getElementById("city").style.border = "1px solid red";
	// 		document.getElementById("city").focus();
	// 		return false;
	// 	}	

	// 	//validate pin code
	// 	var pinreg = /^\d{6}$/;

	// 	if(!pinreg.test(pin)==true){
	// 		alert("Invalid PIN code");
	// 		document.getElementById("pin").style.border = "1px solid red";
	// 		document.getElementById("pin").focus();
	// 		return false;
	// 	}

	// 	//validate email
	// 	var reg = /^([A-Za-z0-9_\-\.])+@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
	// 	if(reg.test(mail)==false){
	// 		alert("Invalid email");
	// 		document.getElementById("email").style.border = "1px solid red";
	// 		document.getElementById("email").focus();
	// 		return false;
	// 	}
		
	// 	//validate mobile number
		
	// 	var mobreg = /^\d{10}$/;
	// 	if(!mobreg.test(mob)==true){
	// 		alert("Invalid mobile number");
	// 		document.getElementById("mob").style.border = "1px solid red";
	// 		document.getElementById("mob").focus();
	// 		return false;
	// 	}

	// 	if(mob.charAt(0)!='9' && mob.charAt(0)!='8' && mob.charAt(0)!='7'){
	// 		alert("Invalid start of mobile number");
	// 		document.getElementById("mob").style.border = "1px solid red";
	// 		document.getElementById("mob").focus();
	// 		return false;
	// 	}

	// 	//validate pass
	// 	if(pas.length<8 || pas2.length<8){
	// 		alert("Password length too small");	
	// 		document.getElementById("password").style.border = "1px solid red";
	// 		document.getElementById("repassword").style.border = "1px solid red";
	// 		document.getElementById("password").focus();	
	// 		return false;
	// 	}else if(pas!=pas2){
	// 		alert("Password and retyped password dont match");
	// 		document.getElementById("password").style.border = "1px solid red";
	// 		document.getElementById("repassword").style.border = "1px solid red";
	// 		document.getElementById("password").focus();
	// 		return false;
	// 	}
	    
	// }
