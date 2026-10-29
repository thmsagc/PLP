package li2.plp.imperative2.dotenv.model;

import li2.plp.expressions2.expression.Id;

public class EntradaDotenv {

	private final Id id;
	private final ValorDotenv valor;

	public EntradaDotenv(Id id, ValorDotenv valor) {
		this.id = id;
		this.valor = valor;
	}

	public EntradaDotenv(Id id) {
		this(id, null);
	}

	public Id getId() {
		return id;
	}

	public ValorDotenv getValor() {
		return valor;
	}

	public boolean temValor() {
		return valor != null;
	}

	public String getValorTexto() {
		return valor != null ? valor.getValor() : "";
	}

	@Override
	public String toString() {
		if (valor == null) {
			return id.toString() + "=";
		}
		return id.toString() + "=" + valor.toString();
	}
}

