<%-- 
    Document   : index
    Created on : 20 Sep, 2019, 9:17:09 AM
    Author     : pict2
--%>

<%@page import="java.sql.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>SCHOOL WALA</title>
	<link rel="stylesheet" type="text/css" href="./css/common.css">
</head>
<body>
	<!-- there goes navbar -->
<br>
		<div class="txtbox" style="width:87vw">
                    <h1 style="color:green;text-align:center">YOU ARE SUCCESSFULLY LOGGED IN!</h1>
                    
	
                    <%
                        
                        try{
                            String uname = session.getAttribute("name").toString();
                            String mobile = session.getAttribute("mobile").toString();
                            String email = session.getAttribute("email").toString();
                            if(uname.isEmpty())
                            {
                                out.println("<script>alert('You Should login first!');</script>");
                                out.println("<script>window.location.href = 'index.jsp'</script>");
                            }else{
                                %>
                                <h3>USERNAME: <%=uname%>
                                    <br><br>MOBILE NUMBER: <%=mobile%>
                                    <br><br>EMAIL: <%=email%></h3>
                                <%
                            }
                        }catch(Exception e){
                            System.out.println("Exception on dashboard "+e.toString());
                        }
                    %>
                    <button class="button" onclick="window.location.href = 'index.jsp';">LOGOUT</button>
                </div>



</body>
</html>
