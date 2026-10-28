package li2.plp.imperative2.memory;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Id;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;

public interface AmbienteCompilacaoImperativa2
		extends AmbienteCompilacaoImperativa {

	public void mapVariavelAmbiente(Id idArg, Tipo tipoId)
			throws VariavelAmbienteJaDeclaradaException;

	public Tipo getTipoVariavelAmbiente(Id idArg)
			throws VariavelAmbienteNaoDeclaradaException;
}
