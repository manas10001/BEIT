/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author pict2
 */
public class Login extends HttpServlet {


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        processRequest(request, response);
        
        PrintWriter out = response.getWriter();
        String username=null,password=null;
        
        try{
            //CREATING SESSION
            HttpSession session = request.getSession();
            //connecting to databse
            Class.forName("com.mysql.jdbc.Driver"); 
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loginsystem","root",""); 
            
            username = request.getParameter("uname");
            password = request.getParameter("pass");
            
            Statement stmt = con.createStatement();
            String sql = "select * from users where email='"+username+"' and password='"+password+"'";
            ResultSet rs = stmt.executeQuery(sql);
            
            if(rs.next()){
                out.println("<script>alert('Login success!');</script>");
                out.println("<script>window.location.href = 'dashboard.jsp'</script>");
                
            }
            else{
                out.println("<script>alert('Login failed!');</script>");
                out.println("<script>window.location.href = 'index.jsp'</script>");
            }
        }catch(Exception e){
            System.out.println("Exception on login: "+e.toString());
        }finally{
            out.close();
        }
        
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet for login";
    }// </editor-fold>

}
