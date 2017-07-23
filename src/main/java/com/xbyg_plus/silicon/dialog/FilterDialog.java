package com.xbyg_plus.silicon.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.xbyg_plus.silicon.R;
import com.xbyg_plus.silicon.adapter.VideoRVAdapter.RequestFilter;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrFrameLayout;

public class FilterDialog extends Dialog {
    private RequestFilter requestFilter;
    private PtrFrameLayout ptrFrame;

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

    public FilterDialog(Context context, RequestFilter requestFilter, PtrFrameLayout ptrFrame) {
        super(context);
        this.requestFilter = requestFilter;
        this.ptrFrame = ptrFrame;

        this.categoryPostValueList = requestFilter.categoryMap.getKeyList();
        this.categoryNameList = requestFilter.categoryMap.getValueList();
        this.sortPostValueList = requestFilter.sortMap.getKeyList();
        this.sortNameList = requestFilter.sortMap.getValueList();
        this.timePostValueList = requestFilter.timeMap.getKeyList();
        this.timeNameList = requestFilter.timeMap.getValueList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_filter);

        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, categoryNameList);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(categoryPostValueList.indexOf(requestFilter.category));

        sortSpinner = (Spinner) findViewById(R.id.sort_spinner);
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, sortNameList);
        sortSpinner.setAdapter(sortAdapter);
        sortSpinner.setSelection(sortPostValueList.indexOf(requestFilter.sort));

        timeSpinner = (Spinner) findViewById(R.id.time_spinner);
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, timeNameList);
        timeSpinner.setAdapter(timeAdapter);
        timeSpinner.setSelection(timePostValueList.indexOf(requestFilter.time));

        doneBtn = (Button) findViewById(R.id.done);
        doneBtn.setOnClickListener(v -> {
            requestFilter.category = categoryPostValueList.get(categorySpinner.getSelectedItemPosition());
            requestFilter.sort = sortPostValueList.get(sortSpinner.getSelectedItemPosition());
            requestFilter.time = timePostValueList.get(timeSpinner.getSelectedItemPosition());
            ptrFrame.autoRefresh(true);
            dismiss();
        });
    }
}
