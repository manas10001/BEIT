<jsp>
    <head>
        <title>IED</title>
        <link rel="stylesheet" href="./style.css">
    </head>

    <body>
        <div class="navbar">
            <ul class="navmenu">
                <a href="./index.jsp" class="navitem active">Home</a>
                <a href="./interview_tips.jsp" class="navitem">Interview Tips</a>
                <a href="./categories.jsp" class="navitem">Categories</a>
                <a href="./profiles.jsp" class="navitem">Profiles</a>
                <%
                    String uname = null;
                    try{
                            uname = (String) session.getAttribute("name");
                            if(uname == null){
                            %>
                                <a href="./login.jsp" class="navitem">Login/Register</a>
                            <%
                            }else{
                                uname = "";
                            %>
                                <a href="./logout.jsp" class="navitem">Logout</a>
                            <%}
                    }catch(Exception e){
                        System.out.println(""+e.toString());
                    }
                %>
            </ul>    
        </div>

        <div class="content">
            <div class="card card-xlg">
                <div class="card-body">
                    <div class="card-title" style="text-align: center;">
                        <strong>Java Developer Interview Experience!</strong>
                    </div>
                    <hr>
                    <div class="card-content">
                        <strong>Job Title:</strong> Java Developer<br>
                        <strong>Company:</strong> Oracle<br>
                        <strong>Posted On:</strong> 01 JAN 2021<br>
                        <strong>Posted By:</strong> ABC<br>
                        <strong>Result:</strong> Selected<br><br>
                        <strong>Application Process:</strong>
                            <p>
                                I have applied in oracle careers page 2 years ago when I was a fresher. 
                                <br>
                                All of sudden HR calls me and tells that your profile is selected for one of our position in Oracle CGBU (Communications Global Business Unit).
                                <br>
                                She asked tell me about yourself and how much your expecting (She was filling info to send it desired managers I think).
                                <br>
                                They said there will be three rounds. First two rounds will be technical and third will be managerial round.
                                <br>
                                Meeting was on zoom link was send by WhatsApp.
                            </p>
                        <strong>Round 1:</strong>
                            <p>
                                (Technical Interview about 50min): Interviewer was friendly he first introduced himself that he had an experience of 5 years, 3years in some other company and 2 years in ORACLE OSS.  
                                <br>
                                He started with “Introduce yourself and how you landed to current company?” 
                                <br>
                                All upcoming questions were on experienced based in my previous jobs.
                                <br>
                                Most questions were on java, cpp, relational database, system design, oop.
                            </p>
                        <strong>Round 2:</strong>
                            <p>
                                (HR Round 30min): Common HR questions and discussion regarding salary expectations.
                            </p>
                        <strong>Preperation Tips:</strong>
                            <p>
                                Though I was 2 years experience I was not sure what would be asked they asked which technology you prefer Java or C++.
                                <br>
                                I opted for Java as I was working in java in my current company.
                            </p>
                    </div>
                </div>
            </div>
        </div>
    </body>
</jsp>