package li2.plp.imperative2.dotenv.resolver;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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

	/**
	 * Trata as sequencias de escape de Java: barra invertida seguida de n, t,
	 * r, b, f, aspas, barra ou u com quatro digitos hexadecimais.
	 * Feito a mao, sem commons-text, porque o TeaVM nao consegue traduzir
	 * o StringEscapeUtils para JavaScript (IDE web).
	 */
	private static String desescapar(String s) {
		if (s == null || s.indexOf('\\') < 0) {
			return s;
		}
		StringBuilder sb = new StringBuilder(s.length());
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c != '\\' || i + 1 == s.length()) {
				sb.append(c);
				continue;
			}
			char e = s.charAt(++i);
			switch (e) {
			case 'n': sb.append('\n'); break;
			case 't': sb.append('\t'); break;
			case 'r': sb.append('\r'); break;
			case 'b': sb.append('\b'); break;
			case 'f': sb.append('\f'); break;
			case 'u':
				if (i + 4 < s.length() && ehHex(s.substring(i + 1, i + 5))) {
					sb.append((char) Integer.parseInt(s.substring(i + 1, i + 5), 16));
					i += 4;
				} else {
					sb.append('\\').append(e);
				}
				break;
			default: sb.append(e); // \" \' \\ e qualquer outro caractere
			}
		}
		return sb.toString();
	}

	private static boolean ehHex(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.digit(s.charAt(i), 16) < 0) {
				return false;
			}
		}
		return true;
	}

	public Map<Id, Tipo> getTipos() {
		return tipos;
	}

	public Map<Id, Valor> getValores() {
		return valores;
	}
}
