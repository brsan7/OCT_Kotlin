package com.brsan7.oct.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.brsan7.oct.R
import com.brsan7.oct.model.LocalVO
import com.brsan7.oct.utils.SolarUtils
import com.brsan7.oct.viewmodels.ConSolDetDiagViewModel

class ConsultaSolarDetailDialog : DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var conSolDetDiagViewModel: ConSolDetDiagViewModel
    private var idEvento = 0
    private var diaJuliano = 0
    private var ano = 0
    private lateinit var tvConSolDetDiagTitulo: TextView
    private lateinit var tvConSolDetDiagNascente: TextView
    private lateinit var tvConSolDetDiagPoente: TextView
    private lateinit var tvConSolDetDiagFotoperiodo: TextView
    private lateinit var tvConSolDetDiagEstacao: TextView

    companion object{
        private const val EXTRA_ID = "id"
        private const val EXTRA_DIA_DO_ANO = "diaJuliano"
        private const val EXTRA_ANO = "ano"

        fun newInstance(id: Int, diaJuliano: Int, ano: Int): ConsultaSolarDetailDialog {
            val bundle = Bundle()
            bundle.putInt(EXTRA_ID, id)
            bundle.putInt(EXTRA_DIA_DO_ANO, diaJuliano)
            bundle.putInt(EXTRA_ANO, ano)
            val selectFragment = ConsultaSolarDetailDialog()
            selectFragment.arguments = bundle
            return selectFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_detail_consulta_solar, null)
        idEvento = arguments?.getInt(EXTRA_ID) ?: 0
        diaJuliano = arguments?.getInt(EXTRA_DIA_DO_ANO) ?: 0
        ano = arguments?.getInt(EXTRA_ANO) ?: 0
        setupComponentes(view)
        setupconSolDetDiagViewModel()

        return AlertDialog.Builder(activity as Activity)
                .setView(view)
                .setNeutralButton("VOLTAR",this)
                .create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int){} //VOLTAR

    private fun setupComponentes(view: View){

        tvConSolDetDiagTitulo = view.findViewById(R.id.tvConSolDetDiagTitulo)
        tvConSolDetDiagNascente = view.findViewById(R.id.tvConSolDetDiagNascente)
        tvConSolDetDiagPoente = view.findViewById(R.id.tvConSolDetDiagPoente)
        tvConSolDetDiagFotoperiodo = view.findViewById(R.id.tvConSolDetDiagFotoperiodo)
        tvConSolDetDiagEstacao = view.findViewById(R.id.tvConSolDetDiagEstacao)
    }

    private fun setupconSolDetDiagViewModel() {
        conSolDetDiagViewModel = ViewModelProvider(this).get(ConSolDetDiagViewModel::class.java)
        conSolDetDiagViewModel.vmLocalSelecionado.observe(this, { lista->
            atualizarEventoSelecionado(lista)
        })
        conSolDetDiagViewModel.buscarLocalSelecionado(idEvento)
    }

    private fun atualizarEventoSelecionado(itemLocalVO: LocalVO){

        val fotoPeriodo = SolarUtils().fotoPeriodo(
                itemLocalVO.latitude.toDouble()+0,
                itemLocalVO.longitude.toDouble()+0,
                itemLocalVO.fusoHorario.toInt()+0,
                diaJuliano+0,
                ano+0)

        tvConSolDetDiagTitulo.text = itemLocalVO.titulo
        tvConSolDetDiagNascente.text = fotoPeriodo[1]
        tvConSolDetDiagPoente.text = fotoPeriodo[2]
        tvConSolDetDiagFotoperiodo.text = fotoPeriodo[0]
        tvConSolDetDiagEstacao.text = SolarUtils().estacaoDoAnoAtual(diaJuliano,ano,itemLocalVO.latitude.toDouble())
    }
}