package duolthdelivery.api.config;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

	@PostConstruct
	public void initializeFirebase() throws IOException {

		System.out.println("Inicializando Firebase...");
		
		InputStream serviceAccount;

		String firebaseConfig = System.getenv("FIREBASE_CREDENTIALS");

		if (firebaseConfig != null) {
			
			serviceAccount = new ByteArrayInputStream(firebaseConfig.getBytes());
		} else {
			serviceAccount = new FileInputStream("src/main/resources/firebase.json");
		}
		
		FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();
		
		if(FirebaseApp.getApps().isEmpty()) {
			
			FirebaseApp.initializeApp(options);
			
			System.out.println("Firebase inicializado!");
				
		}
	}
		
}


