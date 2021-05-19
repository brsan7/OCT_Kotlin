package com.brsan7.oct

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.brsan7.oct.dialogs.EventoDetailDialog
import com.brsan7.oct.utils.CalendarUtils
import com.brsan7.oct.viewmodels.CalEvtActivityViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class CalendarioEventoActivity : DrawerMenuActivity() {

    private lateinit var calEvtActivityViewModel: CalEvtActivityViewModel
    private lateinit var mcvCalEvtAct: MaterialCalendarView
    private lateinit var spnCalEvtActFeriado: Spinner
    private lateinit var spnCalEvtActCompromisso: Spinner
    private lateinit var spnCalEvtActLembrete: Spinner
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario_evento)
        
        setupDrawerMenu(getString(R.string.menu2_1_ConsEvt))
        setupComponentes()
        setupCalEvtActivityViewModel()
        setupCalendario()
    }

    override fun onDestroy() {
        super.onDestroy()
        calEvtActivityViewModel.reStartActivity = true
    }

    private fun setupComponentes(){

        mcvCalEvtAct = findViewById(R.id.mcvCalEvtAct)
        spnCalEvtActFeriado = findViewById(R.id.spnCalEvtActFeriado)
        spnCalEvtActCompromisso = findViewById(R.id.spnCalEvtActCompromisso)
        spnCalEvtActLembrete = findViewById(R.id.spnCalEvtActLembrete)
    }
    
    private fun setupCalEvtActivityViewModel() {
        
        calEvtActivityViewModel = ViewModelProvider(this).get(CalEvtActivityViewModel::class.java)

        calEvtActivityViewModel.getAllDecorateDates()

        calEvtActivityViewModel.getRegistrosDiaSelecionado(calEvtActivityViewModel.diaSelecionado)

        calEvtActivityViewModel.vmMcvCalActFeriado.observe(this, { allFeriados->
            mcvCalEvtAct.addDecorators(CalendarUtils.MultipleEventDecorator(tipo = "Feriado",dates = allFeriados))
            mcvCalEvtAct.addDecorators(CalendarUtils.DayDecorator(this,CalendarDay.today(),tipo = "Hoje"))
        })
        calEvtActivityViewModel.vmMcvCalActCompromisso.observe(this, { allEventos->
            mcvCalEvtAct.addDecorators(CalendarUtils.MultipleEventDecorator(tipo = "Compromisso",dates = allEventos))
            mcvCalEvtAct.addDecorators(CalendarUtils.DayDecorator(this,CalendarDay.today(),tipo = "Hoje"))
        })
        calEvtActivityViewModel.vmMcvCalActLembrete.observe(this, { allLembretes->
            mcvCalEvtAct.addDecorators(CalendarUtils.MultipleEventDecorator(tipo = "Lembrete",dates = allLembretes))
            mcvCalEvtAct.addDecorators(CalendarUtils.DayDecorator(this,CalendarDay.today(),tipo = "Hoje"))
        })
        calEvtActivityViewModel.vmSpnCalActFeriado.observe(this, { itensSpinner->
            atualizarSpinnerFeriado(itensSpinner)
        })
        calEvtActivityViewModel.vmSpnCalActCompromisso.observe(this, { itensSpinner->
            atualizarSpinnerEvento(itensSpinner)
        })
        calEvtActivityViewModel.vmSpnCalActLembrete.observe(this, { itensSpinner->
            atualizarSpinnerLembrete(itensSpinner)
        })
    }

    private fun setupCalendario() {
        
        mcvCalEvtAct.selectedDate = calEvtActivityViewModel.diaSelecionado

        mcvCalEvtAct.setOnDateChangedListener { _, date, _ ->

            calEvtActivityViewModel.reStartActivity = false
            calEvtActivityViewModel.diaSelecionado = date
            calEvtActivityViewModel.idItemSpinners.clear()
            calEvtActivityViewModel.getRegistrosDiaSelecionado(date)
        }
    }

    private fun atualizarSpinnerFeriado(itensSpinner: Array<String>){
        
        val adapterSpnCalActFeriado = ArrayAdapter(this, R.layout.item_spinner_feriado, itensSpinner)
        adapterSpnCalActFeriado.setDropDownViewResource(R.layout.item_spinner)
        spnCalEvtActFeriado.adapter = adapterSpnCalActFeriado
        if (itensSpinner.size == 1){spnCalEvtActFeriado.visibility = View.GONE}
        else{spnCalEvtActFeriado.visibility = View.VISIBLE}
        spnCalEvtActFeriado.onItemSelectedListener = object :
                AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
            override fun onItemSelected(item: AdapterView<*>, view: View?, position: Int, id: Long) {

                if(position > 0 && !calEvtActivityViewModel.reStartActivity) {

                    val fragment = EventoDetailDialog.newInstance(
                            calEvtActivityViewModel.getIdRegistro(tipo = getString(R.string.txt_spnRegEvtActTipoFeriado), index = position)
                    )
                    fragment.show(supportFragmentManager, "dialog")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                calEvtActivityViewModel.reStartActivity=false
            }
        }
    }

    private fun atualizarSpinnerEvento(itensSpinner: Array<String>) {
        
        val adapterSpnCalActEvento = ArrayAdapter(this, R.layout.item_spinner_evento, itensSpinner)
        adapterSpnCalActEvento.setDropDownViewResource(R.layout.item_spinner)
        spnCalEvtActCompromisso.adapter = adapterSpnCalActEvento
        if (itensSpinner.size == 1){spnCalEvtActCompromisso.visibility = View.GONE}
        else{spnCalEvtActCompromisso.visibility = View.VISIBLE}
        spnCalEvtActCompromisso.onItemSelectedListener = object :
                AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
            override fun onItemSelected(item: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(position != 0 && !calEvtActivityViewModel.reStartActivity) {

                    val fragment = EventoDetailDialog.newInstance(
                            calEvtActivityViewModel.getIdRegistro(tipo = getString(R.string.txt_spnRegEvtActTipoCompromisso), index = position)
                    )
                    fragment.show(supportFragmentManager, "dialog")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                calEvtActivityViewModel.reStartActivity=false
            }
        }
    }

    private fun atualizarSpinnerLembrete(itensSpinner: Array<String>) {
        
        val adapterSpnCalActLembrete = ArrayAdapter(this, R.layout.item_spinner_lembrete, itensSpinner)
        adapterSpnCalActLembrete.setDropDownViewResource(R.layout.item_spinner)
        spnCalEvtActLembrete.adapter = adapterSpnCalActLembrete
        if (itensSpinner.size == 1){spnCalEvtActLembrete.visibility = View.GONE}
        else{spnCalEvtActLembrete.visibility = View.VISIBLE}
        spnCalEvtActLembrete.onItemSelectedListener = object :
                AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
            override fun onItemSelected(item: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(position != 0 && !calEvtActivityViewModel.reStartActivity) {

                    val fragment = EventoDetailDialog.newInstance(
                            calEvtActivityViewModel.getIdRegistro(tipo = getString(R.string.txt_spnRegEvtActTipoLembrete), index = position)
                    )
                    fragment.show(supportFragmentManager, "dialog")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                calEvtActivityViewModel.reStartActivity=false
            }
        }
    }
}