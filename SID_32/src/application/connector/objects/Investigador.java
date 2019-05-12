package application.connector.objects;

public class Investigador {

	private String id_investigador;
	private String nome_investigador;
	private String email_investigador;
	private String pwd;
	private String categoria_profissional;

	public Investigador(String id_investigador, String nome_investigador, String email_investigador, String pwd,
			String categoria_profissional) {
		super();
		this.id_investigador = id_investigador;
		this.nome_investigador = nome_investigador;
		this.email_investigador = email_investigador;
		this.pwd = pwd;
		this.categoria_profissional = categoria_profissional;
	}

	public String getId_investigador() {
		return id_investigador;
	}

	public void setId_investigador(String id_investigador) {
		this.id_investigador = id_investigador;
	}

	public String getNome_investigador() {
		return nome_investigador;
	}

	public void setNome_investigador(String nome_investigador) {
		this.nome_investigador = nome_investigador;
	}

	public String getEmail_investigador() {
		return email_investigador;
	}

	public void setEmail_investigador(String email_investigador) {
		this.email_investigador = email_investigador;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getCategoria_profissional() {
		return categoria_profissional;
	}

	public void setCategoria_profissional(String categoria_profissional) {
		this.categoria_profissional = categoria_profissional;
	}

}
