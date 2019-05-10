package application.connector.objects;

public class Medicao{

	private String id_medicao;
	private String data_hora_medicao;
	private String valor_medicao;
	private String variavel_medida_fk;
	private String more_info;

	public Medicao(String id_medicao, String data_hora_medicao, String valor_medicao, String variavel_medida_fk) {
		this.id_medicao = id_medicao;
		this.data_hora_medicao = data_hora_medicao;
		this.valor_medicao = valor_medicao;
		this.variavel_medida_fk = variavel_medida_fk;
	}

	public String getId_medicao() {
		return id_medicao;
	}

	public void setId_medicao(String id_medicao) {
		this.id_medicao = id_medicao;
	}

	public String getData_hora_medicao() {
		return data_hora_medicao;
	}

	public void setData_hora_medicao(String data_hora_medicao) {
		this.data_hora_medicao = data_hora_medicao;
	}

	public String getValor_medicao() {
		return valor_medicao;
	}

	public void setValor_medicao(String valor_medicao) {
		this.valor_medicao = valor_medicao;
	}

	public String getVariavel_medida_fk() {
		return variavel_medida_fk;
	}

	public void setVariavel_medida_fk(String variavel_medida_fk) {
		this.variavel_medida_fk = variavel_medida_fk;
	}

	public String getMore_info() {
		return more_info;
	}

	public void setMore_info(String more_info) {
		this.more_info = more_info;
	}
}
