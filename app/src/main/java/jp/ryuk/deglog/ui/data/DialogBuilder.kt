package jp.ryuk.deglog.ui.data

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jp.ryuk.deglog.R
import jp.ryuk.deglog.database.Todo
import jp.ryuk.deglog.utilities.*

object DialogBuilder {

    fun datePickerDialogBuilder(context: Context, date: Long, unit: (Int, Int, Int) -> Unit): DatePickerDialog {
        return DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                unit(year, month, dayOfMonth)
            },
            date.getYear(),
            date.getMonth(),
            date.getDayOfMonth()
        )
    }

    fun timePickerDialogBuilder(context: Context, date: Long, unit: (Int, Int) -> Unit): TimePickerDialog {
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

    fun createDeleteDiaryDialog(context: Context, unit: () -> Unit): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.dialog_delete_title))
            .setMessage(context.getString(R.string.dialog_delete_message))
            .setNeutralButton(context.getString(R.string.dialog_cancel)) { _, _ -> }
            .setPositiveButton(context.getString(R.string.dialog_yes)) { _, _ ->
                unit()
            }.create()
    }



    @SuppressLint("InflateParams")
    fun createTodoDialog(context: Context, unit: (String) -> Unit): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_todo, null)
        val titleText = view.findViewById<TextView>(R.id.dialog_todo_title)
        val editText = view.findViewById<EditText>(R.id.dialog_todo_text)
        titleText.text = context.getString(R.string.add_new_todo)
        editText.hint = context.getString(R.string.todo)

        val dialog = MaterialAlertDialogBuilder(context)
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

    fun deleteTodoDialogBuilder(
        context: Context, todo: Todo, unit: () -> Unit
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setMessage("選択したToDoを完了します\n\"${todo.todo}\"")
            .setNeutralButton(context.getString(R.string.dialog_cancel), null)
            .setPositiveButton(context.getString(R.string.dialog_success)) { _, _ ->
                unit()
            }
            .create()
    }

    fun selectDashboardDialogBuilder(
        context: Context, list: Array<String>, unit: (String) -> Unit
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.choice_pet))
            .setItems(list) { _, which ->
                unit(list[which])
            }
            .create()
    }

}