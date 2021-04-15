package com.brsan7.oct

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.brsan7.oct.model.LocalVO
import com.brsan7.oct.utils.CalendarUtils
import com.brsan7.oct.utils.SolarUtils
import com.brsan7.oct.viewmodels.CalSolActivityViewModel
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.util.*

class CalendarioSolarActivity : DrawerMenuActivity() {

    private lateinit var calSolActivityViewModel: CalSolActivityViewModel
    private lateinit var spnCalSolarActLocal: Spinner
    private lateinit var tvCalSolarActNascente: TextView
    private lateinit var tvCalSolarActPoente: TextView
    private lateinit var mcvCalSolarAct: MaterialCalendarView
    private lateinit var localSelecionado: LocalVO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendario_solar)

        setupDrawerMenu("Consulta Solar")
        setupComponentes()
        setupCalSolActivityViewModel()
        setupCalendario()
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


        calSolActivityViewModel.vmSpnCalSolarActLocal.observe(this, { itensSpinner->
            setupSpinner(itensSpinner)
        })
        calSolActivityViewModel.vmCalSolarActDadosLocal.observe(this, { itemLocalVo->
            atualizarDadosDoLocal(itemLocalVo)
            localSelecionado = itemLocalVo
            calSolActivityViewModel.getAllEstacoesAno()
        })
        calSolActivityViewModel.vmMcvCalSolarAct.observe(this, { datasEstacoesDoAno->
            mcvCalSolarAct.addDecorators(
                    CalendarUtils.DayDecorator(this,datasEstacoesDoAno[0],localSelecionado.latitude)
            )
            mcvCalSolarAct.addDecorators(
                    CalendarUtils.DayDecorator(this,datasEstacoesDoAno[1],localSelecionado.latitude)
            )
            mcvCalSolarAct.addDecorators(
                    CalendarUtils.DayDecorator(this,datasEstacoesDoAno[2],localSelecionado.latitude)
            )
            mcvCalSolarAct.addDecorators(
                    CalendarUtils.DayDecorator(this,datasEstacoesDoAno[3],localSelecionado.latitude)
            )
        })
    }

    private fun setupSpinner(itensSpinner: Array<String>){
        val adapterSpnCalSolarActLocal = ArrayAdapter(this, R.layout.item_spinner, itensSpinner)
        adapterSpnCalSolarActLocal.setDropDownViewResource(R.layout.item_spinner)
        spnCalSolarActLocal.adapter = adapterSpnCalSolarActLocal
        spnCalSolarActLocal.onItemSelectedListener = object :
                AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
            override fun onItemSelected(item: AdapterView<*>, view: View?, position: Int, id: Long) {

                calSolActivityViewModel.getDadosLocalSelecionado(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //calEvtActivityViewModel.reStartActivity=false
            }
        }
    }

    private fun setupCalendario() {

        mcvCalSolarAct.selectedDate = calSolActivityViewModel.diaSelecionado

        mcvCalSolarAct.setOnDateChangedListener { widget, date, selected ->

            calSolActivityViewModel.reStartActivity=false
            calSolActivityViewModel.diaSelecionado = date
            calSolActivityViewModel.idItemSpinners.clear()
            atualizarDadosDoLocal(calSolActivityViewModel.vmCalSolarActDadosLocal.value ?: LocalVO())
        }
    }

    private fun atualizarDadosDoLocal(itemLocalVO: LocalVO){
        val dataSelecionada = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH,calSolActivityViewModel.diaSelecionado.day)
            set(Calendar.MONTH,calSolActivityViewModel.diaSelecionado.month-1)
            set(Calendar.YEAR,calSolActivityViewModel.diaSelecionado.year)
        }

        val fotoPeriodo = SolarUtils().fotoPeriodo(
                itemLocalVO.latitude.toDouble()+0,
                itemLocalVO.longitude.toDouble()+0,
                itemLocalVO.fusoHorario.toInt()+0,
                dataSelecionada[Calendar.DAY_OF_YEAR]+0,
                dataSelecionada[Calendar.YEAR]+0)

        tvCalSolarActNascente.text = fotoPeriodo[1]
        tvCalSolarActPoente.text = fotoPeriodo[2]
    }

}