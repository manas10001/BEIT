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
			<a href="./home.php" class="navitem">Home</a>                	
                <?}else{?>
                	<a href="./index.php" class="navitem">Home</a>
                <?}?>
                <a href="./interview_tips.php" class="navitem">Interview Tips</a>
                <a href="./categories.php" class="navitem active">Categories</a>
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
                    <img src="./media/jobs.jpeg" alt="interview_experience"/>
                </div>
                <div class="card-body">
                    <div class="card-title">
                        <strong>Jobs</strong>
                    </div>
                    <div class="card-content">
                        A Job interview, may you be a fresher interviewing for the first job of your life or an experienced candidate lokking for a better opportunity reconnaissance always comes in handy.<br>
                        Explore interviews of candidates who interviewed for the same job postion or for the same company in past maybe learn a thing or two. All the best.
                    </div>
                </div>
                <a class="link" href="./expList.php?type=jb">Lets Begin by reading job interview experiences!</a>
            </div>

            <div class="card card-lg">
                <div class="card-image">
                    <img src="./media/internship.jpeg" alt="interview_experience"/>
                </div>
                <div class="card-body">
                    <div class="card-title">
                        <strong>Internships</strong>
                    </div>
                    <div class="card-content">
                        Internships, they are one of the most key things towards successfull careers, they can turn into a full time oportuity easily, if an intern performs good in an internship its highly likely that the companies offer them a full time position.<br>
                        Explore interviews of candidates who interviewed for the same internship or for the same company in past maybe learn a thing or two. All the best.
                    </div>
                </div>
                <a class="link" href="./expList.php?type=is">Lets Begin by reading internship interview experiences!</a>
            </div>

            <div class="card card-lg">
                <div class="card-image">
                    <img src="./media/parttime.png" alt="interview_experience"/>
                </div>
                <div class="card-body">
                    <div class="card-title">
                        <strong>Part Time Jobs</strong>
                    </div>
                    <div class="card-content">
                        Part time jobs just like internships they can turn into a full time oportuity easily, its always great to hunt for part time jobs while studing or whenever one has time.<br>
                        Explore interviews of candidates who interviewed for the same job or for the same company in past maybe learn a thing or two. All the best.
                    </div>
                </div>
                <a class="link" href="./expList.php?type=pt">Lets Begin by reading part-time job interview experiences!</a>
            </div>
            
        </div>

    </body>
</html>
