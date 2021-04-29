package com.brsan7.oct.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.brsan7.oct.R
import com.brsan7.oct.RegistroEventoActivity
import com.brsan7.oct.model.EventoVO
import com.brsan7.oct.viewmodels.EvtDetDiagViewModel

class EventoDetailDialog : DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var evtDetDiagViewModel: EvtDetDiagViewModel
    private var idEvento = 0
    private lateinit var tvEvtDetDiagTitulo: TextView
    private lateinit var tvEvtDetDiagData: TextView
    private lateinit var tvEvtDetDiagHora: TextView
    private lateinit var tvEvtDetDiagTipo: TextView
    private lateinit var tvEvtDetDiagRecorrencia: TextView
    private lateinit var tvEvtDetDiagDescricao: TextView

    companion object{
        private const val EXTRA_ID = "id"

        fun newInstance(id: Int): EventoDetailDialog {
            val bundle = Bundle()
            bundle.putInt(EXTRA_ID, id)
            val selectFragment = EventoDetailDialog()
            selectFragment.arguments = bundle
            return selectFragment
        }
    }

    interface  Atualizar{
        fun onDeleteEvento()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_detail_evento, null)
        idEvento = arguments?.getInt(EXTRA_ID) ?: 0
        setupComponentes(view)
        setupEvtDetDiagViewModel()

        return AlertDialog.Builder(activity as Activity)
                .setView(view)
                .setNeutralButton(getString(R.string.txt_btnDialogsVoltar),this)
                .setNegativeButton(getString(R.string.txt_btnDialogsExcluir),this)
                .setPositiveButton(getString(R.string.txt_btnDialogsEditar),this)
                .create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when(which){
            -1 -> {onClickEditar()}//EDITAR
            -2 -> {onClickExcluir()}//EXCLUIR
            -3 -> {}//VOLTAR
        }
    }

    private fun setupComponentes(view: View){

        tvEvtDetDiagTitulo = view.findViewById(R.id.tvEvtDetDiagTitulo)
        tvEvtDetDiagData = view.findViewById(R.id.tvEvtDetDiagData)
        tvEvtDetDiagHora = view.findViewById(R.id.tvEvtDetDiagHora)
        tvEvtDetDiagTipo = view.findViewById(R.id.tvEvtDetDiagTipo)
        tvEvtDetDiagRecorrencia = view.findViewById(R.id.tvEvtDetDiagRecorrencia)
        tvEvtDetDiagDescricao = view.findViewById(R.id.tvEvtDetDiagDescricao)
    }

    private fun setupEvtDetDiagViewModel() {
        evtDetDiagViewModel = ViewModelProvider(this).get(EvtDetDiagViewModel::class.java)
        evtDetDiagViewModel.vmEvtSelecionado.observe(this, { evtSelecionado->
            atualizarEventoSelecionado(evtSelecionado)
        })
        evtDetDiagViewModel.buscarEventoSelecionado(idEvento)
    }

    private fun atualizarEventoSelecionado(evtSelecionado: EventoVO){

        tvEvtDetDiagTitulo.text = evtSelecionado.titulo
        tvEvtDetDiagData.text = evtSelecionado.data
        tvEvtDetDiagHora.text = evtSelecionado.hora
        tvEvtDetDiagTipo.text = evtSelecionado.tipo
        tvEvtDetDiagRecorrencia.text = evtSelecionado.recorrencia
        tvEvtDetDiagDescricao.text = evtSelecionado.descricao
        if (evtSelecionado.hora.isEmpty()){
            tvEvtDetDiagHora.visibility = View.GONE
        }
        if (evtSelecionado.descricao.isEmpty()){
            tvEvtDetDiagDescricao.text = getString(R.string.aviso_SemDescricaoDialogs)
        }
    }

    private fun onClickEditar(){
        val intent = Intent(context, RegistroEventoActivity::class.java)
        intent.putExtra("titulo",getString(R.string.menu1_2_EdtEvt))
        intent.putExtra("id",idEvento)
        startActivity(intent)
    }
    private fun onClickExcluir(){
        evtDetDiagViewModel.deletarEventoSelecionado(idEvento)
        (activity as(Atualizar)).onDeleteEvento()
        dismiss()
    }
}