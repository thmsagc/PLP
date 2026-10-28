package li2.plp.imperative2.declaration;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative2.memory.AmbienteCompilacaoImperativa2;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;

/**
 * Declaracao <code>env id : Tipo = Expressao</code>. So aparece no inicio do
 * programa (ver {@link li2.plp.imperative2.Programa}), por isso nao estende
 * {@link li2.plp.imperative1.declaration.Declaracao}.
 */
public class DeclaracaoVariavelAmbiente {

	private Id id;
	private Tipo tipo;
	private Expressao expressao;

	public DeclaracaoVariavelAmbiente(Id id, Tipo tipo, Expressao expressao) {
		this.id = id;
		this.tipo = tipo;
		this.expressao = expressao;
	}

	/**
	 * Liga o id ao valor inicial no nivel da base da pilha de variaveis de
	 * ambiente.
	 */
	public AmbienteExecucaoImperativa2 elabora(
			AmbienteExecucaoImperativa2 ambiente)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException {
		Valor valorInicial = expressao.avaliar(ambiente);
		ambiente.mapVariavelAmbiente(id, valorInicial);
		return ambiente;
	}

	/**
	 * O tipo do valor inicial deve ser igual ao tipo declarado.
	 */
	public boolean checaTipo(AmbienteCompilacaoImperativa2 ambiente)
			throws VariavelJaDeclaradaException, VariavelNaoDeclaradaException {
		if (!expressao.checaTipo(ambiente)
				|| !tipo.eIgual(expressao.getTipo(ambiente))) {
			return false;
		}
		ambiente.mapVariavelAmbiente(id, tipo);
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

	@Override
	public String toString() {
		return "env " + id + " : " + tipo.getNome() + " = " + expressao;
	}
}
