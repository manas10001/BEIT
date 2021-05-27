<?php
	session_start();
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
                	<a href="./addExperience.php" class="navitem">Add Experience</a>                	
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
                        IED aka Interview Experience Drive is yor one stop solution to get inside information on your next interview. <br>
                        Before going to your next interview it is always recommended to read interview experiences of past candates for same role they give you useful insights to the companies recruitment process as well as tell you what topics to focus on. <br>
                        The interview experience might be for the same role or for maybe a different role in the same domain but it will still give you pointers on your upcoming interview.<br>
                        The goal of IED is to allow candidates to share their own interview experiences as well as to learn from others.
                    </div>
                </div>
                <a class="link" href="./interview_tips.php">Lets Begin by reading some interview tips!</a>

            </div>
        </div>
    </body>
</html>
