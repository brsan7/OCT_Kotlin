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
import java.util.*

class SelectDiasSemanaDialog : DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var rbtnDiagSelDiaSemDom: CheckBox
    private lateinit var rbtnDiagSelDiaSemSeg: CheckBox
    private lateinit var rbtnDiagSelDiaSemTer: CheckBox
    private lateinit var rbtnDiagSelDiaSemQua: CheckBox
    private lateinit var rbtnDiagSelDiaSemQui: CheckBox
    private lateinit var rbtnDiagSelDiaSemSex: CheckBox
    private lateinit var rbtnDiagSelDiaSemSab: CheckBox
    private lateinit var selectionDiasSemana: String
    private lateinit var proxDiaValido: String

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

    private fun composeProxDiaValido() : String{

        proxDiaValido = ""

        var diaJuliano = Calendar.getInstance()[Calendar.DAY_OF_YEAR]
        var ano = Calendar.getInstance()[Calendar.YEAR]
        val isBissexto: Boolean = (( ano % 4 == 0 && ano % 100 != 0 ) || ano % 400 == 0)

        while (proxDiaValido.isEmpty()){
            val data = Calendar.getInstance().apply {
                set(Calendar.YEAR,ano)
                set(Calendar.DAY_OF_YEAR,diaJuliano)
            }
            for (index in selectionDiasSemana.split(",").indices){
                val diaSemana = when(selectionDiasSemana.split(",")[index]){
                    rbtnDiagSelDiaSemDom.text.substring(0..2) -> {1}
                    rbtnDiagSelDiaSemSeg.text.substring(0..2) -> {2}
                    rbtnDiagSelDiaSemTer.text.substring(0..2) -> {3}
                    rbtnDiagSelDiaSemQua.text.substring(0..2) -> {4}
                    rbtnDiagSelDiaSemQui.text.substring(0..2) -> {5}
                    rbtnDiagSelDiaSemSex.text.substring(0..2) -> {6}
                    rbtnDiagSelDiaSemSab.text.substring(0..2) -> {7}
                    else -> {0}
                }
                if (diaSemana == data[Calendar.DAY_OF_WEEK]){
                    val txtDiaSemana = when(data[Calendar.DAY_OF_WEEK]){
                        1 -> { getString(R.string.label_rbtnDiagSelDiaSemDom).substring(0..2) }
                        2 -> { getString(R.string.label_rbtnDiagSelDiaSemSeg).substring(0..2) }
                        3 -> { getString(R.string.label_rbtnDiagSelDiaSemTer).substring(0..2) }
                        4 -> { getString(R.string.label_rbtnDiagSelDiaSemQua).substring(0..2) }
                        5 -> { getString(R.string.label_rbtnDiagSelDiaSemQui).substring(0..2) }
                        6 -> { getString(R.string.label_rbtnDiagSelDiaSemSex).substring(0..2) }
                        7 -> { getString(R.string.label_rbtnDiagSelDiaSemSab).substring(0..2) }
                        else -> { "" }
                    }
                    proxDiaValido = "$txtDiaSemana, ${data[Calendar.DAY_OF_MONTH]}/${data[Calendar.MONTH]+1}/${data[Calendar.YEAR]}"
                }
            }
            diaJuliano++
            if (diaJuliano == 366 && !isBissexto || diaJuliano == 367 && isBissexto){
                diaJuliano = 1
                ano++
            }
        }
        return proxDiaValido
    }

    private fun onClickSelecionar(){
        selectionDiasSemana = composeSelectionDiasSemana()
        if (selectionDiasSemana.isNotEmpty()) {
            proxDiaValido = composeProxDiaValido()
            val diasSemanaSelecionado = EventoVO(
                    data = proxDiaValido,
                    recorrencia = selectionDiasSemana
            )
            (activity as (SelecaoDiasSemana)).onDiasSemanaSelecionado(diasSemanaSelecionado)
            dismiss()
        }
    }
}