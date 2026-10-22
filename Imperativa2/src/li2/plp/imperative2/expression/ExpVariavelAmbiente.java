package li2.plp.imperative2.expression;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.memory.AmbienteCompilacao;
import li2.plp.expressions2.memory.AmbienteExecucao;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative2.memory.AmbienteCompilacaoImperativa2;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;
import li2.plp.functional2.expression.ValorIrredutivel;

public class ExpVariavelAmbiente implements Expressao {

	private Id id;

	public ExpVariavelAmbiente(Id id) {
		this.id = id;
	}

	@Override
	public Valor avaliar(AmbienteExecucao ambiente)
			throws VariavelNaoDeclaradaException,
			VariavelJaDeclaradaException {
		AmbienteExecucaoImperativa2 ambienteImperativa2 =
				(AmbienteExecucaoImperativa2) ambiente;
		return ambienteImperativa2.getAmbiente(id);
	}

	@Override
	public boolean checaTipo(AmbienteCompilacao ambiente)
			throws VariavelNaoDeclaradaException,
			VariavelJaDeclaradaException {
		getTipo(ambiente);
		return true;
	}

	@Override
	public Tipo getTipo(AmbienteCompilacao ambiente)
			throws VariavelNaoDeclaradaException,
			VariavelJaDeclaradaException {
		AmbienteCompilacaoImperativa2 ambienteImperativa2 =
				(AmbienteCompilacaoImperativa2) ambiente;
		return ambienteImperativa2.getTipoAmbiente(id);
	}

	@Override
	public Expressao reduzir(AmbienteExecucao ambiente) {
		try {
			Valor valor = avaliar(ambiente);
			if (valor instanceof ValorIrredutivel) {
				return this;
			}
			return valor.clone();
		} catch (VariavelNaoDeclaradaException e) {
			return this;
		} catch (VariavelJaDeclaradaException e) {
			return this;
		}
	}

	@Override
	public ExpVariavelAmbiente clone() {
		return new ExpVariavelAmbiente(id);
	}

	public Id getId() {
		return id;
	}

	@Override
	public String toString() {
		return "$" + id;
	}
}
