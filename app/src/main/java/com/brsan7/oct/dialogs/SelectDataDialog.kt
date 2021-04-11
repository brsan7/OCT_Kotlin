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
import com.brsan7.oct.model.DataVO

class SelectDataDialog : DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var selectsDataHoraDiagViewModel: SelectsDataHoraDialogViewModel
    private lateinit var datePicker: DatePicker

    companion object{
        fun newInstance(): SelectDataDialog {
            return SelectDataDialog()
        }
    }

    interface  SelecaoData{
        fun onDataSelecionada(data : DataVO)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_select_data, null)
        datePicker = view.findViewById(R.id.dpDiagSelData)

        setupSelectDialogViewModel()

        return AlertDialog.Builder(activity as Activity)
            .setView(view)
            .setNeutralButton("VOLTAR",this)
            .setPositiveButton("SELECIONAR",this)
            .create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if(which==-1){onClickSelecionar()}

    }

    private fun setupSelectDialogViewModel(){
        selectsDataHoraDiagViewModel = ViewModelProvider(this).get(SelectsDataHoraDialogViewModel::class.java)
        val data = DataVO(datePicker.dayOfMonth,datePicker.month,datePicker.year)
        selectsDataHoraDiagViewModel.getSelecaoData(data)
        selectsDataHoraDiagViewModel.vmRegDlgData.observe(this, { _data->
            setupData(_data)
        })
    }

    override fun onPause() {
        super.onPause()
        selectsDataHoraDiagViewModel.vmRegDlgData.value?.dia = datePicker.dayOfMonth
        selectsDataHoraDiagViewModel.vmRegDlgData.value?.mes = datePicker.month
        selectsDataHoraDiagViewModel.vmRegDlgData.value?.ano = datePicker.year
    }

    private fun setupData(data: DataVO){
        datePicker.updateDate(data.ano,data.mes,data.dia)
    }

    private fun onClickSelecionar(){
        val data = DataVO(
            datePicker.dayOfMonth,
            datePicker.month,
            datePicker.year
        )
        (activity as(SelecaoData)).onDataSelecionada(data)
        dismiss()
    }
}