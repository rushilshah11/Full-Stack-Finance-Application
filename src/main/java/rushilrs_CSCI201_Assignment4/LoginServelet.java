package rushilrs_CSCI201_Assignment4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;


@WebServlet("/LoginServelet")
public class LoginServelet extends HttpServlet{
    private static final long serialVersionUID = 1L;
	
    //based off the logic from lab
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		Gson gson = new Gson();
		
		if(username == null || username.isBlank() || password == null || password.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "User info missing";
			pw.write(gson.toJson(error));
			pw.flush();
		}
		
		
		User currUser = getUser(username, password);
		
		if(currUser == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "Username and/or password not found.";
			pw.write(gson.toJson(error));
			pw.flush();
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			pw.write(gson.toJson(currUser));
			pw.flush();
		}
		
	}
	
	public static User getUser(String username, String password) {
		try {
    		Class.forName("com.mysql.cj.jdbc.Driver");
    	}
    	catch(ClassNotFoundException e) {
    		e.printStackTrace();
    	}
    	
    	Connection conn = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	User currUser = null;
    	
    	try {
    		conn = DriverManager.getConnection("jdbc:mysql://localhost/Assignment4DB?user=root&password=Laker$hah248");
    		ps = conn.prepareStatement("SELECT * FROM Users WHERE username=? AND password=?");
    		ps.setString(1, username);
            ps.setString(2, password);
    		rs = ps.executeQuery();
    		
    		if (rs.next()) {
                // User found, create User object
                
                String uname = rs.getString("username");
                String pass = rs.getString("password");
                String email = rs.getString("email");
                Double balance = rs.getDouble("balance");
                // Populate other fields as needed
                currUser = new User(uname, pass, email, balance);
            } else {
                // No user found
                currUser = null;
            }
    		
    	}
    	catch(SQLException sqle) {
    		System.out.println("SQUException in getUser.");
    		sqle.printStackTrace();
    	}
    	finally {
    		 try {
                 if (rs != null) rs.close();
                 if (ps != null) ps.close();
                 if (conn != null) conn.close();
             } 
    		 catch (SQLException sqle) {
                 System.out.println("sqle: " + sqle.getMessage());
             }
    	}
    	
    	return currUser;
	}
}
