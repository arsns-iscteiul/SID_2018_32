package application.connector.objects;

public class AlertaVariavel {

	private int idAlertaVariavel;
	private String dataHora;
	private String descricao;
	private int idMedicao;
	private String intensidade;
	private int idInvestigador;

	public AlertaVariavel(int idAlertaVariavel, String dataHora, String descricao, int idMedicao, String intensidade,
			int idInvestigador) {
		this.idAlertaVariavel = idAlertaVariavel;
		this.dataHora = dataHora;
		this.descricao = descricao;
		this.idMedicao = idMedicao;
		this.intensidade = intensidade;
		this.idInvestigador = idInvestigador;
	}

	public int getIdAlertaVariavel() {
		return idAlertaVariavel;
	}

	public void setIdAlertaVariavel(int idAlertaVariavel) {
		this.idAlertaVariavel = idAlertaVariavel;
	}

	public String getDataHora() {
		return dataHora;
	}

	public void setDataHora(String dataHora) {
		this.dataHora = dataHora;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getIdMedicao() {
		return idMedicao;
	}

	public void setIdMedicao(int idMedicao) {
		this.idMedicao = idMedicao;
	}

	public String getIntensidade() {
		return intensidade;
	}

	public void setIntensidade(String intensidade) {
		this.intensidade = intensidade;
	}

	public int getIdInvestigador() {
		return idInvestigador;
	}

	public void setIdInvestigador(int idInvestigador) {
		this.idInvestigador = idInvestigador;
	}

}
