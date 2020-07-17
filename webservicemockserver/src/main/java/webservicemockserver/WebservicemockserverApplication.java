package webservicemockserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class WebservicemockserverApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(WebservicemockserverApplication.class, args);
	}

}
