package li2.plp.imperative2.declaration;

import li2.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li2.plp.imperative1.util.Lista;
import li2.plp.imperative2.memory.AmbienteCompilacaoImperativa2;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;

/**
 * Lista das declaracoes <code>env</code> do inicio do programa, na ordem em
 * que aparecem no codigo. Uma declaracao pode usar as anteriores.
 */
public class ListaDeclaracaoVariavelAmbiente extends
		Lista<DeclaracaoVariavelAmbiente> {

	public ListaDeclaracaoVariavelAmbiente() {
	}

	public ListaDeclaracaoVariavelAmbiente(
			DeclaracaoVariavelAmbiente declaracao) {
		super(declaracao, null);
	}

	public ListaDeclaracaoVariavelAmbiente(
			DeclaracaoVariavelAmbiente declaracao,
			ListaDeclaracaoVariavelAmbiente listaDeclaracao) {
		super(declaracao, listaDeclaracao);
	}

	public boolean checaTipo(AmbienteCompilacaoImperativa2 ambiente)
			throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException {
		boolean resposta;
		if (getHead() != null) {
			if (getTail() != null) {
				resposta = getHead().checaTipo(ambiente)
						&& ((ListaDeclaracaoVariavelAmbiente) getTail())
								.checaTipo(ambiente);
			} else {
				resposta = getHead().checaTipo(ambiente);
			}
		} else {
			resposta = true;
		}
		return resposta;
	}

	public AmbienteExecucaoImperativa2 elabora(
			AmbienteExecucaoImperativa2 ambiente)
			throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException {
		AmbienteExecucaoImperativa2 resposta;
		if (getHead() != null) {
			if (getTail() != null) {
				resposta = ((ListaDeclaracaoVariavelAmbiente) getTail())
						.elabora(getHead().elabora(ambiente));
			} else {
				resposta = getHead().elabora(ambiente);
			}
		} else {
			resposta = ambiente;
		}
		return resposta;
	}
}
