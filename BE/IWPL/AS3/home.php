<?php
	session_start();
	if (!isset($_SESSION['username']) || empty($_SESSION['username'])){
		header("Location: ./loginpage.php");
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
			<a href="./home.php" class="navitem active">Home</a>                	
                <?}else{?>
                	<a href="./index.php" class="navitem active">Home</a>
                <?}?>
                <a href="./interview_tips.php" class="navitem">Interview Tips</a>
                <a href="./categories.php" class="navitem">Categories</a>
                <a href="./profiles.php" class="navitem">Profiles</a>
                <?php if (isset($_SESSION['username'])&& !empty($_SESSION['username'])) {?>
			<a href="./logout.php" class="navitem">Logout</a>                	
                <?}else{?>
                	<a href="./loginpage.php" class="navitem">Login/Register</a>
                <?}?>
            </ul>    
        </div>

        <div class="content">
            <div class="card card-lg">
                <div class="card-image">
                    <img src="./media/interview_experience.png" alt="interview_experience"/>
                </div>
                <div class="card-body">
                    <div class="card-title">
                        <strong>Welcome to IED</strong>
                    </div>
                    <div class="card-content">
                        Login Success!
                    </div>
                </div>
                <a class="link" href="./interview_tips.php">Lets Begin by reading some interview tips!</a>

            </div>
        </div>
    </body>
</html>
