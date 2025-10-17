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

@WebServlet("/TradeServlet2")
public class TradeServlet2 extends HttpServlet{
    private static final long serialVersionUID = 1L;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String ticker = request.getParameter("ticker");
		String transactionType = request.getParameter("transactionType");
		int quantity = 0;
		
        Gson gson = new Gson();
        User user = gson.fromJson(request.getParameter("user"), User.class);
        
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		try {
			quantity = Integer.parseInt(request.getParameter("quantity"));
		}
		catch(NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println("entered error2");
			String error = "Failed: Purchase not possible";
			pw.write(gson.toJson(error));
			pw.flush();
			return;
		}

        FinnHubDescription finnHubDescription = fetchFinnHubDescription(ticker);
        FinnHubObject finnHubObject = fetchFinnhubObject(ticker);

        Float price = finnHubObject.getC();
		
		if(transactionType.equals("buy")) {
			System.out.println(user.getBalance());
			System.out.println(price*quantity);
	        if(quantity <= 0 || request.getParameter("quantity").isEmpty() || user.getBalance() < price * quantity){
	        	System.out.println("entered error");
	            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				String error = "Failed: Purchase not possible";
				pw.write(gson.toJson(error));
				pw.flush();
				return;
	        }
	        else{
	            registerTrade(user, ticker, price, quantity);
	            response.setStatus(HttpServletResponse.SC_OK);
	            String success = "Bought " + quantity + " shares of " + ticker + " for " +  (Math.round((price*quantity) * 100.0) / 100.0);
	            pw.write(gson.toJson(success));
	            pw.flush();
	        }
		}
        else{
            int getQuantityOfTicker = getQuantityOfTicker(user, ticker);
            if(quantity <= 0 || request.getParameter("quantity").isEmpty() || getQuantityOfTicker < quantity){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                String error = "Failed: Purchase not possible";
                pw.write(gson.toJson(error));
                pw.flush();
            }
            else{
                registerTrade(user, ticker, price, -1*quantity);
                response.setStatus(HttpServletResponse.SC_OK);
                String success = "Sold " + -1*quantity + " shares of " + ticker;
                pw.write(gson.toJson(success));
                pw.flush();
            }
        }
    }

    int getQuantityOfTicker(User user, String ticker){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        
        int totalNumStock = 0;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/Assignment4DB?user=root&password=Laker$hah248");

            ps = conn.prepareStatement("SELECT user_id FROM Users WHERE username = ?");
            ps.setString(1, user.getUsername());
            rs = ps.executeQuery();

            if (rs.next()) {
                int user_id = rs.getInt("user_id");

                ps = conn.prepareStatement("SELECT numStock FROM Portfolio WHERE user_id = ? AND ticker = ?");
                ps.setInt(1, user_id);
                ps.setString(2, ticker);
                rs = ps.executeQuery();

                while (rs.next()) {
                    totalNumStock += rs.getInt("numStock");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
            System.out.println("SQUException in tradeServlet2.");
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("sqle: " + e.getMessage());
            }
        }
        
        return totalNumStock;
    }


    void registerTrade(User user, String ticker, Float price, int quantity) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/Assignment4DB?user=root&password=Laker$hah248");

            // Get user_id from the username
            ps = conn.prepareStatement("SELECT user_id FROM Users WHERE username = ?");
            ps.setString(1, user.getUsername());
            rs = ps.executeQuery();

            if (rs.next()) {
                int user_id = rs.getInt("user_id");
                
                
                ps = conn.prepareStatement("INSERT INTO Portfolio (user_id, ticker, numStock, price) VALUES (?, ?, ?, ?)");
                ps.setInt(1, user_id);
                ps.setString(2, ticker);
                ps.setInt(3, quantity);
                ps.setDouble(4, price);
                ps.executeUpdate();
                    

                ps = conn.prepareStatement("UPDATE Users SET balance = balance - ? WHERE user_id = ?");
                ps.setDouble(1, price * quantity);
                ps.setInt(2, user_id);
                ps.executeUpdate();
            }
           
        } catch (SQLException e) {
            e.printStackTrace(); 
            System.out.println("SQUException in tradeServlet2.");
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
    
    
    public static FinnHubDescription fetchFinnHubDescription(String ticker) throws IOException {
		String apiKey = "cnsvcf9r01qi1jjgedggcnsvcf9r01qi1jjgedh0";
		String urlTicker = "https://finnhub.io/api/v1/stock/profile2?symbol=" + ticker + "&token=" + apiKey;
		StringBuilder response = new StringBuilder();
		
		//logic came from working with urls lecture
				try {
					URL myURL = new URL(urlTicker);
					
					HttpURLConnection connection = (HttpURLConnection) myURL.openConnection();
					connection.setRequestMethod("GET");
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					
		            String inputLine;
		            while ((inputLine = in.readLine()) != null) {
		                response.append(inputLine);
		            }
		            in.close();
				}
				catch(MalformedURLException e) {
					 System.out.println(e);
				}
				
				Gson gson = new Gson();
		        FinnHubDescription finnHubDescription = gson.fromJson(response.toString(), FinnHubDescription.class);
		        return finnHubDescription;
	}
	
	
	public static FinnHubObject fetchFinnhubObject(String ticker) throws IOException {
		String apiKey = "cnsvcf9r01qi1jjgedggcnsvcf9r01qi1jjgedh0";
		String urlTicker = "https://finnhub.io/api/v1/quote?symbol=" + ticker + "&token=" + apiKey;
		StringBuilder response = new StringBuilder();
		
		//logic came from working with urls lecture
		try {
			URL myURL = new URL(urlTicker);
			
			HttpURLConnection connection = (HttpURLConnection) myURL.openConnection();
			connection.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
		}
		catch(MalformedURLException e) {
			 System.out.println(e);
		}
		
		Gson gson = new Gson();
        FinnHubObject finnHubObject = gson.fromJson(response.toString(), FinnHubObject.class);
        return finnHubObject;
	}
}