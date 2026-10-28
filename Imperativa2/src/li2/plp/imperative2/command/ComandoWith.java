package li2.plp.imperative2.command;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative1.memory.EntradaVaziaException;
import li2.plp.imperative1.memory.ErroTipoEntradaException;
import li2.plp.imperative2.memory.AmbienteCompilacaoImperativa2;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;

/**
 * Comando <code>with id = Expressao do Comando</code>. Cria uma ligacao nova
 * para a variavel de ambiente que vale durante toda a execucao do comando,
 * inclusive dentro dos procedimentos chamados por ele, e a desfaz no fim.
 */
public class ComandoWith implements Comando {

	private Id id;
	private Expressao expressao;
	private Comando comando;

	public ComandoWith(Id id, Expressao expressao, Comando comando) {
		this.id = id;
		this.expressao = expressao;
		this.comando = comando;
	}

	@Override
	public AmbienteExecucaoImperativa executar(
			AmbienteExecucaoImperativa ambiente)
			throws IdentificadorJaDeclaradoException,
			IdentificadorNaoDeclaradoException, EntradaVaziaException,
			ErroTipoEntradaException {
		AmbienteExecucaoImperativa2 ambienteImperativa2 =
				(AmbienteExecucaoImperativa2) ambiente;
		// avaliada antes de empilhar: em "with x = $x + 1" o $x e o externo
		Valor valor = expressao.avaliar(ambienteImperativa2);

		ambienteImperativa2.incrementaEscopoAmbiente();
		try {
			ambienteImperativa2.mapVariavelAmbiente(id, valor);
			return comando.executar(ambienteImperativa2);
		} finally {
			ambienteImperativa2.restauraEscopoAmbiente();
		}
	}

	/**
	 * O id deve ser uma variavel de ambiente declarada e a expressao deve ter
	 * o tipo declarado. Como o tipo nao muda, o comando e checado no mesmo
	 * ambiente.
	 */
	@Override
	public boolean checaTipo(AmbienteCompilacaoImperativa ambiente)
			throws IdentificadorJaDeclaradoException,
			IdentificadorNaoDeclaradoException, EntradaVaziaException {
		AmbienteCompilacaoImperativa2 ambienteImperativa2 =
				(AmbienteCompilacaoImperativa2) ambiente;
		Tipo tipoAmbiente = ambienteImperativa2.getTipoVariavelAmbiente(id);

		return expressao.checaTipo(ambienteImperativa2)
				&& tipoAmbiente.eIgual(expressao.getTipo(ambienteImperativa2))
				&& comando.checaTipo(ambienteImperativa2);
	}
}
