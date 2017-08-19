package com.xbyg_plus.silicon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.fragment.adapter.VideoRVAdapter.RequestFilter;

import java.util.ArrayList;

public class FilterDialog extends Dialog {
    private Runnable optionsSelectedAction;

    private ArrayList<String> categoryPostValueList;
    private ArrayList<String> categoryNameList;
    private ArrayList<String> sortPostValueList;
    private ArrayList<String> sortNameList;
    private ArrayList<String> timePostValueList;
    private ArrayList<String> timeNameList;

    private Button doneBtn;
    private Spinner categorySpinner;
    private Spinner sortSpinner;
    private Spinner timeSpinner;

    public FilterDialog(Context context, RequestFilter requestFilter) {
        super(context);
        setContentView(R.layout.dialog_filter);

        this.categoryPostValueList = requestFilter.categoryMap.getKeyList();
        this.categoryNameList = requestFilter.categoryMap.getValueList();

        this.sortPostValueList = requestFilter.sortMap.getKeyList();
        this.sortNameList = requestFilter.sortMap.getValueList();

        this.timePostValueList = requestFilter.timeMap.getKeyList();
        this.timeNameList = requestFilter.timeMap.getValueList();

        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        categorySpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, categoryNameList));
        categorySpinner.setSelection(categoryPostValueList.indexOf(requestFilter.category));

        sortSpinner = (Spinner) findViewById(R.id.sort_spinner);
        sortSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, sortNameList));
        sortSpinner.setSelection(sortPostValueList.indexOf(requestFilter.sort));

        timeSpinner = (Spinner) findViewById(R.id.time_spinner);
        timeSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, timeNameList));
        timeSpinner.setSelection(timePostValueList.indexOf(requestFilter.time));

        doneBtn = (Button) findViewById(R.id.done);
        doneBtn.setOnClickListener(v -> {
            requestFilter.category = categoryPostValueList.get(categorySpinner.getSelectedItemPosition());
            requestFilter.sort = sortPostValueList.get(sortSpinner.getSelectedItemPosition());
            requestFilter.time = timePostValueList.get(timeSpinner.getSelectedItemPosition());
            dismiss();
            if (optionsSelectedAction != null) {
                optionsSelectedAction.run();
            }
        });
    }

    public FilterDialog setOptionsSelectedAction(Runnable action) {
        this.optionsSelectedAction = action;
        return this;
    }
}
