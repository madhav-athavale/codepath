package com.codepath.simpletodo;

import android.app.DatePickerDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created by madhavathavale on 9/11/16.
 */
public class TaskEditorFragment extends DialogFragment {

    public  interface OnFinishListener {
          void onComplete(TaskBeingEdited taskBeingEdited);
    }

    EditText editTask;
    Button saveButton;
    Button cancelButton;
    Button dateButton;

    public static String TASK = "TASK";

    private static final String TAG = "TASK_EDITOR_FRAGMENT";
    private OnFinishListener finishListener;
    private TaskBeingEdited taskBeingEdited;


    public TaskEditorFragment() {
    }


    public void setTaskBeingEdited(TaskBeingEdited taskBeingEdited) {
        this.taskBeingEdited = taskBeingEdited;
    }

    public OnFinishListener getFinishListener() {
        return finishListener;
    }

    public void setFinishListener(OnFinishListener mListener) {
        this.finishListener = mListener;
    }

    @Override
    public void onResume() {
        super.onResume();
//        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
//        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);


        int width = getResources().getDisplayMetrics().widthPixels;

        int height = getResources().getDisplayMetrics().heightPixels;

        width = (int) ((double) width * 0.85);
        height = (int) ((double) height * 0.55);
        getDialog().getWindow().setLayout(width, height);


//        Window window = getDialog().getWindow();
//        Point size = new Point();
//        // Store dimensions of the screen in `size`
//        Display display = window.getWindowManager().getDefaultDisplay();
//        display.getSize(size);
//        // Set the width of the dialog proportional to 75% of the screen width
//        window.setLayout((int) (size.x * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
//        window.setGravity(Gravity.CENTER);
//        // Call super onResume after sizing
        super.onResume();



    }

    public static TaskEditorFragment newInstance(OnFinishListener onFinishListener, TaskBeingEdited taskBeingEdited) {

        TaskEditorFragment editFragment = new TaskEditorFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TaskEditorFragment.TASK, taskBeingEdited);
        editFragment.setArguments(bundle);
        editFragment.setTaskBeingEdited(taskBeingEdited);
        editFragment.setFinishListener(onFinishListener);
        return editFragment;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTask = (EditText) view.findViewById(R.id.editTitle);
        saveButton = (Button) view.findViewById(R.id.saveButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);
        dateButton = (Button) view.findViewById(R.id.dateButton);
        final CheckBox chkStatus = (CheckBox) view.findViewById(R.id.chkStatus);
        chkStatus.setChecked(taskBeingEdited.getSimpleTask().isStatusComplete() ? true : false);
        final RadioGroup radioGroupPriority = (RadioGroup) view.findViewById(R.id.priorityGroup);
        RadioButton priorityHigh = (RadioButton) view.findViewById(R.id.highPriority);
        RadioButton priorityLow = (RadioButton) view.findViewById(R.id.lowPriority);
        priorityHigh.setChecked(taskBeingEdited.getSimpleTask().isPriorityHigh() ? true : false);
        priorityLow.setChecked(taskBeingEdited.getSimpleTask().isPriorityHigh() ? false : true);

        Date taskDate = taskBeingEdited.getSimpleTask().getDate();
        if ( taskDate == null){
            taskDate = Calendar.getInstance().getTime();
            taskBeingEdited.getSimpleTask().setDate(taskDate);
        }
        final TextView txtDate = (TextView)view.findViewById(R.id.txtDate);
        txtDate.setText(SimpleTask.FORMAT.format(taskDate));
        String title = taskBeingEdited.getSimpleTask().getTitle();
        editTask.setText(title);
        final Date dateforPicker = taskDate;
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 Calendar calendar = new GregorianCalendar();
                calendar.setTime(dateforPicker);
                 DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        taskBeingEdited.getSimpleTask().setDate(newDate.getTime());
                        txtDate.setText(SimpleTask.FORMAT.format(newDate.getTime()));

                    }

                },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();


            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                taskBeingEdited.setEditCancelled(false);
                String editedItem = editTask.getText().toString();
                taskBeingEdited.getSimpleTask().setStatus(chkStatus.isChecked() == true ? 1 : 0);
                taskBeingEdited.getSimpleTask().setTitle(editedItem);


                int radioId = radioGroupPriority.getCheckedRadioButtonId();

                RadioButton radioButton = (RadioButton) view.findViewById(radioId);
                Log.i(TAG, radioButton.getText().toString());
                Log.i(TAG, getResources().getString(R.string.priority_high));
                if (radioButton.getText().toString().equals(getResources().getString(R.string.priority_high)))
                    taskBeingEdited.getSimpleTask().setPriorityHigh(true);
                else
                    taskBeingEdited.getSimpleTask().setPriorityHigh(false);
                finishListener.onComplete(taskBeingEdited);
                dismiss();


            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskBeingEdited.setEditCancelled(true);
                finishListener.onComplete(taskBeingEdited);
                dismiss();
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return inflater.inflate(R.layout.fragment_edit_item, container);
    }


}
