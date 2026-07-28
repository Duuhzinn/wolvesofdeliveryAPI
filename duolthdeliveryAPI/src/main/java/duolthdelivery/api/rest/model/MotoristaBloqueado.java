package duolthdelivery.api.rest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "motorista_bloqueado", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"restaurante_id", "motorista_id"})
})
public class MotoristaBloqueado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurante_id", nullable = false)
    private Long restauranteId;

    @Column(name = "motorista_id", nullable = false)
    private Long motoristaId;

    @Column(name = "data_bloqueio", nullable = false)
    private LocalDateTime dataBloqueio;

    public MotoristaBloqueado() {
    }

    public MotoristaBloqueado(Long restauranteId, Long motoristaId) {
        this.restauranteId = restauranteId;
        this.motoristaId = motoristaId;
        this.dataBloqueio = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(Long restauranteId) {
        this.restauranteId = restauranteId;
    }

    public Long getMotoristaId() {
        return motoristaId;
    }

    public void setMotoristaId(Long motoristaId) {
        this.motoristaId = motoristaId;
    }

    public LocalDateTime getDataBloqueio() {
        return dataBloqueio;
    }

    public void setDataBloqueio(LocalDateTime dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }
}