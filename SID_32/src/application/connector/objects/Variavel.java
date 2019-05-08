package application.connector.objects;

public class Variavel {
	
	private String id_variavel;
	private String nome_variavel;
	
	public Variavel(String id_variavel, String nome_variavel) {
		this.id_variavel = id_variavel;
		this.nome_variavel = nome_variavel;
	}

	public String getId_variavel() {
		return id_variavel;
	}

	public void setId_variavel(String id_variavel) {
		this.id_variavel = id_variavel;
	}

	public String getNome_variavel() {
		return nome_variavel;
	}

	public void setNome_variavel(String nome_variavel) {
		this.nome_variavel = nome_variavel;
	}

	@Override
	public String toString() {
		return this.nome_variavel;
	}
	
	

}
