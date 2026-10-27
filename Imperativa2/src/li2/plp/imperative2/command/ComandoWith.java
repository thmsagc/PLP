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
		Valor valor = expressao.avaliar(ambienteImperativa2);

		ambienteImperativa2.incrementaAmbiente();
		try {
			ambienteImperativa2.mapAmbiente(id, valor);
			return comando.executar(ambienteImperativa2);
		} finally {
			ambienteImperativa2.restauraAmbiente();
		}
	}

	@Override
	public boolean checaTipo(AmbienteCompilacaoImperativa ambiente)
			throws IdentificadorJaDeclaradoException,
			IdentificadorNaoDeclaradoException, EntradaVaziaException {
		AmbienteCompilacaoImperativa2 ambienteImperativa2 =
				(AmbienteCompilacaoImperativa2) ambiente;
		Tipo tipoAmbiente = ambienteImperativa2.getTipoAmbiente(id);

		if (!expressao.checaTipo(ambienteImperativa2)
				|| !tipoAmbiente.eIgual(
						expressao.getTipo(ambienteImperativa2))) {
			return false;
		}

		ambienteImperativa2.incrementaAmbiente();
		try {
			ambienteImperativa2.mapAmbiente(id, tipoAmbiente);
			return comando.checaTipo(ambienteImperativa2);
		} finally {
			ambienteImperativa2.restauraAmbiente();
		}
	}
}
