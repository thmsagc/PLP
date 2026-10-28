package li2.plp.imperative2.memory;

import java.util.HashMap;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Id;
import li2.plp.imperative1.memory.ContextoCompilacaoImperativa;
import li2.plp.imperative1.memory.ListaValor;

public class ContextoCompilacaoImperativa2
		extends ContextoCompilacaoImperativa
		implements AmbienteCompilacaoImperativa2 {

	/**
	 * Tipos das variaveis de ambiente. Como elas so podem ser declaradas no
	 * inicio do programa e o with nao muda o tipo, basta um unico nivel.
	 */
	private HashMap<Id, Tipo> tiposAmbiente;

	public ContextoCompilacaoImperativa2(ListaValor entrada) {
		super(entrada);
		tiposAmbiente = new HashMap<Id, Tipo>();
	}

	public void mapVariavelAmbiente(Id idArg, Tipo tipoId)
			throws VariavelAmbienteJaDeclaradaException {
		if (tiposAmbiente.containsKey(idArg)) {
			throw new VariavelAmbienteJaDeclaradaException(idArg);
		}
		tiposAmbiente.put(idArg, tipoId);
	}

	public Tipo getTipoVariavelAmbiente(Id idArg)
			throws VariavelAmbienteNaoDeclaradaException {
		Tipo tipo = tiposAmbiente.get(idArg);
		if (tipo == null) {
			throw new VariavelAmbienteNaoDeclaradaException(idArg);
		}
		return tipo;
	}
}
