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

//logic is from writeup

@WebServlet("/SignupServelet")
public class SignupServelet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String reqEmail = request.getParameter("email");
		String reqUsername2 = request.getParameter("username2");
		String reqPassword2 = request.getParameter("password2");
		
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
	
		User currUser = new User(reqUsername2, reqPassword2, reqEmail, 50000.0);
		
		String username = currUser.getUsername();
		String password = currUser.getPassword();
		String email = currUser.getEmail();
		Double balance = currUser.getBalance();
		
		Gson gson = new Gson();
		
		if(username == null || username.isBlank() || password == null || password.isBlank() || email == null || email.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "User info missing";
			pw.write(gson.toJson(error));
			pw.flush();
		}
		
		int userID = registerUser(username, password, email);
		
		if(userID == -1) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "Username is taken";
			pw.write(gson.toJson(error));
			pw.flush();
		}
		else if(userID == -2) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "Email is already registered";
			pw.write(gson.toJson(error));
			pw.flush();
		}
		else {
			response.setStatus(HttpServletResponse.SC_OK);
			pw.write(gson.toJson(currUser));
			pw.flush();
		}
	}
    
    public static int registerUser(String username, String password, String email) {
    	try {
    		Class.forName("com.mysql.cj.jdbc.Driver");
    	}
    	catch(ClassNotFoundException e) {
    		e.printStackTrace();
    	}
    	
    	Connection conn = null;
    	Statement st = null;
    	ResultSet rs = null;
    	
    	int userID = -1;
    	
    	try {
    		conn = DriverManager.getConnection("jdbc:mysql://localhost/Assignment4DB?user=root&password=Laker$hah248");
    		
    		 st = conn.createStatement();
             rs = st.executeQuery("SELECT * FROM Users WHERE username='" + username + "'");
             
             if(!rs.next()) {
            	 st = conn.createStatement();
            	 rs = st.executeQuery("SELECT * FROM Users WHERE email='" + email + "'");
            	 if(!rs.next()) {
            		 rs.close();
            		 st.execute("INSERT INTO Users (username, password, email, balance) VALUES ('" + username + "', '" + password + "', '" + email + "', 50000)");
            		 rs = st.executeQuery("SELECT LAST_INSERT_ID()");
            		 rs.next();
            		 userID = rs.getInt(1);
            	 }
            	 else {
            		 userID = -2;
            	 }
             }
    	}
    	catch(SQLException sqle) {
    		System.out.println("SQUException in registerUser.");
    		sqle.printStackTrace();
    	}
    	finally {
    		 try {
                 if (rs != null) rs.close();
                 if (st != null) st.close();
                 if (conn != null) conn.close();
             } 
    		 catch (SQLException sqle) {
                 System.out.println("sqle: " + sqle.getMessage());
             }
    	}
    	
    	return userID;
    }
    
}
