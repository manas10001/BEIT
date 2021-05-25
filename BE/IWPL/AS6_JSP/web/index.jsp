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
                    String uname = "";
                    try{
                            uname = (String) session.getAttribute("name");
                            if(uname == null){
                                uname = "";
                            %>
                                <a href="./login.jsp" class="navitem">Login/Register</a>
                            <%
                            }else{
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
            <div class="card card-lg">
                <div class="card-image">
                    <img src="./media/interview_experience.png" alt="interview_experience"/>
                </div>
                <div class="card-body">
                    <div class="card-title">
                        <strong>Welcome to IED <%=uname%></strong>
                    </div>
                    <div class="card-content">
                        IED aka Interview Experience Drive is yor one stop solution to get inside information on your next interview. <br>
                        Before going to your net interview it is always recommended to read interview experiences of past candates for same role they give you useful insights to the companies recruitment process as well as tell you what topics to focus on. <br>
                        The interview experience might be for the same role or for maybe a different role in the same domain but it will still give you pointers on your upcoming interview.<br>
                        The goal of IED is to allow candidates to share their own interview experiences as well as to learn from others.
                    </div>
                </div>
                <a class="link" href="./interview_tips.jsp">Lets Begin by reading some interview tips!</a>

            </div>
        </div>
    </body>
</jsp>