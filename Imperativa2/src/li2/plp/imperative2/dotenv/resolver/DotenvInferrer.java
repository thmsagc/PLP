package li2.plp.imperative2.dotenv.resolver;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions1.util.TipoPrimitivo;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.expression.Valor;
import li2.plp.expressions2.expression.ValorBooleano;
import li2.plp.expressions2.expression.ValorInteiro;
import li2.plp.expressions2.expression.ValorString;
import li2.plp.imperative2.dotenv.exception.DotenvException;
import li2.plp.imperative2.dotenv.model.ArquivoDotenv;
import li2.plp.imperative2.dotenv.model.EntradaDotenv;
import li2.plp.imperative2.dotenv.model.ValorDotenv;

public class DotenvInferrer {

	private final Map<Id, Tipo> tipos;
	private final Map<Id, Valor> valores;

	private DotenvInferrer(Map<Id, Tipo> tipos, Map<Id, Valor> valores) {
		this.tipos = tipos;
		this.valores = valores;
	}

	public static DotenvInferrer processar(ArquivoDotenv arquivo) throws DotenvException {
		Map<Id, Tipo> mapaTipos = new LinkedHashMap<Id, Tipo>();
		Map<Id, Valor> mapaValores = new LinkedHashMap<Id, Valor>();

		if (arquivo != null && arquivo.getEntradas() != null) {
			for (EntradaDotenv entrada : arquivo.getEntradasComoLista()) {
				Id id = entrada.getId();
				if (mapaTipos.containsKey(id)) {
					throw new DotenvException("Chave duplicada encontrada no arquivo .env: " + id);
				}
				Valor valor = inferirValor(entrada);
				try {
					mapaTipos.put(id, valor.getTipo(null));
				} catch (Exception e) {
					mapaTipos.put(id, TipoPrimitivo.STRING);
				}
				mapaValores.put(id, valor);
			}
		}

		return new DotenvInferrer(Collections.unmodifiableMap(mapaTipos), Collections.unmodifiableMap(mapaValores));
	}

	private static Valor inferirValor(EntradaDotenv entrada) {
		ValorDotenv v = entrada.getValor();
		if (v == null) {
			return new ValorString("");
		}
		if (v.isAspado()) {
			return new ValorString(desescapar(v.getValor()));
		}
		String texto = v.getValor();
		try {
			return new ValorInteiro(Integer.parseInt(texto));
		} catch (NumberFormatException e) {
			// Não é inteiro válido da linguagem, tenta booleano ou string
		}
		if ("true".equalsIgnoreCase(texto)) {
			return new ValorBooleano(true);
		}
		if ("false".equalsIgnoreCase(texto)) {
			return new ValorBooleano(false);
		}
		return new ValorString(texto);
	}

	private static String desescapar(String s) {
		return s == null ? null : StringEscapeUtils.unescapeJava(s);
	}

	public Map<Id, Tipo> getTipos() {
		return tipos;
	}

	public Map<Id, Valor> getValores() {
		return valores;
	}
}
