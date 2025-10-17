package rushilrs_CSCI201_Assignment4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;


@WebServlet("/HomeServelet")
public class HomeServelet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String userTicker = request.getParameter("userTicker");
		try {
            FinnHubObject finnHubObject = fetchFinnhubObject(userTicker.toUpperCase());
            FinnHubDescription finnHubDescription = fetchFinnHubDescription(userTicker.toUpperCase());
            response.setContentType("application/json");
            Gson gson = new Gson();
            
            
            String jsonResponse = gson.toJson(finnHubObject);
            String jsonResponse2 = gson.toJson(finnHubDescription);
            JsonObject combinedJson = new JsonObject();
            combinedJson.add("finnHubObject", gson.fromJson(jsonResponse, JsonObject.class));
            combinedJson.add("finnHubDescription", gson.fromJson(jsonResponse2, JsonObject.class));
            
            PrintWriter out = response.getWriter();
            out.print(combinedJson);
            
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error while fetch finnhub data");
        }
		
	}
	
	//same logic as assignment3
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
	
	//same logic as assignment3
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
