package com.brsan7.oct

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.brsan7.oct.utils.CalendarUtils
import com.brsan7.oct.viewmodels.CalSolActivityViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class CalendarioSolarActivity : DrawerMenuActivity() {

    private lateinit var calSolActivityViewModel: CalSolActivityViewModel
    private lateinit var spnCalSolarActLocal: Spinner
    private lateinit var tvCalSolarActNascente: TextView
    private lateinit var tvCalSolarActPoente: TextView
    private lateinit var mcvCalSolarAct: MaterialCalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario_solar)

        setupDrawerMenu("Consulta Solar")
        setupComponentes()
        setupCalSolActivityViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        calSolActivityViewModel.reStartActivity = true
    }

    private fun setupComponentes() {

        mcvCalSolarAct = findViewById(R.id.mcvCalSolarAct)
        spnCalSolarActLocal = findViewById(R.id.spnCalSolarActLocal)
        tvCalSolarActNascente = findViewById(R.id.tvCalSolarActNascente)
        tvCalSolarActPoente = findViewById(R.id.tvCalSolarActPoente)
    }
    private fun setupCalSolActivityViewModel() {
        calSolActivityViewModel = ViewModelProvider(this).get(CalSolActivityViewModel::class.java)

        calSolActivityViewModel.getAllLocais()
        calSolActivityViewModel.getAllEstacoesAno()

        calSolActivityViewModel.vmSpnCalSolarActLocal.observe(this, { itensSpinner->
            setupSpinner(itensSpinner)
        })
        calSolActivityViewModel.vmMcvCalSolarAct.observe(this, { estacoes->
            mcvCalSolarAct.addDecorators(
                    CalendarUtils.DayDecorator(this,
                            CalendarDay.from(CalendarDay.today().year,CalendarDay.today().month,estacoes[0].toInt()),"Estações"))

            mcvCalSolarAct.addDecorators(
                    CalendarUtils.DayDecorator(this,
                            CalendarDay.from(CalendarDay.today().year,CalendarDay.today().month,estacoes[1].toInt()),"Estações"))

            mcvCalSolarAct.addDecorators(
                    CalendarUtils.DayDecorator(this,
                            CalendarDay.from(CalendarDay.today().year,CalendarDay.today().month,estacoes[2].toInt()),"Estações"))

            mcvCalSolarAct.addDecorators(
                    CalendarUtils.DayDecorator(this,
                            CalendarDay.from(CalendarDay.today().year,CalendarDay.today().month,estacoes[3].toInt()),"Estações"))
        })
    }

    private fun setupSpinner(itensSpinner: Array<String>){
        val adapterSpnCalSolarActLocal = ArrayAdapter(this, R.layout.item_spinner, itensSpinner)
        adapterSpnCalSolarActLocal.setDropDownViewResource(R.layout.item_spinner)
        spnCalSolarActLocal.adapter = adapterSpnCalSolarActLocal
        spnCalSolarActLocal.onItemSelectedListener = object :
                AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
            override fun onItemSelected(item: AdapterView<*>, view: View?, position: Int, id: Long) {

                tvCalSolarActNascente.text = "IDlocal=${calSolActivityViewModel.getIdRegistro("Local",position)}"
                tvCalSolarActPoente.text = "IDspn=$position"
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //calEvtActivityViewModel.reStartActivity=false
            }
        }
    }
}