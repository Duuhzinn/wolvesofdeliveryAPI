package wolvesofdelivery.api.rest.service;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidConfig.Priority;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.Message;

@Service
public class FirebaseNotificationService {

	public String enviarNotificacao(String token, String titulo, String mensagem, Long corridaId, Long cliente_id) {
		try {
			Message message = Message.builder()
					.setToken(token)
					.setNotification(Notification.builder()
							.setTitle(titulo)
							.setBody(mensagem)
							.build())
					.putData("title", titulo)
					.putData("body", mensagem)
					.putData("corridaId", corridaId.toString())
					.putData("despachanteId", cliente_id.toString())
					.setAndroidConfig(AndroidConfig.builder()
					.setPriority(Priority.HIGH)
					.build())
					.setApnsConfig(ApnsConfig.builder()
							.setAps(Aps.builder()
									.setContentAvailable(true)
									.build())
							.putHeader("apns-priority", "10")
							.build())
					.build();
			FirebaseMessaging.getInstance().send(message);
			return "Notificação Enviada";
		} catch (Exception e) {
			e.printStackTrace();
			return "Erro ao enviar notificação";
		}
	}
}