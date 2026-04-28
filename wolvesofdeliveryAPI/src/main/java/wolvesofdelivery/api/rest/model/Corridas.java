package wolvesofdelivery.api.rest.model;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@JsonPropertyOrder({
    "id",
    "statuscorrida",
    "datachamada",
    "iniciocorrida",
    "terminocorrida",
    "usuario_id"
})

@Entity
public class Corridas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO) // aqui faz o auto incremento
	private Long id;

	private String statuscorrida;
	private Timestamp datachamada;
	private Timestamp iniciocorrida;
	private Timestamp terminocorrida;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStatuscorrida() {
		return statuscorrida;
	}

	public void setStatuscorrida(String statuscorrida) {
		this.statuscorrida = statuscorrida;
	}

	public Timestamp getDatachamada() {
		return datachamada;
	}

	public void setDatachamada(Timestamp datachamada) {
		this.datachamada = datachamada;
	}

	public Timestamp getIniciocorrida() {
		return iniciocorrida;
	}

	public void setIniciocorrida(Timestamp iniciocorrida) {
		this.iniciocorrida = iniciocorrida;
	}

	public Timestamp getTerminocorrida() {
		return terminocorrida;
	}

	public void setTerminocorrida(Timestamp terminocorrida) {
		this.terminocorrida = terminocorrida;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
