<jsp>
    <head>
        <title>IED</title>
        <link rel="stylesheet" href="./style.css">
    </head>

    <body>
        <div class="navbar">
            <ul class="navmenu">
                <a href="./index.jsp" class="navitem">Home</a>
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
            <div class="card card-md">
                <div class="card-body">
                    <div class="card-title">
                        <strong>Register</strong>
                    </div>
                    <div class="forms">
                        <form action="RegisterHandler" method="POST">
                            <fieldset id="fs1">
                                <legend>Account Details</legend>
                            <label class="labels">Username:*</label><br>
                            <input class="inputs" type="text" id="login" name="login" autocomplete="off" placeholder="Username">
                                
                            <label class="labels">Email ID:*</label><br>
                            <input class="inputs" type="email" id="mail" name="mail" placeholder="abc@h1.com">

                            <label class="labels">Password:*</label><br>
                            <input class="inputs" type="password" id="password" name="pass" placeholder="Password">
                            
                            <label class="labels">Retype Password:*</label><br>
                            <input class="inputs" type="password" id="repassword" name="repass" placeholder="Retype Password">
                            
                             </fieldset>
                         <br><br>
                         <input type="submit" class="logbtn" value="Register">
                         
                       </form>
                    </div>
                </div>
                <a class="link" href="./login.jsp">Already Registered? Login!</a>
            </div>
        </div>
        <script type="text/javascript" src="./jquery.min.js"></script>
        <script type="text/javascript" src="./validator.js"></script>
        <script>
            $(document).ready(function(){
                $('#login').keyup(function(){
                    let data = $(this).val();
                    var reg=/^[A-Za-z][A-Za-z\s]*$/;
                    
                    if(data.length == 0)
                        $(this).css("border", "1px solid red");
                    else{
                        $(this).css("border", "1px solid green");
                        
                        if(reg.test(data)==true)
                            $(this).css("border", "1px solid green");
                        else
                            $(this).css("border", "1px solid red");
                    }
                });

                $('#password, #repassword').keyup(function(){
                    let data = $(this).val();
                    if(data.length < 8)
                        $(this).css("border", "1px solid red");
                    else
                        $(this).css("border", "1px solid green");
                });

                $('#mail').keyup(function(){
                    let data = $(this).val();
                    var reg = /^([A-Za-z0-9_\-\.])+@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
                    if(reg.test(data) == true)
                        $(this).css("border", "1px solid green");
                    else
                        $(this).css("border", "1px solid red");
                });
            });
        </script>
    </body>
</jsp>