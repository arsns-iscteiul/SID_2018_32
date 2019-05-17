package application.connector.objects;

public class VariavelMedida {

	private String variavel_medida_id;
	private String cultura_fk;
	private String variavel_fk;
	private String limite_superior;
	private String limite_inferior;
	private String limiteInferiorLaranja;
	private String limiteInferiorVermelho;
	private String limiteSuperiorLaranja;
	private String limiteSuperiorVermelho;

	public VariavelMedida(String variavel_medida_id, String cultura_fk, String variavel_fk, String limite_superior,
			String limite_inferior, String limiteInferiorLaranja, String limiteInferiorVermelho,
			String limiteSuperiorLaranja, String limiteSuperiorVermelho) {
		this.variavel_medida_id = variavel_medida_id;
		this.cultura_fk = cultura_fk;
		this.variavel_fk = variavel_fk;
		this.limite_superior = limite_superior;
		this.limite_inferior = limite_inferior;
		this.limiteInferiorLaranja = limiteInferiorLaranja;
		this.limiteInferiorVermelho = limiteInferiorVermelho;
		this.limiteSuperiorLaranja = limiteSuperiorLaranja;
		this.limiteSuperiorVermelho = limiteSuperiorVermelho;
	}

	public void setLimiteInferiorLaranja(String limiteInferiorLaranja) {
		this.limiteInferiorLaranja = limiteInferiorLaranja;
	}

	public void setLimiteInferiorVermelho(String limiteInferiorVermelho) {
		this.limiteInferiorVermelho = limiteInferiorVermelho;
	}

	public void setLimiteSuperiorLaranja(String limiteSuperiorLaranja) {
		this.limiteSuperiorLaranja = limiteSuperiorLaranja;
	}

	public void setLimiteSuperiorVermelho(String limiteSuperiorVermelho) {
		this.limiteSuperiorVermelho = limiteSuperiorVermelho;
	}

	public String getVariavel_medida_id() {
		return variavel_medida_id;
	}

	public void setVariavel_medida_id(String variavel_medida_id) {
		this.variavel_medida_id = variavel_medida_id;
	}

	public String getCultura_fk() {
		return cultura_fk;
	}

	public void setCultura_fk(String cultura_fk) {
		this.cultura_fk = cultura_fk;
	}

	public String getVariavel_fk() {
		return variavel_fk;
	}

	public void setVariavel_fk(String variavel_fk) {
		this.variavel_fk = variavel_fk;
	}

	public String getLimite_superior() {
		return limite_superior;
	}

	public void setLimite_superior(String limite_superior) {
		this.limite_superior = limite_superior;
	}

	public String getLimite_inferior() {
		return limite_inferior;
	}

	public void setLimite_inferior(String limite_inferior) {
		this.limite_inferior = limite_inferior;
	}

	public String getLimiteInferiorLaranja() {
		return limiteInferiorLaranja;
	}

	public String getLimiteInferiorVermelho() {
		return limiteInferiorVermelho;
	}

	public String getLimiteSuperiorLaranja() {
		return limiteSuperiorLaranja;
	}

	public String getLimiteSuperiorVermelho() {
		return limiteSuperiorVermelho;
	}
}
