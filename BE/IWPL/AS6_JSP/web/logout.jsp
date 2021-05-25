<%-- 
    Document   : logout
    Created on : 24 May, 2021, 11:26:11 PM
    Author     : manas
--%>

<%
//RESETTING ALL SESSION ATTRIBUTES.
    session.setAttribute("name", null);
    session.setAttribute("email", null);
    response.sendRedirect("index.jsp");
%>
