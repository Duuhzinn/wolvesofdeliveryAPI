package duolthdelivery.api.rest.model;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "configuracao_corrida")
public class ConfiguracaoCorrida {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "config_corrida_gen")
    @SequenceGenerator(name = "config_corrida_gen", sequenceName = "config_corrida_seq", allocationSize = 1)
    private Long id;
	
	@Column(nullable = true)
    private BigDecimal valor;
	
	@ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	@Override
    public int hashCode() { return Objects.hash(id); }
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ConfiguracaoCorrida other = (ConfiguracaoCorrida) obj;
        return Objects.equals(id, other.id);
    }

}
