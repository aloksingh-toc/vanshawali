package in.ibrahimabad.vanshawali;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VanshawaliApiApplication {

	public static void main(String[] args) {
		// pgjdbc reports the JVM zone id verbatim to Postgres as a session parameter;
		// some JVMs report the deprecated "Asia/Calcutta" alias, which Postgres rejects.
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
		SpringApplication.run(VanshawaliApiApplication.class, args);
	}

}
