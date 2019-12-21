<%-- 
    Document   : index
    Created on : 20 Sep, 2019, 9:17:09 AM
    Author     : pict2
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>SCHOOL WALA</title>
	<link rel="stylesheet" type="text/css" href="./css/common.css">
	<style>
		.inputst{
			border-radius: 10px;
			color: black;
			padding: 20px 30px;
			border: 1px solid red;
			min-width: 45vw;
			text-decoration: none;
			display: inline-block;
			margin: 4px 2px;	
		}
		input:hover{
			display: inline-block;
			box-shadow: 0 0 20px rgba(0,0,0,0.6);
			/*background-color: rgb(255,255,255);*/
		}

		.titlelabel{
			background: linear-gradient(140deg, #f12711 10%, #f5af19 );
			border-radius: 10px;
			color: white;
			padding: 20px;
			min-width: 150px;
			text-decoration: none;
			display: inline-block;
			margin: 4px 2px;
		}

		.logbtn{
			background: rgb(255,255,255); 
			border: 1px solid #2AA600;
			border-radius: 10px;
			color: rgb(0, 0, 0);
			padding: 10px 15px;
			min-width: 120px;
			text-decoration: none;
			display: inline-block;
			margin: 4px 2px;
		}

		.logbtn:hover{
			background: #2AA600;
			color: rgb(255, 255, 255);
		}

		.resetbtn{
			float: right;
			border-radius: 10px;
			border: 1px solid red;
			color: red;
			padding: 10px 15px;
			min-width: 120px;
			text-decoration: none;
			display: inline-block;
			margin: 4px 2px;	
			background-color: white;
		}
		.resetbtnmain{
			border-radius: 10px;
			border: 1px solid #4169E1;
			color: black;
			padding: 10px 15px;
			min-width: 120px;
			text-decoration: none;
			display: inline-block;
			margin: 4px 2px;	
			background-color: white;
		}
		.resetbtn:hover,.resetbtnmain:hover{
			background-color: red;
			color: white;
			border:none;
		}
		label{
			padding-left:1vh;
			float: left;
		}
		legend,fieldset{
			border-radius: 10px;
			padding: 2vh;
		}
		input:focus, textarea:focus, select:focus{
        	outline: none;
    	}

	</style>
</head>
<body>
	<!-- there goes navbar -->
<br>
	<center>
	<!-- 	PAGE CONTENT -->
		<div class="txtbox" >
			<input type="button" class="titlelabel" onclick="location.href='signup.jsp'" value="Sign Up">
			<input type="button" class="titlelabel" onclick="location.href='index.jsp'" value="Login"><br><br>
			<!-- Login Form --><br>
		    <form action="Login" method="post">
		   	    <fieldset id="fs1">
		   	    	<legend>Account Details</legend>
					<label>Login ID:*</label><br>
					<input class="inputst" type="text" id="login" name="uname" placeholder="Login-ID" onmouseleave="changeBorder('login');">
					
					<br><br>

					<label>Password:*</label><br>
					<input class="inputst" type="password" id="password" name="pass" placeholder="Password" onmouseleave="changeBorder('password');">
					<br><br>

		  	  	</fieldset>
				<br><br>
		      <input type="submit" class="logbtn" onclick="return validate();" value="Log In">
		      <input type="reset" class="resetbtnmain" value="Reset All">
		    </form>
		</div>
	</center>

<script>
        //FUNCTION TO CHANGE BORDER COLOR
        function changeBorder(arg)
	{
		document.getElementById(arg).style.border = "1px solid green";
	}
	//FUNCTION TO RESET A SINGLE FIELD
	function resetThis(arg) {
		//console.log(arg);
		document.getElementById(arg).value=null;
	}

	//FUNCTION TO VALIDATE INPUTS ON SUBMIT
	function validate(){
		var login = document.getElementById("login").value;
		var pas = document.getElementById("password").value;

		//no field should be blank
		if(login.length==0 || pas.length==0)
		{
			alert("All fields required");
			return false;
		} 

		//validate pass
		if(pas.length<8 || pas2.length<8){
			alert("Incorrect Password!");		
			return false;
		}
	}
</script>

</body>
</html>
