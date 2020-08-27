
	function changeBorder(arg)
	{
		document.getElementById(arg).style.border = "1px solid green";
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
		var reg = /^([A-Za-z0-9_\-\.])+@([A-Za-z0-9_\-])+\.([A-Za-z]{2})+\.([A-Za-z]{2})$/;
		var reg2 = /^([A-Za-z0-9_\-\.])+@([A-Za-z0-9_\-])+\.([A-Za-z]{3})$/;
		
		if(reg.test(dat)==true)
			document.getElementById("email").style.border = "1px solid green";
		else if(reg2.test(dat)==true)
			document.getElementById("email").style.border = "1px solid green";
		else
			document.getElementById("email").style.border = "1px solid red";
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


	//FUNCTION TO VALIDATE INPUTS ON SUBMIT
	function validate(){
            
		var name = document.getElementById("name").value;
		var pas = document.getElementById("password").value;
		var pas2 = document.getElementById("repassword").value;
		var mail = document.getElementById("email").value;
		var mob = document.getElementById("mob").value;


		if(name.length==0)
		{
			alert("All fields required");
			document.getElementById("name").style.border = "1px solid red";
			document.getElementById("name").focus();
			return false;
		}
                else if( pas.length==0 )
		{
			alert("All fields required");
			document.getElementById("password").style.border = "1px solid red";
			document.getElementById("password").focus();
			return false;
		}
		else if( pas2.length==0 )
		{
			alert("All fields required");
			document.getElementById("repassword").style.border = "1px solid red";
			document.getElementById("repassword").focus();
			return false;
		}
		else if( mail.length==0 )
		{
			alert("All fields required");
			document.getElementById("email").style.border = "1px solid red";
			document.getElementById("email").focus();
			return false;
		}
		else if( mob.length==0 )
		{
			alert("All fields required");
			document.getElementById("mob").style.border = "1px solid red";
			document.getElementById("mob").focus();
			return false;
		}
				

		//FIELD VALIDATIONS
		//validate name being string only, allow space
		var regstr=/^[A-Za-z][A-Za-z\s]*$/;
		if(regstr.test(name)==false){
			alert("Name should be string only!");
			document.getElementById("name").style.border = "1px solid red";
			document.getElementById("name").focus();
			return false;
		}
                
		//validate pass
		if(pas.length<8 || pas2.length<8){
			alert("Password length too small");	
			document.getElementById("password").style.border = "1px solid red";
			document.getElementById("repassword").style.border = "1px solid red";
			document.getElementById("password").focus();	
			return false;
		}else if(pas!=pas2){
			alert("Password and retyped password dont match");
			document.getElementById("password").style.border = "1px solid red";
			document.getElementById("repassword").style.border = "1px solid red";
			document.getElementById("password").focus();
			return false;
		}

// ^([A-Za-z0-9_\-\.])+@([A-Za-z0-9_\-])+\.([A-Za-z]{2})+\.([A-Za-z]{2})$		//matches manas@yahoo.co.in
// ^([A-Za-z0-9_\-\.])+@([A-Za-z0-9_\-])+\.([A-Za-z]{3})$						//matches manas@gmail.com
		//validate email
		var reg = /^([A-Za-z0-9_\-\.])+@([A-Za-z0-9_\-])+\.([A-Za-z]{2})+\.([A-Za-z]{2})$/;
		var reg2 = /^([A-Za-z0-9_\-\.])+@([A-Za-z0-9_\-])+\.([A-Za-z]{3})$/;
		
		if(reg.test(mail)==false && reg2.test(mail)==false){
			alert("Invalid email");
			document.getElementById("email").style.border = "1px solid red";
			document.getElementById("email").focus();
			return false;
		}
		
		//validate mobile number
		
		var mobreg = /^\d{10}$/;
		if(!mobreg.test(mob)==true){
			alert("Invalid mobile number");
			document.getElementById("mob").style.border = "1px solid red";
			document.getElementById("mob").focus();
			return false;
		}

		if(mob.charAt(0)!='9' && mob.charAt(0)!='8' && mob.charAt(0)!='7'){
			alert("Invalid start of mobile number");
			document.getElementById("mob").style.border = "1px solid red";
			document.getElementById("mob").focus();
			return false;
		}
	    
	}
