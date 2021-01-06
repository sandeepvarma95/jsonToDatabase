package TvDB;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.sql.Connection;
//import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class tvJSONtoDB {
	public static Connection ConnectToDB() throws Exception {

		try {
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/moviedb";
			String username = "root";
			String password = "root";
			Class.forName(driver);

			Connection conn = DriverManager.getConnection(url, username, password);
			System.out.println("Connected");
			return conn;
		} catch (Exception e) {
			System.out.println(e);
		}

		return null;
	}

	public static void main(String args[]) {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date today = Calendar.getInstance().getTime(); 
		
		try {
			String pageFinderURL = "https://api.themoviedb.org/3/tv/popular?api_key=e4b3e22ec2b42c496bb2127725b996ad";
			URL urlpage = new URL(pageFinderURL);
			URLConnection requestPage = urlpage.openConnection();
			requestPage.connect();
			JsonParser jpPage = new JsonParser(); // from gson
			JsonElement rootPage = jpPage.parse(new InputStreamReader((InputStream) requestPage.getContent()));
			JsonObject rootobjPage = rootPage.getAsJsonObject();
			String total_pages = rootobjPage.get("total_pages").getAsString();
			System.out.println(total_pages);
			int total=Integer.parseInt(total_pages); 
			
			
			for(int j=447;j<=total;j++)
			{
			int z = j;
			Integer y = new Integer(z); 
			String concater = y.toString();
			System.out.println("Page number:"+concater);
			String lURL = "https://api.themoviedb.org/3/tv/popular?api_key=e4b3e22ec2b42c496bb2127725b996ad&page="; // just
			
			String sURL = lURL.concat(concater);
			//System.out.println(sURL);
			
			// Connect to the URL using java's native library
			URL url = new URL(sURL);
			URLConnection request = url.openConnection();
			request.connect();
			JsonParser jp = new JsonParser(); // from gson
			JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); // Convert
																											// element
			JsonObject rootobj = root.getAsJsonObject(); // May be an array, may
															// be an object.
			JsonArray jarray = rootobj.getAsJsonArray("results"); // just grab
																	// the
																	// zipcode
			//System.out.println(jarray);
			Connection con = ConnectToDB();
			 PreparedStatement pstmt = con.prepareStatement("INSERT INTO tvdatabase values (?,?,?,?,?,?,?,?,?,?)");

			for (int i = 0; i < jarray.size(); i++) {
				rootobj = jarray.get(i).getAsJsonObject();
				String id = rootobj.get("id").toString();
				String nameQuotes = rootobj.get("name").toString();
				String name = nameQuotes.replace("\"", "");
				String overview_Quotes = rootobj.get("overview").toString();
				String overview = overview_Quotes.replace("\"", "");
				String popularity = rootobj.get("popularity").toString();
				String first_air_date_Quotes = rootobj.get("first_air_date").toString();
				String first_air_date = first_air_date_Quotes.replace("\"", "");
				String vote_average = rootobj.get("vote_average").toString();
				String vote_count = rootobj.get("vote_count").toString();
				String original_language_Quotes = rootobj.get("original_language").toString();
				String original_language = original_language_Quotes.replace("\"", "");
				String poster1 = "https://image.tmdb.org/t/p/w185";
				String poster2 = rootobj.get("poster_path").toString();
				String poster3 = poster2.replace("\"", "");
				String poster = poster1.concat(poster3);
				String createdDate = sdf.format(today);
				
				//System.out.println("ID: "+id+"\t"+"Title: "+title);
				pstmt.setString(1, id);
				pstmt.setString(2, name);
				pstmt.setString(3, overview);
				pstmt.setString(4, popularity);
				pstmt.setString(5, first_air_date);
				pstmt.setString(6, vote_average);
				pstmt.setString(7, vote_count);
				pstmt.setString(8, original_language);
				pstmt.setString(9, poster);
				pstmt.setString(10, createdDate);
				pstmt.executeUpdate();
			}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

