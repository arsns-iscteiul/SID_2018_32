package application.connector.objects;

public class MedicaoLuminosidade {

	private String id_medicao_luminosidade;
	private String data_hora_medicao;
	private String valor_medicao_luminosidade;

	public MedicaoLuminosidade(String id_medicao_luminosidade, String data_hora_medicao,
			String valor_medicao_luminosidade) {
		super();
		this.id_medicao_luminosidade = id_medicao_luminosidade;
		this.data_hora_medicao = data_hora_medicao;
		this.valor_medicao_luminosidade = valor_medicao_luminosidade;
	}

	public String getId_medicao_luminosidade() {
		return id_medicao_luminosidade;
	}

	public void setId_medicao_luminosidade(String id_medicao_luminosidade) {
		this.id_medicao_luminosidade = id_medicao_luminosidade;
	}

	public String getData_hora_medicao() {
		return data_hora_medicao;
	}

	public void setData_hora_medicao(String data_hora_medicao) {
		this.data_hora_medicao = data_hora_medicao;
	}

	public String getValor_medicao_luminosidade() {
		return valor_medicao_luminosidade;
	}

	public void setValor_medicao_luminosidade(String valor_medicao_luminosidade) {
		this.valor_medicao_luminosidade = valor_medicao_luminosidade;
	}

}