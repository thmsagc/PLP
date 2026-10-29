package li2.plp.imperative2.dotenv.model;

import java.util.List;

public class ArquivoDotenv {

	private final ListaEntradaDotenv entradas;

	public ArquivoDotenv() {
		this(new ListaEntradaDotenv());
	}

	public ArquivoDotenv(ListaEntradaDotenv entradas) {
		this.entradas = (entradas != null) ? entradas : new ListaEntradaDotenv();
	}

	public ListaEntradaDotenv getEntradas() {
		return entradas;
	}

	public List<EntradaDotenv> getEntradasComoLista() {
		return entradas.toList();
	}

	@Override
	public String toString() {
		return entradas.toString();
	}
}

