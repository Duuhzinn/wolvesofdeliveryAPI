package wolvesofdelivery.api.config;

import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

	@PostConstruct
	public void initializeFirebase() throws IOException {
		FileInputStream serviceAccount = new FileInputStream(
				"src/main/resources/wolvesofdelivery-a6e2a-firebase-adminsdk-fbsvc-91bc594c7c.json");//

		FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();

		if (FirebaseApp.getApps().isEmpty()) {
			FirebaseApp.initializeApp(options); //
		}
	}

}
