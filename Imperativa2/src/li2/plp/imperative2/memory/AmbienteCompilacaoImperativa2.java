package li2.plp.imperative2.memory;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Id;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;

public interface AmbienteCompilacaoImperativa2
		extends AmbienteCompilacaoImperativa {

	public void incrementaAmbiente();

	public void restauraAmbiente();

	public void mapAmbiente(Id idArg, Tipo tipoId)
			throws VariavelJaDeclaradaException;

	public Tipo getTipoAmbiente(Id idArg)
			throws VariavelNaoDeclaradaException;
}
