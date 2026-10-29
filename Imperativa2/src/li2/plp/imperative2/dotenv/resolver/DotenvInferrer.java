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

	private static String desescapar(String s) {
		if (s == null || s.indexOf('\\') == -1) {
			return s;
		}
		StringBuilder sb = new StringBuilder(s.length());
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (c == '\\' && i + 1 < len) {
				char next = s.charAt(++i);
				switch (next) {
				case 'n':
					sb.append('\n');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 't':
					sb.append('\t');
					break;
				case 'b':
					sb.append('\b');
					break;
				case 'f':
					sb.append('\f');
					break;
				case '\\':
					sb.append('\\');
					break;
				case '"':
					sb.append('"');
					break;
				case '\'':
					sb.append('\'');
					break;
				case 'u':
					if (i + 4 < len) {
						String hex = s.substring(i + 1, i + 5);
						try {
							sb.append((char) Integer.parseInt(hex, 16));
							i += 4;
							break;
						} catch (NumberFormatException e) {
							sb.append('\\').append(next);
						}
					} else {
						sb.append('\\').append(next);
					}
					break;
				default:
					sb.append(next);
					break;
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public Map<Id, Tipo> getTipos() {
		return tipos;
	}

	public Map<Id, Valor> getValores() {
		return valores;
	}
}
