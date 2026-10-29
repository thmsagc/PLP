package plp.debug.imperativa2;

import li2.plp.expressions1.util.Tipo;
import li2.plp.expressions2.expression.Id;
import li2.plp.expressions2.memory.VariavelJaDeclaradaException;
import li2.plp.expressions2.memory.VariavelNaoDeclaradaException;
import li2.plp.imperative1.memory.EntradaVaziaException;
import li2.plp.imperative2.memory.AmbienteCompilacaoImperativa2;
import li2.plp.imperative2.memory.VariavelAmbienteJaDeclaradaException;
import li2.plp.imperative2.memory.VariavelAmbienteNaoDeclaradaException;
import plp.debug.core.InfoEscopo;
import plp.debug.core.ScopeAware;
import plp.debug.core.SnapshotRecorder;

/**
 * Implementa {@link AmbienteCompilacaoImperativa2} delegando todas as
 * chamadas a um ambiente real, sem alterar Imperativa2. Observa
 * incrementa()/restaura()/map() para alimentar um {@link SnapshotRecorder}.
 */
public class AmbienteCompilacaoImperativaDebug implements AmbienteCompilacaoImperativa2, ScopeAware {

	private final AmbienteCompilacaoImperativa2 target;
	private final SnapshotRecorder recorder = new SnapshotRecorder();

	public AmbienteCompilacaoImperativaDebug(AmbienteCompilacaoImperativa2 target) {
		this.target = target;
	}

	public SnapshotRecorder getRecorder() {
		return recorder;
	}

	@Override
	public void registraEscopo(InfoEscopo info) {
		recorder.registraEscopo(info);
	}

	@Override
	public void incrementa() {
		target.incrementa();
		recorder.pushFrame();
	}

	@Override
	public void restaura() {
		target.restaura();
		recorder.popFrame();
	}

	@Override
	public void map(Id idArg, Tipo tipoId) throws VariavelJaDeclaradaException {
		target.map(idArg, tipoId);
		recorder.recordBinding(idArg, tipoId, tipoId == null ? null : tipoId.getNome());
	}

	@Override
	public Tipo get(Id idArg) throws VariavelNaoDeclaradaException {
		return target.get(idArg);
	}

	@Override
	public Tipo getTipoEntrada() throws VariavelNaoDeclaradaException, VariavelJaDeclaradaException, EntradaVaziaException {
		return target.getTipoEntrada();
	}

	@Override
	public void mapVariavelAmbiente(Id idArg, Tipo tipoId) throws VariavelAmbienteJaDeclaradaException {
		target.mapVariavelAmbiente(idArg, tipoId);
	}

	@Override
	public Tipo getTipoVariavelAmbiente(Id idArg) throws VariavelAmbienteNaoDeclaradaException {
		return target.getTipoVariavelAmbiente(idArg);
	}
}
