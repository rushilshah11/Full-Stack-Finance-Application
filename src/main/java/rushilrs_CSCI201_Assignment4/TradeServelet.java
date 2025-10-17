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

@WebServlet("/TradeServelet")
public class TradeServelet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ticker = request.getParameter("ticker");
		Double price = Double.parseDouble(request.getParameter("price"));
		int quantity = 0;
		
        Gson gson = new Gson();
        User user = gson.fromJson(request.getParameter("user"), User.class);
        
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		// from office hours, i was told the illegal numberexception happens if quantity is empty and not in a try catch block
		try {
			quantity = Integer.parseInt(request.getParameter("quantity"));
		}
		catch(NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "Failed: Purchase not possible";
			pw.write(gson.toJson(error));
			pw.flush();
			return;
		}


        if(quantity <= 0 || request.getParameter("quantity").isEmpty() || user.getBalance() < price * quantity){
        	System.out.println("entered error");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String error = "Failed: Purchase not possible";
			pw.write(gson.toJson(error));
			pw.flush();
        }
        else{
            registerTrade(user, ticker, price, quantity);
            response.setStatus(HttpServletResponse.SC_OK);
            String success = "Bought " + quantity + " shares of " + ticker + " for " + (Math.round((price*quantity) * 100.0) / 100.0);
            pw.write(gson.toJson(success));
            pw.flush();
        }

    }

    void registerTrade(User user, String ticker, Double price, int quantity) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/Assignment4DB?user=root&password=Laker$hah248");

            ps = conn.prepareStatement("SELECT user_id FROM Users WHERE username = ?");
            ps.setString(1, user.getUsername());
            rs = ps.executeQuery();

            if (rs.next()) {
                int user_id = rs.getInt("user_id");
                

              //“WHat would the sql statement be to Insert a whole object into my database in a preparedStatement” prompt (1 lines).
      		  //ChatGPT, 4 Sep. version, OpenAI, 21 Apr. 2024, chat.openai.com/chat.
                ps = conn.prepareStatement("INSERT INTO Portfolio (user_id, ticker, numStock, price) VALUES (?, ?, ?, ?)");
                ps.setInt(1, user_id);
                ps.setString(2, ticker);
                ps.setInt(3, quantity);
                ps.setDouble(4, price);
                ps.executeUpdate();
                

              //“WHat would the sql statement be to update a variable in my database in a preparedStatement” prompt (1 lines).
              //ChatGPT, 4 Sep. version, OpenAI, 21 Apr. 2024, chat.openai.com/chat.
                ps = conn.prepareStatement("UPDATE Users SET balance = balance - ? WHERE user_id = ?");
                ps.setDouble(1, price * quantity);
                ps.setInt(2, user_id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
            System.out.println("SQUException in tradeServelet.");
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("sqle: " + e.getMessage());
            }
        }
    }
}