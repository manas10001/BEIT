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
			padding-left:14vh;
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
	<header>
		<nav class="header">
			<a href="index.html" class="logo"><img src="./img/logo.png" width="150px" alt="logo"></a>
			<!-- <h2>Education Rercommendation System</h2> -->
				<ul class="menu"> 
						<a href="index.html">Home</a>
						<div class="dropdown">
							<a href="cities.html" vspace="10">Select City</a>
							<div  class="dropdown-content">
								<a href="pune.html">Pune</a>
								<a href="mumbai.html">Mumbai</a>
							</div>
						</div>
						<div class="dropdown">
							<a href="trends.html">Trends</a>
							<div  class="dropdown-content">
								<a href="engg.html">Engineering</a>
								<a href="science.html">Science</a>
							</div>
						</div>
						<a href="online_courses.html">Online Courses</a>	
						<a href="login.html" class="loginbtn">Login/Signup</a>	
				</ul>
		</nav>
	</header>
<br>


	<center>
	<!-- 	PAGE CONTENT -->
		<div class="txtbox" >
			<input type="button" class="signuplabel" onclick="location.href='signup.html'" value="Sign Up">
			<input type="button" class="signuplabel" onclick="location.href='login.html'" value="Login"><br><br>
			<!-- Login Form --><br>
		    <form action="validate.php" method="post" id="ipform">
		   	    
		  	  	<fieldset>
		  	  		<legend>Professional details</legend>
					<label>Email:*</label><br>
					<input class="inputstyle" autocomplete="off" type="text" id="email" name="email" onkeyup="liveValidateMail();" placeholder="E-Mail ID">
					<br><br>

					<label>Mobile Number:*</label><br>
					<input class="inputstyle" autocomplete="off" type="text" id="mob" name="mob" onkeyup="liveValidateMob();" placeholder="Mobile Number">
					<br><br>

					<label>Your Social link:*</label><br>
					<input class="inputstyle" autocomplete="off" type="text" id="url" name="url"  placeholder="e.g.: www.github/abc" onmouseleave="changeBorder('url');">
					<br><br>
					
					<input type="button" class="resetbtn" id="reset" onclick="var arg = ['email','mob','dept'];resetThis(arg);" name="reset" value="Reset Professional Details">
					<br><br>
					
				</fieldset>
				
		      <input type="submit" class="signupbtn" value="Sign Up">
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