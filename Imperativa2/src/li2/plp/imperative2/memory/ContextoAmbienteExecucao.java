package li2.plp.imperative2.memory;

import java.util.HashMap;
import java.util.Stack;

import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.memory.Contexto;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;

public class ContextoAmbienteExecucao extends Contexto<Valor> {

	public void changeValor(Id idArg, Valor valorId) throws VariavelNaoDeclaradaException {
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
			throw new VariavelNaoDeclaradaException(idArg);
		}
	}
}
