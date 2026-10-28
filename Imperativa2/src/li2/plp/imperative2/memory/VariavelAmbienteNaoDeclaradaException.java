package li2.plp.imperative2.memory;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException;

public class VariavelAmbienteNaoDeclaradaException extends
		IdentificadorNaoDeclaradoException {

	private static final long serialVersionUID = 1L;

	public VariavelAmbienteNaoDeclaradaException(Id id) {
		super("Variavel de ambiente $" + id + " nao declarada.");
	}
}
