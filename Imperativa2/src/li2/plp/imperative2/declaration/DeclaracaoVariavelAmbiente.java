package li2.plp.imperative2.declaration;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative1.declaration.Declaracao;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative1.memory.EntradaVaziaException;
import li2.plp.imperative2.memory.AmbienteCompilacaoImperativa2;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;

public class DeclaracaoVariavelAmbiente extends Declaracao {

	private Id id;
	private Tipo tipo;
	private Expressao expressao;

	public DeclaracaoVariavelAmbiente(Id id, Tipo tipo, Expressao expressao) {
		this.id = id;
		this.tipo = tipo;
		this.expressao = expressao;
	}

	@Override
	public AmbienteExecucaoImperativa elabora(AmbienteExecucaoImperativa ambiente) 
		throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException, EntradaVaziaException {
		AmbienteExecucaoImperativa2 ambienteImperativa2 = (AmbienteExecucaoImperativa2) ambiente;
		try {
			// Se ja existe (vindo de loadenv ou flag), mantem o valor vindo de fora
			ambienteImperativa2.getAmbiente(id);
		} catch (VariavelNaoDeclaradaException e) {
			Valor valorInicial = expressao.avaliar(ambienteImperativa2);
			ambienteImperativa2.mapAmbiente(id, valorInicial);
		}
		return ambienteImperativa2;
	}

	@Override
	public boolean checaTipo(AmbienteCompilacaoImperativa ambiente) 
			throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException, EntradaVaziaException {
		AmbienteCompilacaoImperativa2 ambienteImperativa2 = (AmbienteCompilacaoImperativa2) ambiente;

		if (!expressao.checaTipo(ambienteImperativa2)) {
			return false;
		}

		if (!tipo.eIgual(expressao.getTipo(ambienteImperativa2))) {
			return false;
		}

		try {
			Tipo tipoExistente = ambienteImperativa2.getTipoAmbiente(id);
			if (!tipoExistente.eIgual(tipo)) {
				return false;
			}
		} catch (VariavelNaoDeclaradaException e) {
			ambienteImperativa2.mapAmbiente(id, tipo);
		}

		return true;
	}

	public Id getId() {
		return id;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public Expressao getExpressao() {
		return expressao;
	}
}
