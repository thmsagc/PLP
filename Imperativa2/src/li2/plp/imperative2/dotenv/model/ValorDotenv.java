package li2.plp.imperative2.dotenv.model;

public abstract class ValorDotenv {

	protected final String valor;

	public ValorDotenv(String valor) {
		this.valor = (valor != null) ? valor : "";
	}

	public String getValor() {
		return valor;
	}

	public abstract boolean isAspado();

	@Override
	public String toString() {
		return valor;
	}
}

