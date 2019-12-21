<%-- 
    Document   : signup
    Created on : 20 Sep, 2019, 9:23:34 AM
    Author     : pict2
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>SCHOOL WALA</title>
	<link rel="stylesheet" type="text/css" href="./css/common.css">
	<style>
		.inputstyle{
			border-radius: 10px;
			color: black;
			padding: 20px 30px;
			border: 1px solid red;
			 /*#ccc;*/
			min-width: 45vw;
			text-decoration: none;
			display: inline-block;
			margin: 4px 2px;	
		}
		.inputstyle:hover{
			display: inline-block;
			box-shadow: 0 0 20px rgba(0,0,0,0.6);
			/*min-width: 37vw;*/
			/*border: 1px solid orange;*/
		}

		select{
			border-radius: 10px;
			color: black;
			padding: 20px 30px;
			border: 1px solid red;
			/*#ccc;*/
			min-width: 45vw;
			text-decoration: none;
			display: inline-block;
			margin: 4px 2px;	
			background: white;
		}
		select:hover{
			display: inline-block;
			box-shadow: 0 0 20px rgba(0,0,0,0.6);
			/*min-width:37vw;*/
			/*border: 1px solid orange;*/
		}

		.signuplabel{
			border-radius: 10px;
			color: white;
			padding: 20px;
			min-width: 150px;
			text-decoration: none;
			display: inline-block;
			margin: 4px 2px;	
			background: linear-gradient(140deg, #f12711 10%, #f5af19 );
		}
		.signupbtn{
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

		.signupbtn:hover{
			background: #2AA600;
			color: rgb(255, 255, 255);
		}

		.resetbtn{
			/*float: right;*/
			border-radius: 10px;
			border: 1px solid #4169E1;
			color: black;
			padding: 10px 15px;
			min-width: 120px;
			min-height: 7vh;
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
			background-color: crimson;
			color: white;
			border:none;
		}
		label{
			padding-left:1vw;
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
			<input type="button" class="signuplabel" onclick="location.href='signup.jsp'" value="Sign Up">
			<input type="button" class="signuplabel" onclick="location.href='index.jsp'" value="Login"><br><br>
			<!-- Login Form --><br>
		    <form action="Register" method="post" id="ipform">
		   	    
                        <fieldset>
                                <label>User Name:*</label><br>
                                <input class="nam inputstyle" autocomplete="off" type="text" id="name" name="name" onkeyup="liveValidateStr('name');" placeholder="Name">

                                <label>Password:*</label><br>
                                <input class="inputstyle" autocomplete="off" type="password" id="password" name="pass" placeholder="Password">
                                <br><br>

                                <label>Retype Password:*</label><br>
                                <input class="inputstyle" autocomplete="off" type="password" id="repassword" name="repass" placeholder="Retype Password">
                                <br><br>
                                
                                <label>Email:*</label><br>
                                <input class="inputstyle" autocomplete="off" type="text" id="email" name="email" onkeyup="liveValidateMail();" placeholder="E-Mail ID">
                                <br><br>

                                <label>Mobile Number:*</label><br>
                                <input class="inputstyle" autocomplete="off" type="text" id="mob" name="mob" onkeyup="liveValidateMob();" placeholder="Mobile Number">
                                <br><br>
                        </fieldset>
				
		      <input type="submit" class="signupbtn" onclick="return validate();" value="Sign Up">
		      <input type="reset" class="resetbtnmain" value="Reset All">
		    </form>
		</div>
	</center>

	<script type="text/javascript">
		document.getElementById("mob").maxLength = "10";
	</script>
<script type="text/javascript" src="validateSignup.js"></script>

</body>
</html>
