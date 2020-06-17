package jp.ryuk.deglog.ui.data

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jp.ryuk.deglog.R
import jp.ryuk.deglog.database.Todo
import jp.ryuk.deglog.utilities.*

object DialogBuilder {

    /**
     * 選択ダイアログ
     */
    private fun simpleSelectDialog(
        context: Context,
        array: Array<String>,
        unit: (String) -> Unit
    ): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(context)
            .setItems(array) { _, which ->
                unit(array[which])
            }
    }

    fun selectUnitWeightDialogBuilder(context: Context, unit: (String) -> Unit): AlertDialog {
        val items = context.resources.getStringArray(R.array.weight_unit)
        return simpleSelectDialog(context, items, unit)
            .setTitle(context.getString(R.string.unit_of_weight))
            .create()
    }

    fun selectUnitLengthDialogBuilder(context: Context, unit: (String) -> Unit): AlertDialog {
        val items = context.resources.getStringArray(R.array.length_unit)
        return simpleSelectDialog(context, items, unit)
            .setTitle(context.getString(R.string.unit_of_length))
            .create()
    }

    fun selectDashboardDialogBuilder(
        context: Context, array: Array<String>, unit: (String) -> Unit
    ): AlertDialog {
        return simpleSelectDialog(context, array, unit)
            .setTitle(context.getString(R.string.choice_pet))
            .create()
    }

    fun selectIconDialogBuilder(context: Context, unit: (String) -> Unit): AlertDialog {
        val array = context.resources.getStringArray(R.array.icon_select_array)
        return simpleSelectDialog(context, array, unit)
            .setTitle(context.getString(R.string.icon_select))
            .create()
    }

    /**
     * 確認ダイアログ
     */
    fun confirmDeleteDiaryDialogBuilder(context: Context, unit: () -> Unit): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.dialog_delete_title))
            .setMessage(context.getString(R.string.dialog_delete_message))
            .setNeutralButton(context.getString(R.string.dialog_cancel)) { _, _ -> }
            .setPositiveButton(context.getString(R.string.dialog_yes)) { _, _ ->
                unit()
            }.create()
    }

    fun confirmChangeNameDialogBuilder(context: Context, unit: () -> Unit): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.confirm_change_name))
            .setMessage(context.getString(R.string.confirm_change_name_message))
            .setNeutralButton(context.getString(R.string.dialog_cancel)) { _, _ -> }
            .setPositiveButton(context.getString(R.string.dialog_ok)) { _, _ ->
                unit()
            }.create()
    }

    fun confirmDeleteTodoDialogBuilder(
        context: Context, todo: Todo, unit: () -> Unit
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.delete_select_todo))
            .setMessage(todo.todo)
            .setNeutralButton(context.getString(R.string.dialog_cancel), null)
            .setPositiveButton(context.getString(R.string.dialog_success)) { _, _ ->
                unit()
            }.create()
    }

    /**
     * その他ユニーク
     */


    fun typeDialogBuilder(
        context: Context,
        editText: EditText
    ): MaterialAlertDialogBuilder {
        val types = context.resources.getStringArray(R.array.types)
        val typesBig = context.resources.getStringArray(R.array.types_big)
        val typesMedium = context.resources.getStringArray(R.array.types_medium)
        val typesSmall = context.resources.getStringArray(R.array.types_small)
        val typesBird = context.resources.getStringArray(R.array.types_bird)
        val typesEtc = context.resources.getStringArray(R.array.types_etc)

        return MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.choice_type))
            .setItems(types) { _, size ->
                when (size) {
                    0 -> {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.type_small)
                            .setItems(typesSmall) { _, type -> editText.setText(typesSmall[type]) }
                            .show()
                    }
                    1 -> {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.type_medium)
                            .setItems(typesMedium) { _, type -> editText.setText(typesMedium[type]) }
                            .show()
                    }
                    2 -> {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.type_big)
                            .setItems(typesBig) { _, type -> editText.setText(typesBig[type]) }
                            .show()
                    }
                    3 -> {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.type_bird)
                            .setItems(typesBird) { _, type -> editText.setText(typesBird[type]) }
                            .show()
                    }
                    4 -> {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.type_etc)
                            .setItems(typesEtc) { _, type -> editText.setText(typesEtc[type]) }
                            .show()
                    }
                }
            }
    }

    fun datePickerDialogBuilder(
        context: Context,
        date: Long,
        unit: (Int, Int, Int) -> Unit
    ): DatePickerDialog {
        return DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                unit(year, month, dayOfMonth)
            },
            date.getYear(),
            date.getMonth() - 1,
            date.getDayOfMonth()
        )
    }

    fun timePickerDialogBuilder(
        context: Context,
        date: Long,
        unit: (Int, Int) -> Unit
    ): TimePickerDialog {
        return TimePickerDialog(
            context,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                unit(hourOfDay, minute)
            },
            date.getHour(),
            date.getMinute(),
            true
        )
    }


    @SuppressLint("InflateParams")
    fun createTodoDialog(context: Context, unit: (String) -> Unit): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_todo, null)
        val editText = view.findViewById<EditText>(R.id.dialog_todo_text)

        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.add_new_todo))
            .setView(view)
            .setPositiveButton(context.getString(R.string.add)) { _, _ ->
                unit(editText.text.toString())
            }
            .setNeutralButton(context.getString(R.string.dialog_cancel), null)
            .create()

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = !s.isNullOrBlank()
            }
        })

        return dialog
    }


    fun settingFreeDbDialog(
        context: Context,
        isEnable: Boolean,
        itemName: String?,
        unitName: String?,
        unit: (Boolean, String, String) -> Unit
    ): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_setting_free_db, null)
        val switch = view.findViewById<Switch>(R.id.dialog_st_switch)
        val container = view.findViewById<LinearLayout>(R.id.dialog_st_container)
        val itemNameText = view.findViewById<EditText>(R.id.dialog_st_content_text)
        val unitNameText = view.findViewById<EditText>(R.id.dialog_st_unit_text)

        switch.isChecked = isEnable
        container.visibility = if (isEnable) View.VISIBLE else View.GONE
        itemNameText.setText(itemName)
        unitNameText.setText(unitName)

        val dialog =  MaterialAlertDialogBuilder(context)
            .setTitle("データベース設定")
            .setView(view)
            .setPositiveButton("登録") { _, _ ->
                unit(switch.isChecked, itemNameText.text.toString(), unitNameText.text.toString())
            }
            .setNeutralButton(context.getString(R.string.dialog_cancel), null)
            .create()

        var i = !itemName.isNullOrEmpty()
        var u = !unitName.isNullOrEmpty()

        switch.setOnCheckedChangeListener { _, isChecked ->
            container.visibility = if (isChecked) View.VISIBLE else View.GONE

            if (isChecked){
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = i && u
            } else {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
            }

        }

        itemNameText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                i = !s.isNullOrBlank()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = i && u
            }
        })

        unitNameText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                u = !s.isNullOrBlank()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = i && u
            }
        })

        return dialog
    }


}