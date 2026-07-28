package duolthdelivery.api.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidConfig.Priority;

import duolthdelivery.api.rest.repository.FirebasetokenRepository;

import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.Message;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FirebaseNotificationService {

	@Autowired
	private FirebasetokenRepository firebasetokenRepository;

	// REMOVE O TOKEN INVÁLIDO DO BANCO
	private void removerTokenInvalido(String token) {
		try {
			var firebasetoken = firebasetokenRepository.findByToken(token);
			if (firebasetoken != null) {
				firebasetokenRepository.delete(firebasetoken);
				System.out.println("Token inválido removido: " + token);
			}
		} catch (Exception e) {
			System.out.println("Erro ao remover token: " + e.getMessage());
		}
	}

	// VERIFICA SE O ERRO É DE TOKEN INVÁLIDO/EXPIRADO
	private boolean isTokenInvalido(FirebaseMessagingException e) {
		MessagingErrorCode code = e.getMessagingErrorCode();
		return code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.INVALID_ARGUMENT;
	}

	public String enviarNotificacao(String token, String titulo, String mensagem, Long corridaId, Long cliente_id) {
		try {
			Message message = Message.builder().setToken(token)
					.setNotification(Notification.builder().setTitle(titulo).setBody(mensagem).build())
					.putData("title", titulo).putData("body", mensagem).putData("corridaId", corridaId.toString())
					.putData("despachanteId", cliente_id.toString())
					.setAndroidConfig(AndroidConfig.builder().setPriority(Priority.HIGH)
							.setNotification(AndroidNotification.builder().setChannelId("corrida_channel")
									.setSound("default").setIcon("ic_notification").setTag("corrida_ativa").build())
							.build())
					.setApnsConfig(ApnsConfig.builder()
							.setAps(Aps.builder().setContentAvailable(true).setBadge(1).setSound("default").build())
							.putHeader("apns-priority", "10").build())
					.build();
			String response = FirebaseMessaging.getInstance().send(message);
			System.out.println("Firebase OK: " + response);
			return "Notificação Enviada";
		} catch (FirebaseMessagingException e) {
			if (isTokenInvalido(e)) {
				removerTokenInvalido(token);
				return "Token inválido removido";
			}
			e.printStackTrace();
			return "Erro ao enviar notificação";
		} catch (Exception e) {
			e.printStackTrace();
			return "Erro ao enviar notificação";
		}
	}

	public String enviarNotificacaoMultipla(String token, String titulo, String mensagem, List<Long> corridaIds,
			Long cliente_id) {
		try {
			String corridaIdsStr = corridaIds.stream().map(String::valueOf).collect(Collectors.joining(","));
			Message message = Message.builder().setToken(token)
					.setNotification(Notification.builder().setTitle(titulo).setBody(mensagem).build())
					.putData("title", titulo).putData("body", mensagem)
					.putData("corridaId", corridaIds.get(0).toString()).putData("corridaIds", corridaIdsStr)
					.putData("despachanteId", cliente_id.toString())
					.setAndroidConfig(AndroidConfig.builder().setPriority(Priority.HIGH)
							.setNotification(AndroidNotification.builder().setChannelId("corrida_channel")
									.setSound("default").setIcon("ic_notification").setTag("corrida_ativa").build())
							.build())
					.setApnsConfig(ApnsConfig.builder()
							.setAps(Aps.builder().setContentAvailable(true).setBadge(1).setSound("default").build())
							.putHeader("apns-priority", "10").build())
					.build();
			String response = FirebaseMessaging.getInstance().send(message);
			System.out.println("Firebase OK: " + response);
			return "Notificação Enviada";
		} catch (FirebaseMessagingException e) {
			if (isTokenInvalido(e)) {
				removerTokenInvalido(token);
				return "Token inválido removido";
			}
			e.printStackTrace();
			return "Erro ao enviar notificação";
		} catch (Exception e) {
			e.printStackTrace();
			return "Erro ao enviar notificação";
		}
	}

	public String enviarNotificacaoPerdida(String token, String titulo, String mensagem, Long corridaId) {
		try {
			Message message = Message.builder().setToken(token)
					.setNotification(Notification.builder().setTitle(titulo).setBody(mensagem).build())
					.putData("title", titulo).putData("body", mensagem).putData("corridaId", corridaId.toString())
					.setAndroidConfig(AndroidConfig.builder().setPriority(Priority.HIGH)
							.setNotification(AndroidNotification.builder().setChannelId("geral_channel")
									.setSound("default").setIcon("ic_notification").setTag("corrida_ativa").build())
							.build())
					.setApnsConfig(ApnsConfig.builder()
							.setAps(Aps.builder().setContentAvailable(true).setBadge(1).setSound("default").build())
							.putHeader("apns-priority", "10").build())
					.build();
			String response = FirebaseMessaging.getInstance().send(message);
			System.out.println("Firebase OK: " + response);
			return "Notificação Perdida Enviada";
		} catch (FirebaseMessagingException e) {
			if (isTokenInvalido(e)) {
				removerTokenInvalido(token);
				return "Token inválido removido";
			}
			e.printStackTrace();
			return "Erro ao enviar notificação perdida";
		} catch (Exception e) {
			e.printStackTrace();
			return "Erro ao enviar notificação perdida";
		}
	}

	// CANCELA A NOTIFICAÇÃO EXIBIDA - MESMA TAG SUBSTITUI A ANTERIOR NA BARRA
	public String enviarCancelamento(String token) {
		try {
			Message message = Message.builder().setToken(token)
					.setNotification(Notification.builder().setTitle("Corrida Cancelada ❌")
							.setBody("O estabelecimento cancelou a chamada").build())
					.putData("tipo", "CANCELAR_CORRIDA")
					.setAndroidConfig(AndroidConfig.builder().setPriority(Priority.HIGH)
							.setNotification(AndroidNotification.builder().setChannelId("geral_channel")
									.setIcon("ic_notification").setTag("corrida_ativa").build())
							.build())
					.setApnsConfig(
							ApnsConfig.builder().setAps(Aps.builder().setContentAvailable(true).setBadge(0).build())
									.putHeader("apns-priority", "10").build())
					.build();
			String response = FirebaseMessaging.getInstance().send(message);
			System.out.println("Firebase Cancelamento OK: " + response);
			return "Cancelamento Enviado";
		} catch (FirebaseMessagingException e) {
			if (isTokenInvalido(e)) {
				removerTokenInvalido(token);
				return "Token inválido removido";
			}
			e.printStackTrace();
			return "Erro ao enviar cancelamento";
		} catch (Exception e) {
			e.printStackTrace();
			return "Erro ao enviar cancelamento";
		}
	}
}