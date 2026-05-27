package wolvesofdelivery.api.rest.service;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

@Service
public class FirebaseNotificationService {

	public String enviarNotificacao(String token, String titulo, String mensagem, Long corridaId, Long cliente_id) {
		try {
			Message message = Message.builder()
					.setToken(token)
					.putData("title", titulo)
					.putData("body", mensagem)
					.putData("corridaId", corridaId.toString())
					.putData("despachanteId", cliente_id.toString())
					.build();
			FirebaseMessaging.getInstance().send(message);
			return "Notificação Enviada";
		} catch (Exception e) {
			e.printStackTrace();
			return "Erro ao enviar notificação";
		}
	}
}