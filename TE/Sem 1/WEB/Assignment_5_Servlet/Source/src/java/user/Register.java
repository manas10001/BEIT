/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

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
public class Register extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
    }
  
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
        
        PrintWriter out = response.getWriter();
        String username=null,password=null,repass=null,email=null,mobile=null;
        
        try{
            //CREATING SESSION
            HttpSession session = request.getSession();
            //connecting to databse
            Class.forName("com.mysql.jdbc.Driver"); 
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/loginsystem","root",""); 
            
            username = request.getParameter("name");
            password = request.getParameter("pass");
            repass = request.getParameter("repass");
            email = request.getParameter("email");
            mobile = request.getParameter("mob");
            
            //PASSWORD AND RETYPE PASSWORD CHECK
            if(!password.equals(repass))
            {
                out.println("<script>alert('Password and retyped password dont watch!');</script>");
                out.println("<script>window.location.href = 'index.jsp'</script>");
            }
            
            Statement stmt = con.createStatement();
            String sql = "insert into users values('"+username+"','"+email+"','"+mobile+"','"+password+"')";
            stmt.executeUpdate(sql);
            
                session.setAttribute("name",username);
                session.setAttribute("email", email);
                session.setAttribute("mobile", mobile);
                out.println("<script>alert('Registration success!');</script>");
                out.println("<script>window.location.href = 'dashboard.jsp'</script>");
             
        }catch(Exception e){
            System.out.println("Exception on register: "+e.toString());
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
        return "Register user servlet";
    }// </editor-fold>

}
