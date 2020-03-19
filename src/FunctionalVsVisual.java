import java.io.InputStream;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.util.Assert;

import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;

public class FunctionalVsVisual {

	public static void main(String[] args) {
		try {
			Before();

			Test();
			
			System.out.println("Test Completed Successfully");
		} finally {
			After();
		}	
	}

	@SuppressWarnings("deprecation")
	private static void Test() {

		String url = "https://github.com/login";

		driver.get(url);

		driver.findElement(By.cssSelector("#login > form > div.auth-form-body.mt-3 > input.btn.btn-primary.btn-block"))
				.click();

		//BreakSite();
		
		// validate sign in button
		String buttonText = driver
				.findElement(
						By.cssSelector("#login > form > div.auth-form-body.mt-3 > input.btn.btn-primary.btn-block"))
				.getAttribute("value");
		
		Assert.isTrue(buttonText.compareTo("Sign in") == 0 , "wrong button");
		
		// validate error message
		String errorMessage = driver
				.findElement(
						By.cssSelector("#js-flash-container > div > div")).getText();

		Assert.isTrue(errorMessage.contains("Incorrect username or password.") , "wrong label");
		
		String usernameTextbox = driver
				.findElement(
						By.cssSelector("#login > form > div.auth-form-body.mt-3 > label:nth-child(1)")).getText();

		Assert.isTrue(usernameTextbox.contains("Username or email address") , "wrong label");
		
		String passwordTextbox = driver
				.findElement(
						By.cssSelector("#login > form > div.auth-form-body.mt-3 > label:nth-child(3)")).getText();

		Assert.isTrue(passwordTextbox.contains("Password") , "wrong label");
		
		eyes.checkWindow();
	}

	private static void BreakSite() {
		((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('style','color: red')", driver.findElement(By.cssSelector("#login > form > div.auth-form-body.mt-3 > label:nth-child(1)")));
		((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('style','color: pink')", driver.findElement(By.cssSelector("#login > form > div.auth-form-body.mt-3 > label:nth-child(3)")));
		((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('style','fill: green')", driver.findElement(By.cssSelector("body > div.position-relative.js-header-wrapper > div.header.header-logged-out.width-full.pt-5.pb-4 > div > a > svg")));
		((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('style','font-size: 8px')", driver.findElement(By.cssSelector("#login > p")));
		
	}

	private static Eyes eyes;
	private static ChromeDriver driver;
	private static EyesRunner runner = null;

	private static void After() {

		if (eyes.getIsDisabled() == false) {
			eyes.close(false);
		}
		driver.quit();
	}

	public static void Before() {

		String testName = "Functional VS Visual";

		runner = new ClassicRunner();

		eyes = new Eyes(runner);
		
		eyes.setIsDisabled(true);

		driver = new ChromeDriver();

		Configuration sconf = new Configuration();

		
		sconf.setAppName(testName);

		sconf.setTestName(testName);

		sconf.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

		sconf.setViewportSize(new RectangleSize(1200, 600));

		eyes.setConfiguration(sconf);

		eyes.open(driver);

	}

	public static void printVersion() {
		String version = null;

		FunctionalVsVisual bt = new FunctionalVsVisual();

		try {
			Properties p = new Properties();

			InputStream is = bt.getClass()
					.getResourceAsStream("/META-INF/maven/com.applitools/eyes-selenium-java3/pom.properties");
			if (is != null) {
				p.load(is);
				version = p.getProperty("artifactId", "");
				version += " " + p.getProperty("version", "");

			}
		} catch (Exception e) {
			// ignore
		}

		// fallback to using Java API
		if (version == null) {
			Package aPackage = bt.getClass().getPackage();
			if (aPackage != null) {
				version = aPackage.getImplementationVersion();
				if (version == null) {
					version = aPackage.getSpecificationVersion();
				}
			}
		}

		if (version == null) {
			// we could not compute the version so use a blank
			version = "";
		}

		System.out.println("This app was built with " + version);
	}

}
