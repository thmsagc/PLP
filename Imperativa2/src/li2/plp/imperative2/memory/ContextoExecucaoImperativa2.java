package li2.plp.imperative2.memory;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.memory.Contexto;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative1.memory.ContextoExecucaoImperativa;
import li2.plp.imperative1.memory.ListaValor;
import li2.plp.imperative2.declaration.DefProcedimento;

public class ContextoExecucaoImperativa2 extends ContextoExecucaoImperativa
		implements AmbienteExecucaoImperativa2 {

	/**
	 * O contexto de procedimentos faz as vezes de um contexto de execu��o que
	 * armazena apenas procedimentos.
	 */
	private Contexto<DefProcedimento> contextoProcedimentos;

	/**
	 * Contexto separado para as variáveis de ambiente. Nao acompanha
	 * incrementa()/restaura(), que seguem os blocos e as chamadas de
	 * procedimento; so muda com o comando with.
	 */
	private ContextoVariaveisAmbiente contextoAmbiente;

	/**
	 * Construtor da classe.
	 */
	public ContextoExecucaoImperativa2(ListaValor entrada) {
		super(entrada);
		contextoProcedimentos = new Contexto<DefProcedimento>();
		contextoAmbiente = new ContextoVariaveisAmbiente();
		contextoAmbiente.incrementa();
	}

	@Override
	public void incrementa() {
		super.incrementa();
		this.contextoProcedimentos.incrementa();
	}

	@Override
	public void restaura() {
		super.restaura();
		this.contextoProcedimentos.restaura();
	}

	/**
	 * Mapeia o id no procedimento dado.
	 * 
	 * @exception ProcedimentoJaDeclaradoException
	 *                se j� existir um mapeamento do identificador nesta tabela.
	 */
	public void mapProcedimento(Id idArg, DefProcedimento procedimentoId)
			throws ProcedimentoJaDeclaradoException {
		try {
			this.contextoProcedimentos.map(idArg, procedimentoId);
		} catch (VariavelJaDeclaradaException e) {
			throw new ProcedimentoJaDeclaradoException(idArg);
		}

	}

	/**
	 * Retorna o procedimento mapeado ao id dado.
	 * 
	 * @exception ProcedimentoNaoDeclaradoException
	 *                se n�o existir nenhum procedimento mapeado ao id dado
	 *                nesta tabela.
	 */
	public DefProcedimento getProcedimento(Id idArg)
			throws ProcedimentoNaoDeclaradoException {
		try {
			return this.contextoProcedimentos.get(idArg);
		} catch (VariavelNaoDeclaradaException e) {
			throw new ProcedimentoNaoDeclaradoException(idArg);
		}

	}

	public void incrementaEscopoAmbiente() {
		this.contextoAmbiente.incrementa();
	}

	public void restauraEscopoAmbiente() {
		this.contextoAmbiente.restaura();
	}

	public void mapVariavelAmbiente(Id idArg, Valor valorId)
			throws VariavelAmbienteJaDeclaradaException {
		try {
			this.contextoAmbiente.map(idArg, valorId);
		} catch (VariavelJaDeclaradaException e) {
			throw new VariavelAmbienteJaDeclaradaException(idArg);
		}
	}

	public Valor getVariavelAmbiente(Id idArg)
			throws VariavelAmbienteNaoDeclaradaException {
		try {
			return this.contextoAmbiente.get(idArg);
		} catch (VariavelNaoDeclaradaException e) {
			throw new VariavelAmbienteNaoDeclaradaException(idArg);
		}
	}

	public void changeVariavelAmbiente(Id idArg, Valor valorId)
			throws VariavelAmbienteNaoDeclaradaException {
		this.contextoAmbiente.changeValor(idArg, valorId);
	}
}
