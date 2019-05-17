package application.connector.objects;

public class AlertaSensor {

	private int idAlerta;
	private String tipo;
	private String intensidade;
	private String datahoraalerta;
	private String valormedicao;
	private String descricao;
	private String limiteinferior;
	private String limitesuperior;
	private String idInvestigador;

	public AlertaSensor(int idAlerta, String tipo, String intensidade, String datahoraalerta, String valormedicao,
			String descricao, String limiteinferior, String limitesuperior, String idInvestigador) {
		this.idAlerta = idAlerta;
		this.tipo = tipo;
		this.intensidade = intensidade;
		this.datahoraalerta = datahoraalerta;
		this.valormedicao = valormedicao;
		this.descricao = descricao;
		this.limiteinferior = limiteinferior;
		this.limitesuperior = limitesuperior;
		this.idInvestigador = idInvestigador;
	}

	public int getIdAlerta() {
		return idAlerta;
	}

	public void setIdAlerta(int idAlerta) {
		this.idAlerta = idAlerta;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getIntensidade() {
		return intensidade;
	}

	public void setIntensidade(String intensidade) {
		this.intensidade = intensidade;
	}

	public String getDatahoraalerta() {
		return datahoraalerta;
	}

	public void setDatahoraalerta(String datahoraalerta) {
		this.datahoraalerta = datahoraalerta;
	}

	public String getValormedicao() {
		return valormedicao;
	}

	public void setValormedicao(String valormedicao) {
		this.valormedicao = valormedicao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getLimiteinferior() {
		return limiteinferior;
	}

	public void setLimiteinferior(String limiteinferior) {
		this.limiteinferior = limiteinferior;
	}

	public String getLimitesuperior() {
		return limitesuperior;
	}

	public void setLimitesuperior(String limitesuperior) {
		this.limitesuperior = limitesuperior;
	}

	public String getIdInvestigador() {
		return idInvestigador;
	}

	public void setIdInvestigador(String idInvestigador) {
		this.idInvestigador = idInvestigador;
	}
}
