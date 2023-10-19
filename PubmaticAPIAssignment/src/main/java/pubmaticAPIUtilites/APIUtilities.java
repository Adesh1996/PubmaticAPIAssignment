package pubmaticAPIUtilites;

import org.testng.Assert;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class APIUtilities {
	
	
	/**
	 * @param endPoint
	 * @return Response object
	 */
	   public static Response getResponse(String endPoint) {
	    	RequestSpecification request = RestAssured.given();
	    	try { Thread.sleep(120000);}
	    	catch(Exception e) {}
	        Response response = request.get(endPoint);
	        Assert.assertEquals(response.getStatusCode(), 200, "Unable to fetch Records");
	        return response;
	    }
	   
	   /**
	    * This method is used to get specific employee details
	    * @param BaseURL
	    * @param endPoint
	    * @param employeeID
	    * @return 
	    */
	   public static Response getSpecificResponse(String BaseURL , String endPoint,int employeeID)  {
		   RestAssured.baseURI = BaseURL;
			 RequestSpecification request = RestAssured.given().pathParam("id", employeeID);
			 	try { Thread.sleep(120000);}
		    	catch(Exception e) {}
			 Response  response = request.get(endPoint + "{id}");
		     RestAssured.reset();
		     Assert.assertEquals(response.getStatusCode(), 200, "Unable to fetch Records");
		     return response;  
		 
	   }
	   
	   /**
	    * This method is used to delete specific employee details
	    * @param baseURL
	    * @param employeeID
	    * @return
	    */
	   
	   public static Response deleteSpecificRecord(String baseURL,int employeeID) {
	    	RestAssured.baseURI = baseURL;
	    	RequestSpecification request = RestAssured.given();
	    	try { Thread.sleep(120000);}
	    	catch(Exception e) {}
	    	Response response = request.delete("api/v1/delete/" + employeeID);
	        return response;
	    }
	   
		/**
		 * 
		 * rawTojson() Method converts raw data into json 
		 * @return object of JsonPath 
		 * @param String as a value which is response
		 */
		public static JsonPath rawTojson(String value) {
			
			JsonPath jsonpath = new JsonPath(value);
			return jsonpath;
			
		}

}
