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
                        <strong>Reset Password</strong>
                    </div>
                    <div class="forms">
                        <form action="#" method="post">
                            <fieldset id="fs1">
                                <legend>Account Details</legend>
                                <label>Email ID:*</label><br>
                                <input type="text" id="login" name="login" onkeyup="liveValidateBlank('login'); validateEmail('login');" placeholder="Email-ID">
                            </fieldset>
                         <br><br>
                         <input type="submit" class="logbtn" value="Get OTP">
                       </form>
                    </div>
                </div>
                <a class="link" href="./registerpage.php">Not a member? Register!</a>
            </div>
        </div>
        
        <?php
		if(isset($_POST) && !empty($_POST)){
			$otp = mt_rand(100000, 999999);
			$email=$_POST['login'];
			
			require 'dbconn.php';
			
			//validate email it should be in db
			
			$query="SELECT * FROM users WHERE email='$email'";
			$result=mysqli_query($con,$query);

			$cnt=mysqli_num_rows($result);

			echo " err: ".$cnt;//mysqli_error($con);

			if($cnt >= 1){
			
				$headers = 'From: mnpatil155137@gmail.com';
				$fullText = "Hello user, Use this OTP to reset your password for IED: ".$otp;
				if(mail($email, "Reset Password", $fullText, $headers)){
					//store otp in db
			
					$res = mysqli_query($con,"insert into otp(email, otp) values('$email','$otp')");
		
					if($res){
						//redirct 
						header("Location: ./changePass.php?email=".$email);
					} else {
						echo "<script>alert('Coudnt send otp to email try again!!!');</script>";
				
					}
				}else{ 
					echo "<script>alert('Mail not sent something went wrong!')</script>";			
				
				}	
			}else{
				echo "<script>alert('No such user account in system!')</script>";
				//echo "er3";
			}
		}
	?>

        
        <script type="text/javascript" src="./validator.js"></script>
    </body>
</html>
