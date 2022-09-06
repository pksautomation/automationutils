package com.innovaccer.utils.v2.cucumber;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import cucumber.api.testng.AbstractTestNGCucumberTests;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.PickleEventWrapper;
import cucumber.api.testng.TestNGCucumberRunner;

public class TestNGCucumberTests extends AbstractTestNGCucumberTests {

	protected TestNGCucumberRunner testNGCucumberRunner;

	@Test(groups = "cucumber", description = "Runs Cucumber scenarios", dataProvider = "scenario")
	public void scenario(PickleEventWrapper pickleEvent, CucumberFeatureWrapper 
		    cucumberFeature) throws Throwable {
		testNGCucumberRunner.runScenario(pickleEvent.getPickleEvent());;

	}
	
	@DataProvider
	public Object[][] scenario() {
		return testNGCucumberRunner.provideScenarios();
	}
	
	@BeforeSuite(alwaysRun = true)
	public void setUpClass() {
		testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
	}

	@AfterSuite(alwaysRun = true)
	public void tearDownClass() {

		testNGCucumberRunner.finish();

	}
	

}
