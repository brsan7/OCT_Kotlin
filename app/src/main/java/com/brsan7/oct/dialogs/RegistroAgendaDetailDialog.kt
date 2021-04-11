package com.brsan7.oct.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.brsan7.oct.R
import com.brsan7.oct.viewmodels.CalEvtActivityViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay

class RegistroAgendaDetailDialog : DialogFragment(), DialogInterface.OnClickListener {

    private var idEvento = 0
    lateinit var tvRegDetDiagTitulo: TextView
    lateinit var tvRegDetDiagData: TextView
    lateinit var tvRegDetDiagHora: TextView
    lateinit var tvRegDetDiagTipo: TextView
    lateinit var tvRegDetDiagRecorrencia: TextView
    lateinit var tvRegDetDiagDescricao: TextView

    companion object{
        private const val EXTRA_ID = "id"

        fun newInstance(id: Int): RegistroAgendaDetailDialog {
            val bundle = Bundle()
            bundle.putInt(EXTRA_ID, id)
            val selectFragment = RegistroAgendaDetailDialog()
            selectFragment.arguments = bundle
            return selectFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_detail_registro, null)
        idEvento = arguments?.getInt(EXTRA_ID) ?: 0
        setupComponentes(view)
        buscarEvento()

        return AlertDialog.Builder(activity as Activity)
                .setView(view)
                .setNeutralButton("VOLTAR",this)
                .setNegativeButton("EXCLUIR",this)
                .setPositiveButton("EDITAR",this)
                .create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if(which==-1){}

    }

    private fun setupComponentes(view: View){
        tvRegDetDiagTitulo = view.findViewById(R.id.tvRegDetDiagTitulo)
        tvRegDetDiagData = view.findViewById(R.id.tvRegDetDiagData)
        tvRegDetDiagHora = view.findViewById(R.id.tvRegDetDiagHora)
        tvRegDetDiagTipo = view.findViewById(R.id.tvRegDetDiagTipo)
        tvRegDetDiagRecorrencia = view.findViewById(R.id.tvRegDetDiagRecorrencia)
        tvRegDetDiagDescricao = view.findViewById(R.id.tvRegDetDiagDescricao)
    }

    private fun buscarEvento(){
        val lista = CalEvtActivityViewModel().auxTesteSpinner(CalendarDay.today(), false)
        val evento = lista.find { it.id== idEvento }
        tvRegDetDiagTitulo.text = evento?.titulo
        tvRegDetDiagData.text = evento?.data
        tvRegDetDiagHora.text = evento?.hora
        tvRegDetDiagTipo.text = evento?.tipo
        tvRegDetDiagRecorrencia.text = evento?.recorrencia
        tvRegDetDiagDescricao.text = evento?.descricao
    }
}