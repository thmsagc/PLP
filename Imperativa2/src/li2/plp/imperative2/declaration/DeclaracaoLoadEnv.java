package li2.plp.imperative2.declaration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo;
import li2.plp.expressions2.expression.Expressao;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorString;
import li2.plp.expressions2.memory.IdentificadorJaDeclaradoException;
import li2.plp.expressions2.memory.IdentificadorNaoDeclaradoException;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative1.declaration.Declaracao;
import li2.plp.imperative1.memory.AmbienteCompilacaoImperativa;
import li2.plp.imperative1.memory.AmbienteExecucaoImperativa;
import li2.plp.imperative1.memory.EntradaVaziaException;
import li2.plp.imperative2.dotenv.exception.DotenvException;
import li2.plp.imperative2.dotenv.model.ArquivoDotenv;
import li2.plp.imperative2.dotenv.parser.DotenvParser;
import li2.plp.imperative2.dotenv.parser.ParseException;
import li2.plp.imperative2.dotenv.resolver.DotenvInferrer;
import li2.plp.imperative2.memory.AmbienteCompilacaoImperativa2;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;

public class DeclaracaoLoadEnv extends Declaracao {

	private final Expressao caminho;

	public DeclaracaoLoadEnv(Expressao caminho) {
		this.caminho = caminho;
	}

	public Expressao getCaminho() {
		return caminho;
	}

	@Override
	public boolean checaTipo(AmbienteCompilacaoImperativa ambiente)
			throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException, EntradaVaziaException {
		AmbienteCompilacaoImperativa2 amb = (AmbienteCompilacaoImperativa2) ambiente;

		if (!caminho.checaTipo(amb)) {
			return false;
		}

		if (!caminho.getTipo(amb).eIgual(TipoPrimitivo.STRING)) {
			return false;
		}

		String path = extrairCaminhoString(caminho);
		if (path == null) {
			return false;
		}

		File file = new File(path);
		if (!file.exists()) {
			return false;
		}

		try {
			ArquivoDotenv arq = DotenvParser.parse(file);
			DotenvInferrer inferrer = DotenvInferrer.processar(arq);
			for (Map.Entry<Id, Tipo> entry : inferrer.getTipos().entrySet()) {
				Id id = entry.getKey();
				Tipo tipo = entry.getValue();

				try {
					Tipo tipoExistente = amb.getTipoAmbiente(id);
					if (!tipoExistente.eIgual(tipo)) {
						return false;
					}
				} catch (VariavelNaoDeclaradaException e) {
					try {
						amb.mapAmbiente(id, tipo);
					} catch (VariavelJaDeclaradaException ex) {
						return false;
					}
				}
			}
			return true;
		} catch (DotenvException e) {
			return false;
		} catch (ParseException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public AmbienteExecucaoImperativa elabora(AmbienteExecucaoImperativa ambiente)
			throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException, EntradaVaziaException {
		AmbienteExecucaoImperativa2 amb = (AmbienteExecucaoImperativa2) ambiente;

		Valor valorCaminho = caminho.avaliar(amb);
		String path = ((ValorString) valorCaminho).valor();

		File file = new File(path);
		try {
			ArquivoDotenv arq = DotenvParser.parse(file);
			DotenvInferrer inferrer = DotenvInferrer.processar(arq);
			for (Map.Entry<Id, Valor> entry : inferrer.getValores().entrySet()) {
				Id id = entry.getKey();
				Valor valor = entry.getValue();

				try {
					amb.getAmbiente(id);
					amb.changeAmbiente(id, valor);
				} catch (VariavelNaoDeclaradaException e) {
					try {
						amb.mapAmbiente(id, valor);
					} catch (VariavelJaDeclaradaException ex) {
						// Ignora pois se getAmbiente falhou, a variavel nao estava mapeada
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Erro ao carregar arquivo .env na execucao: " + e.getMessage(), e);
		}

		return amb;
	}

	private String extrairCaminhoString(Expressao exp) {
		if (exp instanceof ValorString) {
			return ((ValorString) exp).valor();
		}
		return null;
	}
}

