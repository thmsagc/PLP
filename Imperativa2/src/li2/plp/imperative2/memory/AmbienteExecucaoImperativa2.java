package li2.plp.imperative2.memory;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative2.declaration.DefProcedimento;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;

public interface AmbienteExecucaoImperativa2 extends AmbienteExecucaoImperativa {

	public void mapProcedimento(Id idArg, DefProcedimento procedimentoId)
			throws ProcedimentoJaDeclaradoException;

	public DefProcedimento getProcedimento(Id idArg)
			throws ProcedimentoNaoDeclaradoException;

	public void incrementaAmbiente();

	public void restauraAmbiente();

	public void mapAmbiente(Id idArg, Valor valorId)
			throws VariavelJaDeclaradaException;

	public Valor getAmbiente(Id idArg)
			throws VariavelNaoDeclaradaException;

	public void changeAmbiente(Id idArg, Valor valorId)
			throws VariavelNaoDeclaradaException;

}
