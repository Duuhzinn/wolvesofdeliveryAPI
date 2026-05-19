package wolvesofdelivery.api.rest.service;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FirebaseNotificationService {

	public String enviarNotificacao(String token, String titulo, String mensagem) {

		try {
			Message message = Message.builder().setToken(token)
					.setNotification(Notification.builder().setTitle(titulo).setBody(mensagem).build()).build();
			FirebaseMessaging.getInstance().send(message);

			return "Notificação Enviada";
		} catch (Exception e) {
			e.printStackTrace();
			return "Erro ao enviar notificação";
		}

	}

}
