package li2.plp.imperative2.memory;

import java.util.HashMap;
import java.util.Stack;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.memory.Contexto;

/**
 * Pilha dinamica das variaveis de ambiente. Cada nivel corresponde a um
 * comando <code>with</code> em execucao; o nivel da base guarda os valores
 * iniciais declarados com <code>env</code>.
 */
public class ContextoVariaveisAmbiente extends Contexto<Valor> {

	/**
	 * Altera a ligacao ativa mais interna do id dado.
	 *
	 * @exception VariavelAmbienteNaoDeclaradaException
	 *                se nao existir nenhuma ligacao para o id.
	 */
	public void changeValor(Id idArg, Valor valorId)
			throws VariavelAmbienteNaoDeclaradaException {
		Object result = null;
		Stack<HashMap<Id, Valor>> auxStack = new Stack<HashMap<Id, Valor>>();
		Stack<HashMap<Id, Valor>> stack = this.getPilha();

		while (result == null && !stack.empty()) {
			HashMap<Id, Valor> aux = stack.pop();
			auxStack.push(aux);
			result = aux.get(idArg);
			if (result != null) {
				aux.put(idArg, valorId);
			}
		}

		while (!auxStack.empty()) {
			stack.push(auxStack.pop());
		}

		if (result == null) {
			throw new VariavelAmbienteNaoDeclaradaException(idArg);
		}
	}
}
