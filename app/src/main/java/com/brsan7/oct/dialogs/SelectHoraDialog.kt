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
import com.brsan7.oct.model.EventoVO
import com.brsan7.oct.viewmodels.SelectsDataHoraDialogViewModel

class SelectHoraDialog : DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var selectsDataHoraDiagViewModel: SelectsDataHoraDialogViewModel
    private lateinit var timePicker: TimePicker

    companion object{
        fun newInstance(): SelectHoraDialog {
            return SelectHoraDialog()
        }
    }

    interface  SelecaoHora{
        fun onHoraSelecionada(horaSelecionada : EventoVO)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_select_hora, null)
        timePicker = view.findViewById(R.id.tpDiagSelHora)

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
        val hora = EventoVO( hora = "${timePicker.hour}:${formatTimePickerMinute()}" )
        selectsDataHoraDiagViewModel.getSelecaoHora(hora)
        selectsDataHoraDiagViewModel.vmRegDlgHora.observe(this, { _hora->
            setupHora(_hora)
        })
    }

    override fun onPause() {
        super.onPause()
        selectsDataHoraDiagViewModel.vmRegDlgHora.value?.hora = "${timePicker.hour}:${formatTimePickerMinute()}"
    }

    private fun setupHora(hora: EventoVO){
        timePicker.hour = hora.hora.split(":")[0].toInt()
        timePicker.minute = hora.hora.split(":")[1].toInt()
    }

    private fun onClickSelecionar(){
        val horaSelecionada = EventoVO(
                hora = "${timePicker.hour}:${formatTimePickerMinute()}"
        )
        (activity as(SelecaoHora)).onHoraSelecionada(horaSelecionada)
        dismiss()
    }

    private fun formatTimePickerMinute(): String{
        return if (timePicker.minute < 10){
            "0${timePicker.minute}"
        }
        else { "${timePicker.minute}" }
    }
}