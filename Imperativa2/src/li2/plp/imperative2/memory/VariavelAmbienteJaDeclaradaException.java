package li2.plp.imperative2.memory;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.IdentificadorJaDeclaradoException;

public class VariavelAmbienteJaDeclaradaException extends
		IdentificadorJaDeclaradoException {

	private static final long serialVersionUID = 1L;

	public VariavelAmbienteJaDeclaradaException(Id id) {
		super("Variavel de ambiente $" + id + " ja declarada.");
	}
}
