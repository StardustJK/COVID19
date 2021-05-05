package com.bupt.sse.group7.covid19.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.sse.group7.covid19.R;
import com.bupt.sse.group7.covid19.adapter.TypeChooseAdapter;
import com.bupt.sse.group7.covid19.model.UserTrip;
import com.bupt.sse.group7.covid19.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddTripDialog extends Dialog {
    private static String types[] = {"飞机", "火车", "地铁", "公交车", "出租车", "轮船"};

    private CardView yes, cancel;
    private CancelClickListener cancelClickListener;
    private YesClickListener yesClickListener;
    EditText no, posStart, posEnd, subNo, memo;
    TextView type, date;

    public void setCancelClickListener(CancelClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;
    }

    public void setYesClickListener(YesClickListener yesClickListener) {
        this.yesClickListener = yesClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_trip);
        setCanceledOnTouchOutside(false);

        initView();
    }

    private void initView() {
        yes = findViewById(R.id.yes);
        cancel = findViewById(R.id.cancel);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesClickListener != null) {
                    if (date.getText().toString().equals("") ||
                            type.getText().toString().equals("") ||
                            type.getText().toString().equals("必填") ||
                            no.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "请填写必填信息", Toast.LENGTH_LONG).show();
                    } else {
                        UserTrip userTrip = new UserTrip();
                        userTrip.setNo(no.getText().toString());
                        userTrip.setEndPos(posEnd.getText().toString());
                        userTrip.setStartPos(posStart.getText().toString());
                        userTrip.setNoSub(subNo.getText().toString());
                        userTrip.setMemo(memo.getText().toString());
                        userTrip.setDate(date.getText().toString());
                        userTrip.setType(Constants.typeMap.get(type.getText().toString()));
                        yesClickListener.onYesClick(userTrip);
                    }

                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelClickListener != null) {
                    cancelClickListener.onCancelClick();
                }
            }
        });

        no = findViewById(R.id.no);
        posStart = findViewById(R.id.pos_start);
        posEnd = findViewById(R.id.pos_end);
        subNo = findViewById(R.id.no_sub);
        memo = findViewById(R.id.memo);
        type = findViewById(R.id.type);
        date = findViewById(R.id.date);
        RelativeLayout type_layout = findViewById(R.id.type_layout);
        type_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectType();
            }
        });
        Calendar calendar = Calendar.getInstance();
        //今天的日期
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText(year + "-" + (++month) + "-" + dayOfMonth);
                    }
                };
                DatePickerDialog datePickerDialog=new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT,listener,year,month,day);
                datePickerDialog.show();
            }
        });
    }

    private void selectType() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_trip, null);

        RecyclerView recyclerView = view.findViewById(R.id.trip_dialog);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        TypeChooseAdapter adapter = new TypeChooseAdapter(types);
        recyclerView.setAdapter(adapter);

        BottomSheetDialog typeDialog = new BottomSheetDialog(getContext());
        typeDialog.setContentView(view);
        typeDialog.show();
        adapter.setOnItemClickListener(new TypeChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String text) {
                type.setText(text);
                typeDialog.dismiss();
            }
        });


    }


    public AddTripDialog(@NonNull Context context) {
        super(context);
    }

    public interface CancelClickListener {
        public void onCancelClick();
    }

    public interface YesClickListener {
        public void onYesClick(UserTrip userTrip);
    }
}
