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
                <a href="./categories.php" class="navitem">Categories</a>
                <a href="./profiles.php" class="navitem active">Profiles</a>
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
                    <img src="./media/cs.jpeg" alt="interview_experience"/>
                </div>
                <div class="card-body">
                    <div class="card-title">
                        <strong>Computer Science</strong>
                    </div>
                    <div class="card-content">
                        Computer Science is the study of computers and computational systems. Unlike electrical and computer engineers, computer scientists deal mostly with software and software systems; this includes their theory, design, development, and application.
                        <br><br>
                        Principal areas of work within Computer Science include artificial intelligence, computer systems and networks, security, database systems, human computer interaction, vision and graphics, numerical analysis, programming languages, software engineering, bioinformatics and theory of computing. 
                    </div>
                </div>
                <a class="link" href="./experience.php">Lets Begin by reading computer science job interview experiences!</a>
            </div>

            <div class="card card-lg">
                <div class="card-image">
                    <img src="./media/finance.jpeg" alt="interview_experience"/>
                </div>
                <div class="card-body">
                    <div class="card-title">
                        <strong>Finance</strong>
                    </div>
                    <div class="card-content">
                        The role of the Finance Officer involves providing financial and administrative support to colleagues, clients and stakeholders of the business. It’s a role that may attract applicants keen to move up the financial corporate ladder; those with ambitions of being Finance Managers, or even the CFO one day.
                        <br><br>
                        A Finance Officer role is well suited to candidates with university qualifications, and this should be detailed in the Finance Officer job description. The most relevant fields of study for this role include:
                        <ul>
                            <li>Finance or Economics</li> 
                            <li>Accounting</li> 
                            <li>Business or Business Administration</li> 
                            <li>Mathematics</li> 
                        </ul>
                    </div>
                </div>
                <a class="link" href="./experience.php">Lets Begin by reading finance job interview experiences!</a>
            </div>

            <div class="card card-lg">
                <div class="card-image">
                    <img src="./media/marketing.jpeg" alt="interview_experience"/>
                </div>
                <div class="card-body">
                    <div class="card-title">
                        <strong>Marketing</strong>
                    </div>
                    <div class="card-content">
                        Marketing executives develop and oversee marketing campaigns to promote products and services. The role of a marketing executive can encompass creative, analytical, digital, commercial and administrative responsibilities. The details of the role will vary depending on the type and size of employer, as well as the industry. Executives are likely to work closely with other employees in areas such as advertising, market research, production, sales and distribution. <br><br>
                        Marketing executives oversee many aspects of a campaign throughout the entire lifespan of a product, service or idea. As such executives are likely to have a great deal of responsibility early on and will be required to manage their time and duties themselves.
                    </div>
                </div>
                <a class="link" href="./experience.php">Lets Begin by reading marketing job interview experiences!</a>
            </div>
            
        </div>

    </body>
</html>
