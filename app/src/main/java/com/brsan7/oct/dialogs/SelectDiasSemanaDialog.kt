package com.brsan7.oct.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.brsan7.oct.R
import com.brsan7.oct.model.EventoVO
import com.brsan7.oct.utils.TempoUtils
import java.util.*

class SelectDiasSemanaDialog : DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var rbtnDiagSelDiaSemDom: CheckBox
    private lateinit var rbtnDiagSelDiaSemSeg: CheckBox
    private lateinit var rbtnDiagSelDiaSemTer: CheckBox
    private lateinit var rbtnDiagSelDiaSemQua: CheckBox
    private lateinit var rbtnDiagSelDiaSemQui: CheckBox
    private lateinit var rbtnDiagSelDiaSemSex: CheckBox
    private lateinit var rbtnDiagSelDiaSemSab: CheckBox

    companion object{
        fun newInstance(): SelectDiasSemanaDialog {
            return SelectDiasSemanaDialog()
        }
    }

    interface  SelecaoDiasSemana{
        fun onDiasSemanaSelecionado(diasSemanaSelecionado : EventoVO)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_select_dias_semana, null)
        rbtnDiagSelDiaSemDom = view.findViewById(R.id.rbtnDiagSelDiaSemDom)
        rbtnDiagSelDiaSemSeg = view.findViewById(R.id.rbtnDiagSelDiaSemSeg)
        rbtnDiagSelDiaSemTer = view.findViewById(R.id.rbtnDiagSelDiaSemTer)
        rbtnDiagSelDiaSemQua = view.findViewById(R.id.rbtnDiagSelDiaSemQua)
        rbtnDiagSelDiaSemQui = view.findViewById(R.id.rbtnDiagSelDiaSemQui)
        rbtnDiagSelDiaSemSex = view.findViewById(R.id.rbtnDiagSelDiaSemSex)
        rbtnDiagSelDiaSemSab = view.findViewById(R.id.rbtnDiagSelDiaSemSab)

        return AlertDialog.Builder(activity as Activity)
            .setView(view)
            .setNeutralButton(getString(R.string.txt_btnDialogsVoltar),this)
            .setPositiveButton(getString(R.string.txt_btnDialogsSelecionar),this)
            .create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if(which==-1){onClickSelecionar()}

    }

    private fun composeSelectionDiasSemana() : String{
        var diasSemana = ""
        if (rbtnDiagSelDiaSemDom.isChecked){ diasSemana += "${rbtnDiagSelDiaSemDom.text.substring(0..2)}," }
        if (rbtnDiagSelDiaSemSeg.isChecked){ diasSemana += "${rbtnDiagSelDiaSemSeg.text.substring(0..2)}," }
        if (rbtnDiagSelDiaSemTer.isChecked){ diasSemana += "${rbtnDiagSelDiaSemTer.text.substring(0..2)}," }
        if (rbtnDiagSelDiaSemQua.isChecked){ diasSemana += "${rbtnDiagSelDiaSemQua.text.substring(0..2)}," }
        if (rbtnDiagSelDiaSemQui.isChecked){ diasSemana += "${rbtnDiagSelDiaSemQui.text.substring(0..2)}," }
        if (rbtnDiagSelDiaSemSex.isChecked){ diasSemana += "${rbtnDiagSelDiaSemSex.text.substring(0..2)}," }
        if (rbtnDiagSelDiaSemSab.isChecked){ diasSemana += "${rbtnDiagSelDiaSemSab.text.substring(0..2)}," }

        return diasSemana
    }

    private fun composeProxDiaValido(selectionDiasSemana: String) : String{

        val data = Calendar.getInstance()
        val hoje = EventoVO(
                data= "${data[Calendar.DAY_OF_MONTH]}/${data[Calendar.MONTH]+1}/${data[Calendar.YEAR]}",
                recorrencia = "projecao_$selectionDiasSemana"
        )
        val dataProjetada = TempoUtils().projecaoSemanalDinamico(hoje)
        return "${TempoUtils().diaSemanaToString(dataProjetada.data)}, ${dataProjetada.data}"
    }

    private fun onClickSelecionar(){
        val selectionDiasSemana = composeSelectionDiasSemana()
        if (selectionDiasSemana.isNotEmpty()) {
            val proxDiaValido = composeProxDiaValido(selectionDiasSemana)
            val diasSemanaSelecionado = EventoVO(
                    data = proxDiaValido,
                    recorrencia = selectionDiasSemana
            )
            (activity as (SelecaoDiasSemana)).onDiasSemanaSelecionado(diasSemanaSelecionado)
            dismiss()
        }
    }
}