package application.connector.objects;

public class Log {

	private String idlog;
	private String utilizador;
	private String dataLog;
	private String operacao;

	public Log(String idlog, String utilizador, String dataLog, String operacao) {
		this.idlog = idlog;
		this.utilizador = utilizador;
		this.dataLog = dataLog;
		this.operacao = operacao;
	}

	public String getIdlog() {
		return idlog;
	}

	public void setIdlog(String idlog) {
		this.idlog = idlog;
	}

	public String getUtilizador() {
		return utilizador;
	}

	public void setUtilizador(String utilizador) {
		this.utilizador = utilizador;
	}

	public String getDataLog() {
		return dataLog;
	}

	public void setDataLog(String dataLog) {
		this.dataLog = dataLog;
	}

	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

}
