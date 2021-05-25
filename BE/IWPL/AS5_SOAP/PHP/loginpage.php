<?php
	session_start();
	if (isset($_SESSION['username']) && !empty($_SESSION['username'])){
		header("Location: ./home.php");
	}
?>
<html>
    <head>
        <title>IED</title>
        <link rel="stylesheet" href="./style.css">
    </head>

    <body>
        <div class="navbar">
            <ul class="navmenu">
                <?php if (isset($_SESSION['username'])&& !empty($_SESSION['username'])) {?>
			<a href="./home.php" class="navitem">Home</a>                	
                <?}else{?>
                	<a href="./index.php" class="navitem">Home</a>
                <?}?>
                <a href="./interview_tips.php" class="navitem">Interview Tips</a>
                <a href="./categories.php" class="navitem">Categories</a>
                <a href="./profiles.php" class="navitem">Profiles</a>
                <?php if (isset($_SESSION['username'])&& !empty($_SESSION['username'])) {?>
			<a href="./logout.php" class="navitem">Logout</a>                	
                <?}else{?>
                	<a href="./loginpage.php" class="navitem active">Login/Register</a>
                <?}?>
            </ul>    
        </div>
        <div class="content">
            <div class="card card-md">
                <div class="card-body">
                    <div class="card-title">
                        <strong>Login</strong>
                    </div>
                    <div class="forms">
                        <form action="login.php" method="post" id="loginForm">
                            <fieldset id="fs1">
                                <legend>Account Details</legend>
                                <label>Login ID:*</label><br>
                                <input type="text" id="login" name="login" onkeyup="liveValidateBlank('login'); liveValidateStr('login');" placeholder="Login-ID">
                                
                                <label>Password:*</label><br>
                                <input type="password" id="password" name="password" onkeyup="liveValidateBlank('password'); lengthValidate('password',8);" placeholder="Password">

                            </fieldset>
                         <br><br>
                         <input type="submit" class="logbtn" value="Log In">
                         <!--<Button type="button" class="logbtn" onclick="return validateLoginForm();">Log In</Button><br>-->
                         
                       </form>
                    </div>
                </div>
                
                <a class="link" href="./resetpassword.php" style="float:left; margin-left:20">Reset Password!</a>
                <a class="link" href="./registerpage.php" style="float:right; margin-right:20">Not a member? Register!</a>
            </div>
        </div>
        <script type="text/javascript" src="./validator.js"></script>
    </body>
</html>
