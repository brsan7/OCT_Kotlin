package com.brsan7.oct.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.brsan7.oct.R
import com.brsan7.oct.viewmodels.SelectsDataHoraDialogViewModel
import com.brsan7.oct.model.EventoVO
import java.util.*

class SelectDataDialog : DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var selectsDataHoraDiagViewModel: SelectsDataHoraDialogViewModel
    private lateinit var datePicker: DatePicker
    private lateinit var tipo: String

    companion object{
        private const val EXTRA_TIPO = "tipo"

        fun newInstance(tipo: String): SelectDataDialog {
            val bundle = Bundle()
            bundle.putString(EXTRA_TIPO, tipo)
            val selectFragment = SelectDataDialog()
            selectFragment.arguments = bundle
            return selectFragment
        }
    }

    interface  SelecaoData{
        fun onDataSelecionada(dataSelecionada : EventoVO)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_select_data, null)
        tipo = arguments?.getString(EXTRA_TIPO) ?: ""
        identificarTipo(tipo)
        datePicker = view.findViewById(R.id.dpDiagSelData)

        setupSelectDialogViewModel()

        return AlertDialog.Builder(activity as Activity)
            .setView(view)
            .setNeutralButton(getString(R.string.txt_btnDialogsVoltar),this)
            .setPositiveButton(getString(R.string.txt_btnDialogsSelecionar),this)
            .create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if(which==-1){onClickSelecionar()}

    }

    private fun setupSelectDialogViewModel(){
        selectsDataHoraDiagViewModel = ViewModelProvider(this).get(SelectsDataHoraDialogViewModel::class.java)
        val data = EventoVO(data = "${datePicker.dayOfMonth}/${datePicker.month}/${datePicker.year}")
        selectsDataHoraDiagViewModel.getSelecaoData(data)
        selectsDataHoraDiagViewModel.vmRegDlgData.observe(this, { _data->
            setupData(_data)
        })
    }

    override fun onPause() {
        super.onPause()
        selectsDataHoraDiagViewModel.vmRegDlgData.value?.data =
                "${datePicker.dayOfMonth}/${datePicker.month}/${datePicker.year}"
    }

    private fun setupData(data: EventoVO){
        datePicker.updateDate(
                data.data.split("/")[2].toInt(),//data.ano,
                data.data.split("/")[1].toInt(),//data.mes,
                data.data.split("/")[0].toInt(),//data.dia)
        )
    }

    private fun onClickSelecionar(){
        val data = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH,datePicker.dayOfMonth)
            set(Calendar.MONTH,datePicker.month)
            set(Calendar.YEAR,datePicker.year)
        }
        var diaSemana = when(tipo){
            "Dinâmico" -> {
                var count = 1
                var index = datePicker.dayOfMonth
                while (index > 0){
                    index -= 7
                    if (index > 0){ count++ }
                }
                "${count}*"
            }
            else -> { "" }
        }
        diaSemana += when(data[Calendar.DAY_OF_WEEK]){
            1 -> { getString(R.string.label_rbtnDiagSelDiaSemDom).substring(0..2) }
            2 -> { getString(R.string.label_rbtnDiagSelDiaSemSeg).substring(0..2) }
            3 -> { getString(R.string.label_rbtnDiagSelDiaSemTer).substring(0..2) }
            4 -> { getString(R.string.label_rbtnDiagSelDiaSemQua).substring(0..2) }
            5 -> { getString(R.string.label_rbtnDiagSelDiaSemQui).substring(0..2) }
            6 -> { getString(R.string.label_rbtnDiagSelDiaSemSex).substring(0..2) }
            7 -> { getString(R.string.label_rbtnDiagSelDiaSemSab).substring(0..2) }
            else -> { "" }
        }
        val dataSelecionada = EventoVO(
                data = "$diaSemana, ${datePicker.dayOfMonth}/${datePicker.month+1}/${datePicker.year}"
        )
        (activity as(SelecaoData)).onDataSelecionada(dataSelecionada)
        dismiss()
    }

    private fun identificarTipo(getString: String){
        tipo = if (getString == resources.getStringArray(R.array.array_recorrencias_lembrete)[5] || //Mensal Dinâmico
                getString == resources.getStringArray(R.array.array_recorrencias_lembrete)[7]){ //Anual Dinâmico

            "Dinâmico"
        }
        else { "Fixo" }
    }
}