package MyPackage;

import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.util.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;

public class FunctionalVsVisualTest {

	private Eyes eyes;
	private ChromeDriver driver;
	private EyesRunner runner = null;

	private boolean shouldBreakSite = false;

	@Test
	public void Test() {

		String url = "https://github.com/login";

		driver.get(url);

		eyes.checkWindow("Login Page");

		driver.findElement(By.cssSelector("#login > form > div.auth-form-body.mt-3 > input.btn.btn-primary.btn-block"))
				.click();

		if (shouldBreakSite)
			BreakSite();

		// validate sign in button
		String buttonText = driver
				.findElement(
						By.cssSelector("#login > form > div.auth-form-body.mt-3 > input.btn.btn-primary.btn-block"))
				.getAttribute("value");

		Assert.isTrue(buttonText.compareTo("Sign in") == 0, "wrong button");

		// validate error message
		String errorMessage = driver.findElement(By.cssSelector("#js-flash-container > div > div")).getText();

		Assert.isTrue(errorMessage.contains("Incorrect username or password."), "wrong label");

		String usernameTextbox = driver
				.findElement(By.cssSelector("#login > form > div.auth-form-body.mt-3 > label:nth-child(1)")).getText();

		Assert.isTrue(usernameTextbox.contains("Username or email address"), "wrong label");

		String passwordTextbox = driver
				.findElement(By.cssSelector("#login > form > div.auth-form-body.mt-3 > label:nth-child(3)")).getText();

		Assert.isTrue(passwordTextbox.contains("Password"), "wrong label");

		eyes.checkWindow("Error Message");
	}

	private void BreakSite() {
		((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('style','color: red')",
				driver.findElement(By.cssSelector("#login > form > div.auth-form-body.mt-3 > label:nth-child(1)")));
		((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('style','color: pink')",
				driver.findElement(By.cssSelector("#login > form > div.auth-form-body.mt-3 > label:nth-child(3)")));
		((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('style','fill: green')",
				driver.findElement(By.cssSelector(
						"body > div.position-relative.js-header-wrapper > div.header.header-logged-out.width-full.pt-5.pb-4 > div > a > svg")));
		((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('style','font-size: 8px')",
				driver.findElement(By.cssSelector("#login > p")));

	}

	@AfterMethod
	public void After() {

		try {

			eyes.closeAsync();

			System.out.println(runner.getAllTestResults(false));

		} finally {
			driver.quit();
		}

	}

	@BeforeMethod
	public void Before() {

		String shouldBreakSiteStr = System.getenv("INJECT_BUG");

		shouldBreakSite = Boolean.parseBoolean(shouldBreakSiteStr);

		String testName = "Functional VS Visual";

		runner = new VisualGridRunner(10);

		eyes = new Eyes(runner);

		ChromeOptions chrome_options = new ChromeOptions();
		chrome_options.addArguments("--headless");
		chrome_options.addArguments("--no-sandbox");
		chrome_options.addArguments("--disable-dev-shm-usage");

		driver = new ChromeDriver(chrome_options);

		Configuration sconf = eyes.getConfiguration();

		sconf.setAppName(testName);

		sconf.setTestName(testName);

		sconf.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

		sconf.setViewportSize(new RectangleSize(1200, 600));

		sconf.setBatch(new BatchInfo(testName));

		sconf.addBrowser(1200, 800, BrowserType.CHROME);
		sconf.addBrowser(1200, 800, BrowserType.FIREFOX);
		sconf.addBrowser(1200, 800, BrowserType.IE_11);
		sconf.addBrowser(1200, 800, BrowserType.EDGE);
		sconf.addBrowser(1200, 800, BrowserType.SAFARI);

		sconf.addDeviceEmulation(DeviceName.iPad, ScreenOrientation.PORTRAIT);
		sconf.addDeviceEmulation(DeviceName.iPad_Pro, ScreenOrientation.PORTRAIT);
		sconf.addDeviceEmulation(DeviceName.iPhone6_7_8_Plus, ScreenOrientation.PORTRAIT);
		sconf.addDeviceEmulation(DeviceName.iPhone_X, ScreenOrientation.PORTRAIT);
		sconf.addDeviceEmulation(DeviceName.Galaxy_Note_3, ScreenOrientation.PORTRAIT);
		sconf.addDeviceEmulation(DeviceName.Nexus_10, ScreenOrientation.PORTRAIT);

		eyes.setConfiguration(sconf);

		eyes.open(driver);
	}
}
