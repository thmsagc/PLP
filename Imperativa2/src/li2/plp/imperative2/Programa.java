package li2.plp.imperative2;

import li2.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.EntradaVaziaException;
import li2.plp.imperative1.memory.ErroTipoEntradaException;
import li2.plp.imperative1.memory.ListaValor;
import li2.plp.imperative2.declaration.ListaDeclaracaoVariavelAmbiente;
import li2.plp.imperative2.memory.AmbienteCompilacaoImperativa2;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;

public class Programa {

	/**
	 * Declaracoes <code>env</code> do inicio do programa. A gramatica so
	 * permite declarar variaveis de ambiente aqui.
	 */
	private ListaDeclaracaoVariavelAmbiente declaracoesAmbiente;

	private Comando comando;

	public Programa(Comando comando) {
		this(new ListaDeclaracaoVariavelAmbiente(), comando);
	}

	public Programa(ListaDeclaracaoVariavelAmbiente declaracoesAmbiente,
			Comando comando) {
		this.declaracoesAmbiente = declaracoesAmbiente;
		this.comando = comando;
	}

	/**
	 * Executa o programa.
	 *
	 * @param ambiente
	 *            o ambiente de execu��o.
	 *
	 * @return o ambiente depois de modificado pela execu��o do programa.
	 * @throws ErroTipoEntradaException
	 *
	 * @exception EntradaNaoFornecidaException
	 *                se n�o for fornecida a tail de valores de entrada do
	 *                programa.
	 *
	 */
	public ListaValor executar(AmbienteExecucaoImperativa2 ambiente)
			throws IdentificadorJaDeclaradoException,
			IdentificadorNaoDeclaradoException, EntradaVaziaException, ErroTipoEntradaException {
		ambiente = declaracoesAmbiente.elabora(ambiente);
		return comando.executar(ambiente).getSaida();
	}

	/**
	 * Realiza a verificacao de tipos do programa
	 *
	 * @param ambiente
	 *            o ambiente de compila��o.
	 * @return <code>true</code> se o programa est� bem tipado;
	 *         <code>false</code> caso contrario.
	 *
	 * @exception EntradaNaoFornecidaException
	 *                se n�o for fornecida a tail de valores de entrada do
	 *                programa.
	 *
	 */
	public boolean checaTipo(AmbienteCompilacaoImperativa2 ambiente)
			throws IdentificadorJaDeclaradoException,
			IdentificadorNaoDeclaradoException, EntradaVaziaException {
		return declaracoesAmbiente.checaTipo(ambiente)
				&& comando.checaTipo(ambiente);
	}

	public ListaDeclaracaoVariavelAmbiente getDeclaracoesAmbiente() {
		return declaracoesAmbiente;
	}

}
