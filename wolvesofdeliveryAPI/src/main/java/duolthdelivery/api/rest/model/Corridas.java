package duolthdelivery.api.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@JsonPropertyOrder({ "id", "statuscorrida", "datachamada", "iniciocorrida", "terminocorrida", "usuario_id" })

@Entity
public class Corridas implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "corridas_gen")
	@SequenceGenerator(name = "corridas_gen", sequenceName = "corridas_seq", allocationSize = 1)
	private Long id;

	private String endereco_entrega;
	private Timestamp data_chamada;
	private Timestamp data_aceite;
	private Timestamp inicio_corrida;
	private Timestamp termino_corrida;
	private String status_corrida;
	private BigDecimal valor_corrida;
	private String obs;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;
	@ManyToOne
	@JoinColumn(name = "cliente_id")
	private Usuario cliente;
	@ManyToOne
	@JoinColumn(name = "motorista_id")
	private Usuario motorista;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEndereco_entrega() {
		return endereco_entrega;
	}

	public void setEndereco_entrega(String endereco_entrega) {
		this.endereco_entrega = endereco_entrega;
	}

	public Timestamp getData_chamada() {
		return data_chamada;
	}

	public void setData_chamada(Timestamp data_chamada) {
		this.data_chamada = data_chamada;
	}

	public Timestamp getData_aceite() {
		return data_aceite;
	}

	public void setData_aceite(Timestamp data_aceite) {
		this.data_aceite = data_aceite;
	}

	public Timestamp getInicio_corrida() {
		return inicio_corrida;
	}

	public void setInicio_corrida(Timestamp inicio_corrida) {
		this.inicio_corrida = inicio_corrida;
	}

	public Timestamp getTermino_corrida() {
		return termino_corrida;
	}

	public void setTermino_corrida(Timestamp termino_corrida) {
		this.termino_corrida = termino_corrida;
	}

	public String getStatus_corrida() {
		return status_corrida;
	}

	public void setStatus_corrida(String status_corrida) {
		this.status_corrida = status_corrida;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getCliente() {
		return cliente;
	}

	public void setCliente(Usuario cliente) {
		this.cliente = cliente;
	}

	public Usuario getMotorista() {
		return motorista;
	}

	public void setMotorista(Usuario motorista) {
		this.motorista = motorista;
	}

	public BigDecimal getValor_corrida() {
		return valor_corrida;
	}

	public void setValor_corrida(BigDecimal valor_corrida) {
		this.valor_corrida = valor_corrida;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

}
