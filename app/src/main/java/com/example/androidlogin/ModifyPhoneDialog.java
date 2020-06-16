package com.example.androidlogin;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ModifyPhoneDialog extends DialogFragment {
    private static final String TAG = "hi";
    private Fragment fragment;

    private static final String pass_key = "phone";

    private FirebaseUser user;
    // 파이어스토어 인증 객체 생성
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private Button positivebutton;
    private Button negativebutton;

    private EditText phone;

    String newPhone ="";

    public ModifyPhoneDialog(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modify2_dialog, container, false);

        positivebutton = view.findViewById(R.id.positivebutton);
        negativebutton = view.findViewById(R.id.negativebutton);

        phone = view.findViewById(R.id.write_phone);


        // 취소 버튼 클릭시 다이얼로그 사라짐
        negativebutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        positivebutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // 로그인 상태일 경우
                if(user != null){
                    newPhone = phone.getText().toString();
                    DocumentReference contact = firebaseFirestore.collection("users").document(user.getEmail());
                    contact.update(pass_key, newPhone)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    getDialog().dismiss();
                                }
                            });
                }
            }

        });

        return view;

    }


    }


