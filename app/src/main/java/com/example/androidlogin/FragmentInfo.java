package com.example.androidlogin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class FragmentInfo extends Fragment {

    // 파이어베이스 사용자 객체 생성
    private FirebaseUser user;

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    // 파이어스토어 인증 객체 생성
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    // 작성한 이메일 값과 비밀번호 값을 저장할 객체 생성
    private TextView editTextEmail;
    private TextView editTextPassword;

    // 작성한 이름 값과 전화번호 값을 저장할 객체 생성
    private TextView editTextName;
    private TextView editTextPhone;

    // 수정 및 새로고침 버튼 객체 생성
    private ImageButton btn_modifypw;
    private ImageButton btn_modifypn;
    private ImageButton btn_modifyname;
    private ImageButton btn_refresh;

    // 새로고침 버튼 클릭시 이메일 입력 다이얼로그가 재생성되는 것을 방지하기 위하여 count라는 변수 생성하여 0을 기본값으로 설정함
    private int count=0;

    public FragmentInfo() {
    }

    // 파이어베이스 인증을 onStart에 넣어줌
    @Override public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                // 로그인한 사용자가 있는 경우
                if (user != null) {
                    // 기본값인 count=0일 경우 이메일 입력 다이얼로그를 보여주고 count값을 1로 바꿔줌
                    if(count==0){
                        showDialog();
                        count =1;
                    }
                    // count값이 1인 경우 이메일 입력 다이얼로그를 생략하고 info 메서드를 실행
                    if(count==1){
                        info();
                    }
                }
                // 로그인한 사용자가 없는 경우
                else {
                    Toast.makeText(getActivity(), "로그인을 먼저 진행해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        // id가 write_email인 editText에 대한 메서드 저장
        editTextEmail = view.findViewById(R.id.write_email);
        // id가 signup_password인 editText에 대한 메서드 저장
        editTextPassword = view.findViewById(R.id.signup_password);
        // id가 write_name인 editText에 대한 메서드 저장
        editTextName = view.findViewById(R.id.write_name);
        // id가 write_phone인 editText에 대한 메서드 저장
        editTextPhone = view.findViewById(R.id.write_phone);

        // id가 modifybutton인 버튼에 대한 메서드 저장
        btn_modifypw = view.findViewById(R.id.modifybutton);
        btn_modifypn = view.findViewById(R.id.modifybutton2);
        btn_modifyname = view.findViewById(R.id.modifybutton3);

        // id가 refreshButton인 버튼에 대한 메서드 저장
        btn_refresh = view.findViewById(R.id.refreshButton);

        // 이메일 일치여부가 성공하기 전에 회원 정보를 보여줄 editText를 모두 빈칸 처리해줌
        editTextEmail.setText("");
        editTextName.setText("");
        editTextPhone.setText("");
        editTextPassword.setText("");

        // 수정 이미지 버튼과 새로고침 버튼을 GONE하여 버튼과 그 공간까지 보이지 않게 처리
        // 이메일 일치여부가 성공하여 수정할 수 있는 조건이 되면 VIDIBLE하여 보여줄 예정
        btn_modifypw.setVisibility(View.GONE);
        btn_modifypn.setVisibility(View.GONE);
        btn_modifyname.setVisibility(View.GONE);
        btn_refresh.setVisibility(View.GONE);

        // 비밀번호 수정 이미지 버튼 클릭 시
        btn_modifypw.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // modifyDialog 호출
                ModifyDialog modifyDialog = new ModifyDialog();
                modifyDialog.show(requireActivity().getSupportFragmentManager(), "tag");
            }
        });

        // 전화번호 수정 이미지 버튼 클릭 시
        btn_modifypn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // modifyPhoneDialog 호출
                ModifyPhoneDialog modifyPhoneDialog = new ModifyPhoneDialog();
                modifyPhoneDialog.show(requireActivity().getSupportFragmentManager(), "tag");
            }
        });

        // 이름 수정 이미지 버튼 클릭 시
        btn_modifyname.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // modifyNameDialog 호출
                ModifyNameDialog modifyNameDialog = new ModifyNameDialog();
                modifyNameDialog.show(requireActivity().getSupportFragmentManager(), "tag");
            }
        });

        // 새로고침 이미지 버튼 클릭 시
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // refresh 메서드 실행
                refresh();
            }
        });

        return view;
    }

    // fragment 화면 갱신 메서드
    private void refresh(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.detach(this).attach(this).commit();
    }

    // 회원정보 열람시 호출할 이메일 입력 다이얼로그
    private void showDialog(){

        // 입력하는 이메일 값을 저장할 변수
        final EditText edittext = new EditText(getActivity());
        // 다이얼로그 호출
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),android.R.style.Theme_Material_Light);
        dialog.setMessage("회원정보 열람을 위해 이메일을 다시 한 번 입력해주세요.")
                .setView(edittext)
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       // 로그인 중인 사용자를 불러옴
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        // 다이얼로그에 입력한 이메일 값
                        String email = edittext.getText().toString();
                        // 로그인 중인 사용자의 이메일 값
                        String checkmail = user.getEmail();
                        // 다이얼로그에 입력한 이메일 값과 로그인 중인 사용자의 이메일 값 비교
                        // 같을 때
                        if(email.equals(checkmail)){
                            Toast.makeText(getActivity(),"이메일 확인 성공", Toast.LENGTH_SHORT).show();
                            // "확인" 버튼 클릭시 info 메서드 호출
                            info();

                        }
                        // 다를 때
                        else{
                            // 이메일 일치여부가 성공하기 전에 회원 정보를 보여줄 editText를 모두 빈칸 처리해줌
                            editTextEmail.setText("");
                            editTextName.setText("");
                            editTextPhone.setText("");
                            editTextPassword.setText("");
                            // 수정 이미지 버튼 GONE하여 버튼과 그 공간까지 보이지 않게 처리
                            btn_modifypw.setVisibility(View.GONE);
                            btn_modifypn.setVisibility(View.GONE);
                            btn_modifyname.setVisibility(View.GONE);
                            btn_refresh.setVisibility(View.GONE);

                            Toast.makeText(getActivity(),"이메일이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                })
                .show();

    }

    // info 메서드
   private void info(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // firestore의 collection 경로를  "users"로 설정
        firebaseFirestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // 파이어스토어에서 데이터를 가져오는 것을 성공했을 때
                       if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                assert user != null;
                                if(user.getEmail().equals(document.getData().get("email"))) {
                                    // editText에 파이어스토에 저장된 값을 setText해줌
                                    editTextEmail.setText(document.getData().get("email").toString());
                                    editTextName.setText(document.getData().get("name").toString());
                                    editTextPhone.setText(document.getData().get("phone").toString());
                                    editTextPassword.setText(document.getData().get("password").toString());
                                   // 수정버튼과 새로고침 버튼 VISIBLE 처리하여 보여줌
                                    btn_modifypw.setVisibility(View.VISIBLE);
                                    btn_modifypn.setVisibility(View.VISIBLE);
                                    btn_modifyname.setVisibility(View.VISIBLE);
                                    btn_refresh.setVisibility(View.VISIBLE);

                                }

                            }
                        }
                    }
                });

    }

    }


