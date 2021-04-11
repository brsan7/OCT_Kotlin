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
import com.brsan7.oct.model.LocalVO

class LocalEditDialog : DialogFragment(), DialogInterface.OnClickListener {

    private var idLocal = 0
    lateinit var etLocEdDiagLocal: EditText
    lateinit var tvLocEdDiagLatitude: TextView
    lateinit var tvLocEdDiagLongitude: TextView
    lateinit var etLocEdDiagDescricao: EditText

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_edit_local, null)
        idLocal = arguments?.getInt(EXTRA_ID) ?: 0
        setupComponentes(view)
        buscarEvento()

        return AlertDialog.Builder(activity as Activity)
                .setView(view)
                .setNeutralButton("VOLTAR",this)
                .setPositiveButton("EDITAR",this)
                .create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if(which==-1){}
    }

    private fun setupComponentes(view: View){
        etLocEdDiagLocal = view.findViewById(R.id.etLocEdDiagLocal)
        tvLocEdDiagLatitude = view.findViewById(R.id.tvLocEdDiagLatitude)
        tvLocEdDiagLongitude = view.findViewById(R.id.tvLocEdDiagLongitude)
        etLocEdDiagDescricao = view.findViewById(R.id.etLocEdDiagDescricao)
    }
    private fun buscarEvento(){
        val lista = auxTesteDialog()
        val evento = lista[idLocal]
        etLocEdDiagLocal.setText(evento.titulo)
        tvLocEdDiagLatitude.text = evento.latitude
        tvLocEdDiagLongitude.text = evento.longitude
        etLocEdDiagDescricao.setText(evento.descricao)
    }

    private fun auxTesteDialog(): List<LocalVO>{
        val lista: MutableList<LocalVO> = mutableListOf()
        for (index in 0..7) {
            val itemLocal = LocalVO(
                    id = index,
                    titulo = "Local $index",
                    latitude = "-23.123",
                    longitude = "-45.123",
                    descricao = "Teste\ntestando\n123\nTestando"
            )
            lista.add(itemLocal)
        }
        return lista
    }
}