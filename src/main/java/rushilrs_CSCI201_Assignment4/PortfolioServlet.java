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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

@WebServlet("/PortfolioServlet")
public class PortfolioServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        User user = gson.fromJson(request.getParameter("user"), User.class);
        
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

        JsonArray tradesArray = getTrades(user);
        String json = gson.toJson(tradesArray);
        pw.println(json);
        pw.flush();
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	Gson gson = new Gson();
    	String username = request.getParameter("user");
        
		PrintWriter pw = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

        Double balance = getB(username);
        String json = gson.toJson(balance);
        pw.println(json);
        pw.flush();
    }
    
    Double getB(String username) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Double balance = 0.0;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/Assignment4DB?user=root&password=Laker$hah248");
    
            ps = conn.prepareStatement("SELECT user_id FROM Users WHERE username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
    
            if(rs.next()){
                int user_id = rs.getInt("user_id");
                ps = conn.prepareStatement("SELECT * FROM Users WHERE user_id = ?");
                ps.setInt(1, user_id);
                rs = ps.executeQuery();
    
                if(rs.next()) {
                    balance = rs.getDouble("balance");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace(); 
            System.out.println("SQUException in portfolioServelet.");
        } finally {

            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("sqle: " + e.getMessage());
            }
        }
        return Math.round(balance * 100.0) / 100.0;                
    }
    
    

    public JsonArray getTrades(User user) throws IOException{
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        JsonArray tradesArray = new JsonArray();
        try {
        	conn = DriverManager.getConnection("jdbc:mysql://localhost/Assignment4DB?user=root&password=Laker$hah248");

            ps = conn.prepareStatement("SELECT user_id FROM Users WHERE username = ?");
            ps.setString(1, user.getUsername());
            rs = ps.executeQuery();

          //“how can i keep track of the total amount of quantity for a given user in my database. Users are identified by user_id (the foreign key)” prompt (2 lines).
		  //ChatGPT, 4 Sep. version, OpenAI, 20 Apr. 2024, chat.openai.com/chat.
            Map<String, Integer> totalQuantityMap = new HashMap<>();
            Map<String, Double> totalCostMap = new HashMap<>();

            if(rs.next()){
                int user_id = rs.getInt("user_id");
                ps = conn.prepareStatement("SELECT * FROM Portfolio WHERE user_id = ?");
                ps.setInt(1, user_id);
                rs = ps.executeQuery();

                while (rs.next()) {
                    String symbol = rs.getString("ticker");
                    int quantity = rs.getInt("numStock");
                    double purchasePrice = rs.getDouble("price");
                    int existingQuantity = totalQuantityMap.getOrDefault(symbol, 0);
                    double existingTotalCost = totalCostMap.getOrDefault(symbol, 0.0);
                    totalQuantityMap.put(symbol, existingQuantity + quantity);
                    totalCostMap.put(symbol, existingTotalCost + (quantity * purchasePrice));
                }
                
            }

          //“how can i keep track of the total amount of quantity for a given user in my database. Users are identified by user_id (the foreign key)” prompt (13 lines).
  		  //ChatGPT, 4 Sep. version, OpenAI, 20 Apr. 2024, chat.openai.com/chat.
            for (Map.Entry<String, Integer> entry : totalQuantityMap.entrySet()) {
                String symbol = entry.getKey();
                int totalQuantity = entry.getValue();
                if(totalQuantity == 0) {
                	continue;
                }
                double totalCost = totalCostMap.get(symbol);
                double averageCostPerShare = totalCost / totalQuantity;
                JsonObject tradeObject = new JsonObject();
                tradeObject.addProperty("symbol", symbol);
                tradeObject.addProperty("quantity", totalQuantity);
                tradeObject.addProperty("total_cost", Math.round(totalCost * 100.0) / 100.0);
                tradeObject.addProperty("average_cost_per_share", Math.round(averageCostPerShare * 100.0) / 100.0);

                FinnHubDescription finnHubDescription = fetchFinnHubDescription(symbol);
                FinnHubObject finnHubObject = fetchFinnhubObject(symbol);

                tradeObject.addProperty("companyName", finnHubDescription.getName());
                tradeObject.addProperty("marketValue", Math.round((finnHubObject.getC() * totalQuantity) * 100.0) / 100.0);
                tradeObject.addProperty("currPrice", Math.round(finnHubObject.getC() * 100.0) / 100.0);
                tradeObject.addProperty("change", Math.round(finnHubObject.getD() * 100.0) / 100.0);
                tradesArray.add(tradeObject);
            }

            
        }
        catch (SQLException e) {
            e.printStackTrace(); 
            System.out.println("SQUException in portfolioServelet.");
        } finally {

            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("sqle: " + e.getMessage());
            }
        }
        return tradesArray;
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