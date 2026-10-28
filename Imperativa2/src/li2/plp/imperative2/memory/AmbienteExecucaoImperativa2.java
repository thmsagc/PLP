package li2.plp.imperative2.memory;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative2.declaration.DefProcedimento;

public interface AmbienteExecucaoImperativa2 extends AmbienteExecucaoImperativa {

	public void mapProcedimento(Id idArg, DefProcedimento procedimentoId)
			throws ProcedimentoJaDeclaradoException;

	public DefProcedimento getProcedimento(Id idArg)
			throws ProcedimentoNaoDeclaradoException;

	/**
	 * Empilha um novo nivel de ligacoes de variaveis de ambiente.
	 */
	public void incrementaEscopoAmbiente();

	/**
	 * Desempilha o nivel mais recente de ligacoes de variaveis de ambiente.
	 */
	public void restauraEscopoAmbiente();

	public void mapVariavelAmbiente(Id idArg, Valor valorId)
			throws VariavelAmbienteJaDeclaradaException;

	public Valor getVariavelAmbiente(Id idArg)
			throws VariavelAmbienteNaoDeclaradaException;

	public void changeVariavelAmbiente(Id idArg, Valor valorId)
			throws VariavelAmbienteNaoDeclaradaException;

}
