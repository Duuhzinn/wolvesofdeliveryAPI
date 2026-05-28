package wolvesofdelivery.api.rest.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.UniqueConstraint;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonPropertyOrder({ "id", "nome", "login", "email", "telefone", "endereco", "tipoUser", "status", "posicaofila" })
// ALTERADO: removido "senha", "clientes", "corridas", "token" do JsonPropertyOrder

@Entity
public class Usuario implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_gen")
	@SequenceGenerator(name = "usuario_gen", sequenceName = "usuario_seq", allocationSize = 1)
	private Long id;

	private String login;
	private String senha;
	private String telefone;
	private String nome;
	private String email;
	private String endereco;
	private String tipoUser;
	private Long status;
	private Timestamp posicaofila;

	@OneToMany(mappedBy = "usuario", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Corridas> corridas = new ArrayList<Corridas>();

	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_role", uniqueConstraints = @UniqueConstraint(columnNames = { "usuario_id",
			"role_id" }, name = "unique_role_user"), joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id", table = "usuario", unique = false, foreignKey = @ForeignKey(name = "usuario_fk", value = ConstraintMode.CONSTRAINT)), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id", table = "role", unique = false, foreignKey = @ForeignKey(name = "role_fk", value = ConstraintMode.CONSTRAINT)))
	private List<Role> roles;

	@JsonIgnore // já estava
	public List<Corridas> getCorridas() {
		return corridas;
	}

	public void setCorridas(List<Corridas> corridas) {
		this.corridas = corridas;
	}

	@OneToMany(mappedBy = "usuario", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Firebasetoken> token = new ArrayList<Firebasetoken>();

	@JsonIgnore // já estava
	public List<Firebasetoken> getToken() {
		return token;
	}

	public void setToken(List<Firebasetoken> token) {
		this.token = token;
	}

	@OneToMany(mappedBy = "usuario", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Clientes> clientes = new ArrayList<Clientes>();

	@JsonIgnore // já estava
	public List<Clientes> getClientes() {
		return clientes;
	}

	public void setClientes(List<Clientes> clientes) {
		this.clientes = clientes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(unique = true)
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getTipoUser() {
		return tipoUser;
	}

	public void setTipoUser(String tipoUser) {
		this.tipoUser = tipoUser;
	}

	
	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	
	public Timestamp getPosicaofila() {
		return posicaofila;
	}

	public void setPosicaofila(Timestamp posicaofila) {
		this.posicaofila = posicaofila;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(id, other.id);
	}

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	@JsonIgnore // já estava
	@Override
	public @Nullable String getPassword() {
		return this.senha;
	}

	@JsonIgnore // já estava
	@Override
	public String getUsername() {
		return this.login;
	}

	@JsonIgnore // já estava
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore // já estava
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore // já estava
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@JsonIgnore // ADICIONADO: isEnabled estava faltando!
	@Override
	public boolean isEnabled() {
		return true;
	}

	public void setRoles(List<Role> roles) { // ALTERADO: removido @JsonIgnore do setter
		this.roles = roles;
	}
}