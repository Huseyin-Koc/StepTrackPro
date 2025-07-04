package com.example.myapplication2

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var stepList: MutableList<Step>
    private lateinit var taskList: MutableList<Task>
    private lateinit var stepAdapter: StepAdapter
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var stepDatabaseManager: StepDatabaseManager
    private lateinit var taskDatabaseManager: TaskDatabaseManager

    companion object {
        private const val REQUEST_CODE_ADD_STEP = 1
        const val REQUEST_CODE_EDIT_STEP = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1) Önce dili yükle
        loadLocale()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 2) Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 3) Veritabanı yöneticileri
        stepDatabaseManager = StepDatabaseManager(this)
        taskDatabaseManager = TaskDatabaseManager(this)

        // 4) Step ve Task listeleri
        stepList = stepDatabaseManager.getAllSteps().toMutableList()
        taskList = taskDatabaseManager.getAllTasks().toMutableList()

        // 5) Adaptörleri tanımla
        stepAdapter = StepAdapter(stepList, this)
        taskAdapter = TaskAdapter(taskList, this)

        // 6) RecyclerView
        val stepRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        stepRecyclerView.layoutManager = LinearLayoutManager(this)
        stepRecyclerView.adapter = stepAdapter

        // 7) Buton
        val addStepButton = findViewById<Button>(R.id.addStepButton)
        // addStepButton.text = getString(R.string.add_step_button) // (XML'de zaten @string/add_step_button varsa yeterli)

        // 8) Step ekleme
        addStepButton.setOnClickListener {
            val intent = Intent(this, AddStepActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_STEP)
        }
    }

    // ------------------------------------------------------------------
    // Menü
    // ------------------------------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // ------------------------------------------------------------------
    // Menü tıklamaları
    // ------------------------------------------------------------------
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_task -> {
                startActivity(Intent(this, AddTaskActivity::class.java))
                true
            }
            R.id.action_view_tasks -> {
                startActivity(Intent(this, TaskListActivity::class.java))
                true
            }
            R.id.action_view_statistics -> {
                startActivity(Intent(this, StepStatisticsActivity::class.java))
                true
            }
            R.id.action_profile -> {
                startActivity(Intent(this, UserProfileActivity::class.java))
                true
            }
            R.id.action_language -> {
                showLanguageOptions()
                true
            }
            R.id.action_logout -> {
                val logoutIntent = Intent(this, LoginActivity::class.java)
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(logoutIntent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ------------------------------------------------------------------
    // onActivityResult
    // ------------------------------------------------------------------
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CODE_EDIT_STEP -> {
                    val position = data.getIntExtra("position", -1)
                    val stepCount = data.getStringExtra("stepCount")
                    val duration = data.getStringExtra("duration")
                    val date = data.getStringExtra("date")

                    if (position >= 0 && stepCount != null && duration != null && date != null) {
                        stepList[position].apply {
                            this.stepCount = stepCount
                            this.duration = duration
                            this.date = date
                        }
                        stepDatabaseManager.updateStep(
                            stepList[position].stepCount,
                            stepList[position]
                        )
                        stepAdapter.notifyItemChanged(position)
                    }
                }
                REQUEST_CODE_ADD_STEP -> {
                    stepList.clear()
                    stepList.addAll(stepDatabaseManager.getAllSteps())
                    stepAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // Dil seçimi dialog
    // ------------------------------------------------------------------
    private fun showLanguageOptions() {
        val languages = arrayOf(
            getString(R.string.turkish),
            getString(R.string.english)
        )

        val currentLang = getSharedPreferences("LanguagePrefs", MODE_PRIVATE)
            .getString("selected_language", "tr")
        val selectedIndex = if (currentLang == "tr") 0 else 1

        val dialog = android.app.AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.language_selection))
        dialog.setSingleChoiceItems(languages, selectedIndex) { _, which ->
            val selectedLocale = when (which) {
                0 -> Locale("tr")
                1 -> Locale("en")
                else -> Locale.getDefault()
            }
            setLocale(selectedLocale)
        }
        // DİKKAT: "OK" yerine getString(R.string.ok_button_text) kullanalım:
        dialog.setPositiveButton(getString(R.string.ok_button_text)) { d, _ ->
            d.dismiss()
        }
        dialog.show()
    }

    // ------------------------------------------------------------------
    // Locale
    // ------------------------------------------------------------------
    private fun setLocale(locale: Locale) {
        val sp = getSharedPreferences("LanguagePrefs", MODE_PRIVATE)
        sp.edit().putString("selected_language", locale.language).apply()

        val resources: Resources = resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loadLocale() {
        val sp = getSharedPreferences("LanguagePrefs", MODE_PRIVATE)
        val language = sp.getString("selected_language", "tr") // Default: tr
        val locale = Locale(language!!)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
