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
import li2.plp.imperative2.dotenv.exception.DotenvException;
import li2.plp.imperative2.dotenv.model.ArquivoDotenv;
import li2.plp.imperative2.dotenv.parser.DotenvParser;
import li2.plp.imperative2.dotenv.parser.ParseException;
import li2.plp.imperative2.dotenv.resolver.DotenvInferrer;
import li2.plp.imperative2.memory.AmbienteCompilacaoImperativa2;
import li2.plp.imperative2.memory.AmbienteExecucaoImperativa2;
import li2.plp.imperative2.memory.VariavelAmbienteJaDeclaradaException;
import li2.plp.imperative2.memory.VariavelAmbienteNaoDeclaradaException;

public class DeclaracaoLoadEnv extends DeclaracaoVariavelAmbiente {

	private final Expressao caminho;

	public DeclaracaoLoadEnv(Expressao caminho) {
		super(null, null, caminho);
		this.caminho = caminho;
	}

	public Expressao getCaminho() {
		return caminho;
	}

	@Override
	public boolean checaTipo(AmbienteCompilacaoImperativa2 ambiente)
			throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException {
		if (!caminho.checaTipo(ambiente)) {
			return false;
		}

		if (!caminho.getTipo(ambiente).eIgual(TipoPrimitivo.STRING)) {
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
					Tipo tipoExistente = ambiente.getTipoVariavelAmbiente(id);
					if (!tipoExistente.eIgual(tipo)) {
						return false;
					}
				} catch (VariavelAmbienteNaoDeclaradaException e) {
					ambiente.mapVariavelDotenv(id, tipo);
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
	public AmbienteExecucaoImperativa2 elabora(AmbienteExecucaoImperativa2 ambiente)
			throws IdentificadorJaDeclaradoException, IdentificadorNaoDeclaradoException {
		Valor valorCaminho = caminho.avaliar(ambiente);
		String path = ((ValorString) valorCaminho).valor();

		File file = new File(path);
		try {
			ArquivoDotenv arq = DotenvParser.parse(file);
			DotenvInferrer inferrer = DotenvInferrer.processar(arq);
			for (Map.Entry<Id, Valor> entry : inferrer.getValores().entrySet()) {
				Id id = entry.getKey();
				Valor valor = entry.getValue();

				try {
					ambiente.getVariavelAmbiente(id);
					ambiente.changeVariavelAmbiente(id, valor);
				} catch (VariavelAmbienteNaoDeclaradaException e) {
					try {
						ambiente.mapVariavelAmbiente(id, valor);
					} catch (VariavelAmbienteJaDeclaradaException ex) {
						// Ignora
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Erro ao carregar arquivo .env na execucao: " + e.getMessage(), e);
		}

		return ambiente;
	}

	private String extrairCaminhoString(Expressao exp) {
		if (exp instanceof ValorString) {
			return ((ValorString) exp).valor();
		}
		return null;
	}

	@Override
	public String toString() {
		return "loadenv " + caminho;
	}
}
