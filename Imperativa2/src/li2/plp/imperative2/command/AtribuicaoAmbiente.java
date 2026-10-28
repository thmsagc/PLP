package li2.plp.imperative2.command;

import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative1.command.Comando;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative2.memory.AmbienteCompilacaoImperativa2;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;

/**
 * Comando <code>$id := Expressao</code>. Altera a ligacao ativa mais interna
 * da variavel de ambiente; se ela foi criada por um with, a alteracao e
 * descartada quando o with termina.
 */
public class AtribuicaoAmbiente implements Comando {

	private Id id;

	private Expressao expressao;

	public AtribuicaoAmbiente(Id id, Expressao expressao) {
		this.id = id;
		this.expressao = expressao;
	}

	public AmbienteExecucaoImperativa executar(
			AmbienteExecucaoImperativa ambiente)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException {
		AmbienteExecucaoImperativa2 ambienteImperativa2 =
				(AmbienteExecucaoImperativa2) ambiente;
		ambienteImperativa2.changeVariavelAmbiente(id,
				expressao.avaliar(ambienteImperativa2));
		return ambienteImperativa2;
	}

	public boolean checaTipo(AmbienteCompilacaoImperativa ambiente)
			throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException {
		AmbienteCompilacaoImperativa2 ambienteImperativa2 =
				(AmbienteCompilacaoImperativa2) ambiente;
		return expressao.checaTipo(ambienteImperativa2)
				&& ambienteImperativa2.getTipoVariavelAmbiente(id).eIgual(
						expressao.getTipo(ambienteImperativa2));
	}

	@Override
	public String toString() {
		return "$" + id + " := " + expressao;
	}
}
