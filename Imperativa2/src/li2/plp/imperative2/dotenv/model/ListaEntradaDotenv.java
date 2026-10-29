package li2.plp.imperative2.dotenv.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import li2.plp.imperative1.util.Lista;

public class ListaEntradaDotenv extends Lista<EntradaDotenv> {

	public ListaEntradaDotenv() {
		super();
	}

	public ListaEntradaDotenv(EntradaDotenv entrada) {
		super(entrada, null);
	}

	public ListaEntradaDotenv(EntradaDotenv entrada, ListaEntradaDotenv lista) {
		super(entrada, lista);
	}

	public List<EntradaDotenv> toList() {
		List<EntradaDotenv> result = new ArrayList<EntradaDotenv>();
		Lista<EntradaDotenv> atual = this;
		while (atual != null && atual.getHead() != null) {
			result.add(atual.getHead());
			atual = atual.getTail();
		}
		return Collections.unmodifiableList(result);
	}
}

