package li2.plp.imperative2.dotenv.model;

public class ValorAspado extends ValorDotenv {

	private final boolean aspasSimples;

	public ValorAspado(String valor) {
		this(valor, false);
	}

	public ValorAspado(String valor, boolean aspasSimples) {
		super(valor);
		this.aspasSimples = aspasSimples;
	}

	public boolean isAspasSimples() {
		return aspasSimples;
	}

	@Override
	public boolean isAspado() {
		return true;
	}

	@Override
	public String toString() {
		return aspasSimples ? ("'" + valor + "'") : ("\"" + valor + "\"");
	}
}
