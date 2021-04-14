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
import com.brsan7.oct.viewmodels.LocEditDialogViewModel

class LocalEditDialog : DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var locEditDialogViewModel: LocEditDialogViewModel
    private var idLocal = 0
    private lateinit var etLocEdDiagLocal: EditText
    private lateinit var tvLocEdDiagLatitude: TextView
    private lateinit var tvLocEdDiagLongitude: TextView
    private lateinit var tvLocEdDiagFusoHorario: TextView
    private lateinit var etLocEdDiagDescricao: EditText

    companion object{
        private const val EXTRA_ID = "id"

        fun newInstance(id: Int): LocalEditDialog {
            val bundle = Bundle()
            bundle.putInt(EXTRA_ID, id)
            val selectFragment = LocalEditDialog()
            selectFragment.arguments = bundle
            return selectFragment
        }
    }

    interface  Atualizar{
        fun onModifyLocal()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_edit_local, null)
        idLocal = arguments?.getInt(EXTRA_ID) ?: 0
        setupComponentes(view)
        setupLocEditDialogViewModel()

        return AlertDialog.Builder(activity as Activity)
                .setView(view)
                .setNeutralButton("VOLTAR",this)
                .setNegativeButton("EXCLUIR",this)
                .setPositiveButton("EDITAR",this)
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

        etLocEdDiagLocal = view.findViewById(R.id.etLocEdDiagLocal)
        tvLocEdDiagLatitude = view.findViewById(R.id.tvLocEdDiagLatitude)
        tvLocEdDiagLongitude = view.findViewById(R.id.tvLocEdDiagLongitude)
        tvLocEdDiagFusoHorario = view.findViewById(R.id.tvLocEdDiagFusoHorario)
        etLocEdDiagDescricao = view.findViewById(R.id.etLocEdDiagDescricao)
    }

    private fun setupLocEditDialogViewModel() {
        locEditDialogViewModel = ViewModelProvider(this).get(LocEditDialogViewModel::class.java)
        locEditDialogViewModel.vmLocSelecionado.observe(this, { lista->
            atualizarRegistroSelecionado(lista)
        })
        locEditDialogViewModel.buscarLocalSelecionado(idLocal)
    }

    private fun atualizarRegistroSelecionado(listaFiltrada: List<LocalVO>){
        val evento = listaFiltrada.first()
        etLocEdDiagLocal.setText(evento.titulo)
        tvLocEdDiagLatitude.text = evento.latitude
        tvLocEdDiagLongitude.text = evento.longitude
        tvLocEdDiagFusoHorario.text = evento.fusoHorario
        etLocEdDiagDescricao.setText(evento.descricao)
    }

    private fun onClickEditar(){
        val composicaoRegistro = LocalVO(
                idLocal,
                "${etLocEdDiagLocal.text}",
                "${tvLocEdDiagLatitude.text}",
                "${tvLocEdDiagLongitude.text}",
                "${tvLocEdDiagFusoHorario.text}",
                "${etLocEdDiagDescricao.text}"
        )
        locEditDialogViewModel.editarLocalSelecionado(composicaoRegistro)
        (activity as(Atualizar)).onModifyLocal()
        dismiss()
    }
    private fun onClickExcluir(){
        locEditDialogViewModel.deletarLocalSelecionado(idLocal)
        (activity as(Atualizar)).onModifyLocal()
        dismiss()
    }
}