package testruner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features ="src/test/java/features",
        glue = "stepdefinition",
        tags = "@checkout",
        plugin = {
            "pretty",
                "html:target\\cucumber\\rapport.html",
                "json:target\\cucumber\\rapport1.json"

        }

)
public class run {



}
