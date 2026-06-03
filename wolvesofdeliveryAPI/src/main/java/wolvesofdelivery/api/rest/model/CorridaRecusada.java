package wolvesofdelivery.api.rest.model;

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
public class CorridaRecusada implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "corrida_recusada_gen")
	@SequenceGenerator(name = "corrida_recusada_gen", sequenceName = "corrida_recusada_seq", allocationSize = 1)
	private long id;

	@ManyToOne
	@JoinColumn(name = "motorista_id")
	private Usuario motorista;

	@ManyToOne
	@JoinColumn(name = "corrida_id")
	private Corridas corrida;

	private Timestamp dataRecusa;

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public Timestamp getDataRecusa() {
		return dataRecusa;
	}

	public void setDataRecusa(Timestamp dataRecusa) {
		this.dataRecusa = dataRecusa;
	}

}
