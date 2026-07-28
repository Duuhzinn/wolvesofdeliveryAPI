package duolthdelivery.api.rest.model;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class CorridaExpirada implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "corrida_expirada_gen")
	@SequenceGenerator(name = "corrida_expirada_gen", sequenceName = "corrida_expirada_seq", allocationSize = 1)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "motorista_id")
	private Usuario motorista;

	@ManyToOne
	@JoinColumn(name = "corrida_id")
	private Corridas corrida;

	private Timestamp dataExpirada;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getMotorista() {
		return motorista;
	}

	public void setMotorista(Usuario motorista) {
		this.motorista = motorista;
	}

	public Corridas getCorrida() {
		return corrida;
	}

	public void setCorrida(Corridas corrida) {
		this.corrida = corrida;
	}

	public Timestamp getDataExpirada() {
		return dataExpirada;
	}

	public void setDataExpirada(Timestamp dataExpirada) {
		this.dataExpirada = dataExpirada;
	}

}
