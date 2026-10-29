package li2.plp.imperative2.declaration;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li2.plp.imperative2.memory.AmbienteCompilacaoImperativa2;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;
import li2.plp.imperative2.memory.VariavelAmbienteJaDeclaradaException;
import li2.plp.imperative2.memory.VariavelAmbienteNaoDeclaradaException;

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
	 * ambiente. Caso a variavel ja tenha sido carregada (por exemplo, via .env),
	 * o valor vindo do .env prevalece.
	 */
	public AmbienteExecucaoImperativa2 elabora(
			AmbienteExecucaoImperativa2 ambiente)
			throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException {
		try {
			// Se ja existe (vindo de loadenv ou flag CLI), mantem o valor vindo do .env!
			ambiente.getVariavelAmbiente(id);
		} catch (VariavelAmbienteNaoDeclaradaException e) {
			Valor valorInicial = expressao.avaliar(ambiente);
			ambiente.mapVariavelAmbiente(id, valorInicial);
		}
		return ambiente;
	}

	/**
	 * O tipo do valor inicial deve ser igual ao tipo declarado.
	 */
	public boolean checaTipo(AmbienteCompilacaoImperativa2 ambiente)
			throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException {
		if (!expressao.checaTipo(ambiente)
				|| !tipo.eIgual(expressao.getTipo(ambiente))) {
			return false;
		}

		try {
			Tipo tipoExistente = ambiente.getTipoVariavelAmbiente(id);
			if (ambiente.ehDoEnv(id)) {
				if (!tipoExistente.eIgual(tipo)) {
					return false;
				}
				ambiente.removerDoEnv(id);
				return true;
			}
			throw new VariavelAmbienteJaDeclaradaException(id);
		} catch (VariavelAmbienteNaoDeclaradaException e) {
			ambiente.mapVariavelAmbiente(id, tipo);
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

	@Override
	public String toString() {
		return "env " + id + " : " + tipo.getNome() + " = " + expressao;
	}
}
