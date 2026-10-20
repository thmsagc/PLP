package li2.plp.imperative2.memory;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.Contexto;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative1.memory.ContextoCompilacaoImperativa;
import li2.plp.imperative1.memory.ListaValor;

public class ContextoCompilacaoImperativa2
		extends ContextoCompilacaoImperativa
		implements AmbienteCompilacaoImperativa2 {

	private Contexto<Tipo> contextoAmbiente;

	public ContextoCompilacaoImperativa2(ListaValor entrada) {
		super(entrada);
		contextoAmbiente = new Contexto<Tipo>();
		contextoAmbiente.incrementa();
	}

	public void incrementaAmbiente() {
		contextoAmbiente.incrementa();
	}

	public void restauraAmbiente() {
		contextoAmbiente.restaura();
	}

	public void mapAmbiente(Id idArg, Tipo tipoId)
			throws VariavelJaDeclaradaException {
		contextoAmbiente.map(idArg, tipoId);
	}

	public Tipo getTipoAmbiente(Id idArg)
			throws VariavelNaoDeclaradaException {
		return contextoAmbiente.get(idArg);
	}
}
