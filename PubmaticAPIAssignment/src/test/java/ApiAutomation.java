import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pubmaticAPIUtilites.APIUtilities;

public class ApiAutomation {

	private static final String BASE_URL = "https://dummy.restapiexample.com/api/v1";
	protected Logger log = LogManager.getLogger();

	@Test(description = "To verify count of employee whose age is greter than 30 and salary greater than 50K",priority = 1)
	public void testEmployeesWithAgeAndSalary() {
		RestAssured.baseURI = BASE_URL;
		Response response = APIUtilities.getResponse("/employees");
		
		//Get the employee details
		String responseBody = response.getBody().asString();

		JsonPath js = new JsonPath(responseBody);
		Assert.assertEquals(js.get("status"), "success", "Unable to fetch details");
		List<String> employee = new ArrayList<String>();

		for (int i = 0; i < js.getInt("data.size"); i++) {
			if (Integer.parseInt(js.getString("data[" + i + "].employee_age")) > 30
					&& Integer.parseInt(js.getString("data[" + i + "].employee_salary")) > 50000) {
				employee.add(js.getString("data[" + i + "]"));
				log.info(js.getString("data[" + i + "]"));
			}

		}
		Assert.assertEquals(employee.size(), 16, "Count is not Matching");
	}

	@Test(description = "To verify emplyee details",priority = 2)
	public void testSingleEmployeeData() {

		Response response = APIUtilities.getSpecificResponse(BASE_URL, "/employee/", 12);

		// Get employee data
		JsonPath js = APIUtilities.rawTojson(response.getBody().asString());
		Assert.assertEquals(js.get("status"), "success", "Unable to fetch details");
		Assert.assertEquals(js.get("data.employee_name"), "Quinn Flynn", "Employee name is not matching");
		Assert.assertEquals(js.get("data.employee_salary"), 342000, "Employee Salary is different");

	}

	@Test(description = "Delete employee and verify status",priority = 3)
	public void testDeleteEmployeeAndVerify()  {
		RestAssured.baseURI = BASE_URL;
		int employeeIdToDelete = 15;
		RequestSpecification request = RestAssured.given();
		Response response = request.delete("/delete/" + employeeIdToDelete);
		

		
		// Validate the response code after deletion
		int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode, 200, "Unexpected status code");

		// Attempt to get the deleted employee's data and verify that it should return a
		// --> successfully! deleted Records
		Response response1 = APIUtilities.getSpecificResponse(BASE_URL, "/employee/", 15);
		JsonPath js = APIUtilities.rawTojson(response1.getBody().asString());
		Assert.assertEquals(js.get("message"), "successfully! deleted Records", "Not Deleted available in response");
	}

	@Test(description =  "Create and validate employee",priority = 4)
	public void testCreateEmployeeAndValidate() {
		RestAssured.baseURI = BASE_URL;
		RequestSpecification request = RestAssured.given().header("Content-Type", "application/json");

		Response response = request.body(
				"{\"employee_name\":\"Test\",\"employee_salary\":180000,\"employee_age\":28,\"profile_image\":\"\"}")
				.post("/create");

		// Validate the response code
		int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode, 200, "Unexpected status code");

		JsonPath js = APIUtilities.rawTojson(response.getBody().asString());
		int ID = js.getInt("data.id");

		Response response1 = APIUtilities.getSpecificResponse(BASE_URL, "/employee/", ID);
		JsonPath js1 = APIUtilities.rawTojson(response1.getBody().asString());
		Assert.assertEquals(js1.get("data.employee_name"), "Test", "Employee name is not matching");
		Assert.assertEquals(js1.getInt("data.employee_salary"), 180000, "Employee Salary is different");
		Assert.assertEquals(js1.getInt("data.employee_age"), 28, "Employee Salary is different");

	}

	@AfterSuite(alwaysRun = true)
	public void openReport() {
		try {
			// Opening the current Report file
			File reportFile = new File(System.getProperty("user.dir") + "/test-output/index.html");
			Desktop desktop = Desktop.getDesktop();
			if (reportFile.exists()) {
				desktop.open(reportFile);
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			Assert.fail(" ", e.getCause());
		}
	}
}
