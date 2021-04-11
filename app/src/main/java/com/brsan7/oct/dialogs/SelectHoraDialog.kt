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
import com.brsan7.oct.model.HoraVO

class SelectHoraDialog : DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var selectsDataHoraDiagViewModel: SelectsDataHoraDialogViewModel
    private lateinit var timePicker: TimePicker

    companion object{
        fun newInstance(): SelectHoraDialog {
            return SelectHoraDialog()
        }
    }

    interface  SelecaoHora{
        fun onHoraSelecionada(hora : HoraVO)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_select_hora, null)
        timePicker = view.findViewById(R.id.tpDiagSelHora)

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
        val hora = HoraVO(timePicker.hour,timePicker.minute)
        selectsDataHoraDiagViewModel.getSelecaoHora(hora)
        selectsDataHoraDiagViewModel.vmRegDlgHora.observe(this, { _hora->
            setupHora(_hora)
        })
    }

    override fun onPause() {
        super.onPause()
        selectsDataHoraDiagViewModel.vmRegDlgHora.value?.hora = timePicker.hour
        selectsDataHoraDiagViewModel.vmRegDlgHora.value?.minuto = timePicker.minute
    }

    private fun setupHora(hora: HoraVO){
        timePicker.hour = hora.hora
        timePicker.minute = hora.minuto
    }

    private fun onClickSelecionar(){
        val hora = HoraVO(
            timePicker.hour,
            timePicker.minute
        )
        (activity as(SelecaoHora)).onHoraSelecionada(hora)
        dismiss()
    }
}