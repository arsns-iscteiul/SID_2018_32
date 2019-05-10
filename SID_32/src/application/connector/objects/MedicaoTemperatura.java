package application.connector.objects;

public class MedicaoTemperatura {

	private String id_medicao_temperatura;
	private String data_hora_medicao;
	private String valor_medicao_temperatura;

	public MedicaoTemperatura(String id_medicao_temperatura, String data_hora_medicao,
			String valor_medicao_temperatura) {
		super();
		this.id_medicao_temperatura = id_medicao_temperatura;
		this.data_hora_medicao = data_hora_medicao;
		this.valor_medicao_temperatura = valor_medicao_temperatura;
	}

	public String getId_medicao_temperatura() {
		return id_medicao_temperatura;
	}

	public void setId_medicao_temperatura(String id_medicao_temperatura) {
		this.id_medicao_temperatura = id_medicao_temperatura;
	}

	public String getData_hora_medicao() {
		return data_hora_medicao;
	}

	public void setData_hora_medicao(String data_hora_medicao) {
		this.data_hora_medicao = data_hora_medicao;
	}

	public String getValor_medicao_temperatura() {
		return valor_medicao_temperatura;
	}

	public void setValor_medicao_temperatura(String valor_medicao_temperatura) {
		this.valor_medicao_temperatura = valor_medicao_temperatura;
	}

}
