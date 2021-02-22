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
                        <strong>Register</strong>
                    </div>
                    <div class="forms">
                        <form action="register.php" method="post" id="registerForm">
                            <fieldset id="fs1">
                                <legend>Account Details</legend>
                            <label>Uesrname:*</label><br>
                            <input type="text" id="login" name="login" autocomplete="off" onkeyup="liveValidateBlank('login'); liveValidateStr('login');" placeholder="Username">
                                
                            <label>Email ID:*</label><br>
                            <input type="email" id="mail" name="mail" onkeyup="liveValidateBlank('mail'); validateEmail('mail');" placeholder="abc@h1.com">

                            <label>Password:*</label><br>
                            <input type="password" id="password" name="password" onkeyup="liveValidateBlank('password'); lengthValidate('password',8);" placeholder="Password">
                            
                            <label>Retype Password:*</label><br>
                            <input type="password" id="repassword" name="repassword" onkeyup="liveValidateBlank('repassword'); lengthValidate('repassword',8)" placeholder="Retype Password">
                            
                             </fieldset>
                         <br><br>
                         <Button type="button" class="logbtn" onclick="return validateRegisterForm();">Register</Button>
                       </form>
                    </div>
                </div>
                <a class="link" href="./loginpage.php">Already Registered? Login!</a>
            </div>
        </div>
        <script type="text/javascript" src="./validator.js"></script>
    </body>
</html>
