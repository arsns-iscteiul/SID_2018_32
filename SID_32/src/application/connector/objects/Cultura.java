package application.connector.objects;

public class Cultura{

	private String id_cultura;
	private String nome_cultura;
	private String descricao_cultura;
	private String tipo_cultura;
	private String investigador_fk;

	public Cultura(String id_cultura, String nome_cultura, String descricao_cultura, String tipo_cultura,
			String investigador_fk) {
		this.id_cultura = id_cultura;
		this.nome_cultura = nome_cultura;
		this.descricao_cultura = descricao_cultura;
		this.tipo_cultura = tipo_cultura;
		this.investigador_fk = investigador_fk;
	}

	public String getId_cultura() {
		return id_cultura;
	}

	public void setId_cultura(String id_cultura) {
		this.id_cultura = id_cultura;
	}

	public String getNome_cultura() {
		return nome_cultura;
	}

	public void setNome_cultura(String nome_cultura) {
		this.nome_cultura = nome_cultura;
	}

	public String getDescricao_cultura() {
		return descricao_cultura;
	}

	public void setDescricao_cultura(String descricao_cultura) {
		this.descricao_cultura = descricao_cultura;
	}

	public String getTipo_cultura() {
		return tipo_cultura;
	}

	public void setTipo_cultura(String tipo_cultura) {
		this.tipo_cultura = tipo_cultura;
	}

	public String getInvestigador_fk() {
		return investigador_fk;
	}

	public void setInvestigador_fk(String investigador_fk) {
		this.investigador_fk = investigador_fk;
	}

	@Override
	public String toString() {
		return this.nome_cultura;
	}

}
